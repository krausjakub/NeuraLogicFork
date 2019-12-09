package networks.computation.training.debugging;

import constructs.building.debugging.TemplateDebugger;
import constructs.template.Template;
import networks.computation.evaluation.results.Progress;
import networks.computation.training.NeuralModel;
import pipelines.Pipe;
import pipelines.Pipeline;
import pipelines.pipes.generic.FirstFromPairPipe;
import pipelines.pipes.generic.StreamifyPipe;
import settings.Settings;
import settings.Sources;
import utils.generic.Pair;

import java.util.logging.Logger;
import java.util.stream.Stream;

public class TrainingDebugger extends TemplateDebugger {
    private static final Logger LOG = Logger.getLogger(TrainingDebugger.class.getName());

    public TrainingDebugger(String[] args, Settings settings) {
        super(args, settings);
        if (intermediateDebug){
            settings.debugPipeline = true;
            settings.debugTemplate = true;//todo compress
            settings.debugSampleTraining = true;
            settings.debugGrounding = true;
        }
    }

    public TrainingDebugger(Sources sources, Settings settings) {
        super(sources, settings);
        if (intermediateDebug){
            settings.debugPipeline = true;
            settings.debugTemplate = true;
            settings.debugSampleTraining = true;
            settings.debugGrounding = true;
        }
    }

    public TrainingDebugger(Settings settings) {
        super(settings);
        if (intermediateDebug){
            settings.debugPipeline = true;
            settings.debugTemplate = true;
            settings.debugSampleTraining = true;
            settings.debugGrounding = true;
        }
    }

    /**
     * Build whole training and return learned template
     *
     * @return
     */
    @Override
    public Pipeline<Sources, Stream<Template>> buildPipeline() {
        Pipeline<Sources, Pair<Pair<Template, NeuralModel>, Progress>> sourcesPairPipeline = pipeline.registerStart(end2endTrainigBuilder.buildPipeline());
        //just extract the learned template
        Pipe<Pair<Pair<Template, NeuralModel>, Progress>, Pair<Template, NeuralModel>> pairTemplatePipe1 = pipeline.register(new FirstFromPairPipe<>("FirstFromPairPipe1"));
        sourcesPairPipeline.connectAfter(pairTemplatePipe1);
        Pipe<Pair<Template, NeuralModel>, Template> pairTemplatePipe = pipeline.register(pairTemplatePipe1.connectAfter(new FirstFromPairPipe<>("FirstFromPairPipe2")));
        pipeline.registerEnd(pairTemplatePipe.connectAfter(new StreamifyPipe<>()));
        return pipeline;
    }
}