package networks.structure.neurons.creation;

import constructs.template.WeightedRule;
import networks.evaluation.iteration.State;
import networks.structure.neurons.Neuron;

/**
 * Created by gusta on 8.3.17.
 */
public class AggregationNeuron<S extends State> extends Neuron<RuleNeurons, S> {

    public AggregationNeuron(WeightedRule groundRule, int index, S state) {
        super(index, groundRule.toString(), state, groundRule.aggregationFcn);
    }
}
