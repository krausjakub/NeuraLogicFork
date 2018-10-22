package neuralogic.template;

import com.sun.istack.internal.NotNull;
import constructs.Conjunction;
import constructs.WeightedPredicate;
import constructs.example.ValuedFact;
import constructs.template.components.WeightedRule;
import ida.utils.tuples.Pair;
import networks.structure.components.weights.Weight;
import neuralogic.grammarParsing.PlainGrammarVisitor;
import parsers.neuralogic.NeuralogicBaseVisitor;
import parsers.neuralogic.NeuralogicParser;
import parsers.neuralogic.NeuralogicParser.TemplateFileContext;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Parsing text files for contained template given a antlr4 grammar, using generated Lexer and Parser
 * Processing of the parse tree implemented using the Visitor pattern as inspired by http://jakubdziworski.github.io/java/2016/04/01/antlr_visitor_vs_listener.html
 * <p>
 * STATE: contains factories
 * Created by gusta on 1.3.18.
 */
public class PlainTemplateParseTreeExtractor extends TemplateParseTreeExtractor<PlainGrammarVisitor> {

    private static final Logger LOG = Logger.getLogger(PlainTemplateParseTreeExtractor.class.getName());

    public PlainTemplateParseTreeExtractor(PlainGrammarVisitor visitor) {
        super(visitor);
    }

    @Override
    public List<WeightedRule> getWeightedRules(@NotNull TemplateFileContext ctx) {
        return new RuleLinesVisitor().visitTemplateFile(ctx);
    }

    @Override
    public List<ValuedFact> getWeightedFacts(@NotNull TemplateFileContext ctx) {
        return new FactsVisitor().visitTemplateFile(ctx);
    }

    @Override
    public List<Conjunction> getWeightedConjunctions(TemplateFileContext ctx) {
        return new ConjunctionsVisitor().visitTemplateFile(ctx);
    }

    @Override
    public List<Pair<Weight, Map<String, Object>>> getWeightsMetadata(@NotNull TemplateFileContext ctx) {
        return new WeightsMetadataVisitor().visitTemplateFile(ctx);
    }

    @Override
    public List<Pair<WeightedPredicate, Map<String, Object>>> getPredicatesMetadata(@NotNull TemplateFileContext ctx) {
        return new PredicatesMetadataVisitor().visitTemplateFile(ctx);
    }

    public class RuleLinesVisitor extends NeuralogicBaseVisitor<List<WeightedRule>> {

        @Override
        public List<WeightedRule> visitTemplateFile(@NotNull NeuralogicParser.TemplateFileContext ctx) {

            List<NeuralogicParser.TemplateLineContext> template_lines = ctx.templateLine();

            PlainGrammarVisitor.RuleLineVisitor ruleLineVisitor = visitor.new RuleLineVisitor();
            List<WeightedRule> rules = template_lines.stream()
                    .filter(line -> line.lrnnRule() != null)
                    .map(line -> line.lrnnRule().accept(ruleLineVisitor))
                    .collect(Collectors.toList());

            return rules;
        }
    }

    public class FactsVisitor extends NeuralogicBaseVisitor<List<ValuedFact>> {

        @Override
        public List<ValuedFact> visitTemplateFile(@NotNull NeuralogicParser.TemplateFileContext ctx) {

            List<NeuralogicParser.TemplateLineContext> template_lines = ctx.templateLine();

            PlainGrammarVisitor.FactVisitor factVisitor = visitor.new FactVisitor();
            List<ValuedFact> facts = template_lines.stream()
                    .filter(line -> line.fact() != null)
                    .map(line -> line.fact().accept(factVisitor))
                    .collect(Collectors.toList());

            return facts;
        }
    }

    public class ConjunctionsVisitor extends NeuralogicBaseVisitor<List<Conjunction>> {

        @Override
        public List<Conjunction> visitTemplateFile(@NotNull NeuralogicParser.TemplateFileContext ctx) {

            List<NeuralogicParser.TemplateLineContext> template_lines = ctx.templateLine();

            PlainGrammarVisitor.FactConjunctionVisitor factConjunctionVisitor = visitor.new FactConjunctionVisitor();
            List<Conjunction> conjunctions = template_lines.stream()
                    .filter(line -> line.conjunction() != null)
                    .map(line -> line.conjunction().accept(factConjunctionVisitor))
                    .collect(Collectors.toList());

            return conjunctions;
        }
    }

    public class WeightsMetadataVisitor extends NeuralogicBaseVisitor<List<Pair<Weight, Map<String, Object>>>> {

        @Override
        public List<Pair<Weight, Map<String, Object>>> visitTemplateFile(@NotNull NeuralogicParser.TemplateFileContext ctx) {

            List<NeuralogicParser.TemplateLineContext> template_lines = ctx.templateLine();

            PlainGrammarVisitor.WeightMetadataVisitor weightMetadataVisitor = visitor.new WeightMetadataVisitor();
            List<Pair<Weight, Map<String, Object>>> weightMetadataList = template_lines.stream()
                    .filter(line -> line.weightMetadata() != null)
                    .map(line -> line.lrnnRule().accept(weightMetadataVisitor))
                    .collect(Collectors.toList());

            return weightMetadataList;
        }
    }

    public class PredicatesMetadataVisitor extends NeuralogicBaseVisitor<List<Pair<WeightedPredicate, Map<String, Object>>>> {

        @Override
        public List<Pair<WeightedPredicate, Map<String, Object>>> visitTemplateFile(@NotNull NeuralogicParser.TemplateFileContext ctx) {

            List<NeuralogicParser.TemplateLineContext> template_lines = ctx.templateLine();

            PlainGrammarVisitor.PredicateOffsetVisitor predicateOffsetVisitor = visitor.new PredicateOffsetVisitor();
            template_lines.stream()
                    .filter(line -> line.predicateOffset() != null)
                    .map(line -> line.predicateOffset().accept(predicateOffsetVisitor))
                    .collect(Collectors.toList());
            //no need to do anything, offsets are being set during visiting

            PlainGrammarVisitor.PredicateMetadataVisitor predicateMetadataVisitor = visitor.new PredicateMetadataVisitor();
            List<Pair<WeightedPredicate, Map<String, Object>>> predicateMetadataList = template_lines.stream()
                    .filter(line -> line.predicateMetadata() != null)
                    .map(line -> line.lrnnRule().accept(predicateMetadataVisitor))
                    .collect(Collectors.toList());

            return predicateMetadataList;
        }
    }

}