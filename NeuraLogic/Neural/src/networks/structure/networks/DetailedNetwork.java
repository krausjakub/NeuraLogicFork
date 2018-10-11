package networks.structure.networks;

import ida.utils.tuples.Pair;
import networks.structure.weights.Weight;
import networks.structure.metadata.LinkedNeuronMapping;
import networks.structure.metadata.NetworkMetadata;
import networks.structure.metadata.NeuronMapping;
import networks.structure.metadata.WeightedNeuronMapping;
import networks.structure.neurons.Neuron;
import networks.structure.neurons.WeightedNeuron;
import networks.structure.neurons.creation.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Logger;

public class DetailedNetwork extends TopologicNetwork {
    private static final Logger LOG = Logger.getLogger(DetailedNetwork.class.getName());

    /**
     * Locally valid input overloading for some neurons to facilitate dynamic structure changes
     */
    public @Nullable Map<Neuron, NeuronMapping> extraInputMapping;

    public @Nullable Map<Neuron, NeuronMapping> outputMapping;

    @Nullable
    public
    Neurons neurons;

    public DetailedNetwork() {
    }

    public class Neurons {

        public List<AtomNeuron> atomNeurons;
        public List<AggregationNeuron> aggNeurons;
        public List<RuleNeuron> ruleNeurons;
        public List<WeightedRuleNeuron> weightedRuleNeurons;
        public List<FactNeuron> factNeurons;
        public List<NegationNeuron> negationNeurons;

        List<Neuron> roots;
        List<Neuron> leaves;
    }

    Boolean recursive;

    @Nullable
    NetworkMetadata metadata;

    public Map<Neuron, NeuronMapping> calculateOutputs() {
        Map<Neuron, NeuronMapping> outputMapping = new HashMap<>();
        for (Neuron parent : allNeuronsTopologic) {
            Iterator<Neuron> inputs = getInputs(parent);
            Neuron child;
            while ((child = inputs.next()) != null) {
                NeuronMapping parentMapping = outputMapping.computeIfAbsent(child, f -> new LinkedNeuronMapping());
                parentMapping.addLink(child);
            }
        }
        return outputMapping;
    }

    public <T extends WeightedNeuron, S extends State.Structure> Iterator<Pair<T, Weight>> getInputs(WeightedNeuron<T, S> neuron) {
        WeightedNeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? (WeightedNeuronMapping<T>) extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getWeightedInputs();
        }
    }

    public <T extends Neuron, S extends State.Structure> Iterator<T> getInputs(Neuron<T, S> neuron) {
        NeuronMapping<T> inputMapping;
        if ((inputMapping = extraInputMapping != null ? extraInputMapping.get(neuron) : null) != null) {
            return inputMapping.iterator();
        } else {
            return neuron.getInputs().iterator();
        }
    }

    public <T extends Neuron, S extends State.Structure> Iterator<T> getOutputs(Neuron<T, S> neuron) {
        NeuronMapping<T> mapping;
        if ((mapping = outputMapping != null ? outputMapping.get(neuron) : null) != null) {
            return mapping.iterator();
        } else {
            return null;
        }
    }


    public void removeInput(Neuron neuron, Pair<Neuron, Weight> input) {
        //todo to use with pruning
    }

    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public Integer getSize() {
        return allNeuronsTopologic.size();
    }

}