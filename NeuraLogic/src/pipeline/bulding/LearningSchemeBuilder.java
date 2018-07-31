package pipeline.bulding;

import constructs.example.LogicSample;
import constructs.template.Template;
import ida.utils.tuples.Pair;
import learning.crossvalidation.TrainTestResults;
import pipeline.Pipe;
import pipeline.Pipeline;
import pipeline.bulding.pipes.SamplesProcessor;
import pipeline.bulding.pipes.TemplateProcessor;
import pipeline.prepared.pipes.generic.DuplicateBranch;
import pipeline.prepared.pipes.generic.PairMerge;
import pipeline.prepared.pipes.generic.SecondFromPairPipe;
import settings.Settings;
import settings.Source;
import settings.Sources;
import training.NeuralModel;
import training.results.Results;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class LearningSchemeBuilder extends AbstractPipelineBuilder<Sources, Results> {
    private static final Logger LOG = Logger.getLogger(LearningSchemeBuilder.class.getName());
    private Sources sources;

    public LearningSchemeBuilder(Settings settings, Sources sources) {
        super(settings);
        this.sources = sources;
    }

    @Override
    public Pipeline<Sources, Results> buildPipeline() {
        return buildPipeline(this.sources);
    }

    public Pipeline<Sources, Results> buildPipeline(Sources sources) {
        Pipeline<Sources, Results> pipeline = new Pipeline<>("LearningSchemePipeline");

        //based on provided sources (samples) decide the learning mode
        if (sources.crossvalidation) {
            CrossvalidationBuilder crossvalidationSchemeBuilder = new CrossvalidationBuilder(settings);
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            crossvalidationSchemeBuilder.trainTestBuilder = trainTestBuilder;
            Pipeline<Sources, Results> sourcesResultsPipeline = crossvalidationSchemeBuilder.buildPipeline(sources);
            return sourcesResultsPipeline;
        } else if (sources.trainTest) {
            TrainTestBuilder trainTestBuilder = new TrainTestBuilder(settings);
            Pipeline<Sources, TrainTestResults> pipeline2 = trainTestBuilder.buildPipeline(sources);
        } else if (sources.trainOnly) {
            DuplicateBranch<Sources> duplicateBranch = pipeline.registerStart(new DuplicateBranch<>());
            Pipe<Sources, Source> sourcesSourcePipe = pipeline.register(new Pipe<Sources, Source>("sourcesSourcePipe") {
                @Override
                public Source apply(Sources sources) {
                    return sources.train;
                }
            });
            duplicateBranch.connectAfterL(sourcesSourcePipe);
            SamplesProcessor trainingSamplesProcessor = new SamplesProcessor(settings, sources.train);
            Pipeline<Source, Stream<LogicSample>> sourcesStreamPipeline = pipeline.register(trainingSamplesProcessor.buildPipeline());
            sourcesSourcePipe.connectAfter(sourcesStreamPipeline);

            LearningBuilder learningBuilder = new LearningBuilder(settings);
            if (sources.templateProvided) {
                TemplateProcessor templateProcessor = new TemplateProcessor(settings, sources);
                Pipeline<Sources, Template> sourcesTemplatePipeline = pipeline.register(templateProcessor.buildPipeline());
                duplicateBranch.connectAfterR(sourcesTemplatePipeline);
                PairMerge<Template, Stream<LogicSample>> pairMerge = pipeline.register(new PairMerge<>());
                LearningBuilder.NormalLearningBuilder normalLearningBuilder = learningBuilder.new NormalLearningBuilder(settings);
                Pipeline<Pair<Template, Stream<LogicSample>>, Pair<NeuralModel, Results>> pairPairPipeline = pipeline.register(normalLearningBuilder.buildPipeline());
                pairMerge.connectAfter(pairPairPipeline);
                SecondFromPairPipe<NeuralModel, Results> secondFromPairPipe = pipeline.registerEnd(new SecondFromPairPipe<>());
                pairPairPipeline.connectAfter(secondFromPairPipe);
            } else {
                LearningBuilder.StructureLearningBuilder structureLearningBuilder = learningBuilder.new StructureLearningBuilder(settings);
                Pipeline<Stream<LogicSample>, Pair<Template, Results>> streamPairPipeline = pipeline.register(structureLearningBuilder.buildPipeline());
            }

        } else if (sources.testOnly) {
            TestingBuilder testingBuilder = new TestingBuilder(settings);
            testingBuilder.buildPipeline(sources);
        } else {
            LOG.warning("Invalid learning mode setting.");
        }
        return pipeline;
    }
}