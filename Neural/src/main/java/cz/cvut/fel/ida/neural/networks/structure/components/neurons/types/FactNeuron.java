package cz.cvut.fel.ida.neural.networks.structure.components.neurons.types;

import cz.cvut.fel.ida.algebra.weights.Weight;
import cz.cvut.fel.ida.neural.networks.computation.iteration.NeuronVisiting;
import cz.cvut.fel.ida.neural.networks.computation.iteration.visitors.neurons.NeuronVisitor;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.BaseNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.WeightedNeuron;
import cz.cvut.fel.ida.neural.networks.structure.components.neurons.states.States;

import java.util.ArrayList;

/**
 * Created by gusta on 8.3.17.
 */
public class FactNeuron extends WeightedNeuron<BaseNeuron, States.SimpleValue> implements AtomFact<BaseNeuron, States.SimpleValue> {

    public boolean hasLearnableValue;

    public FactNeuron(String fact, Weight offset, int index, States.SimpleValue state) {
        super(fact, index, state, offset);
        inputs = new ArrayList<>(0);
        weights = new ArrayList<>(0);
    }

    public void visit(NeuronVisitor.Weighted visitor) {
        if (hasLearnableValue) {    // only factNeurons with a learnable value are actionable by some visitors (otherwise they are just a simple constant Value storage)
            visitor.visit(this);
        }
    }

    public void visit(NeuronVisiting.Weighted visitor) {
        if (hasLearnableValue) {    // only factNeurons with a learnable value are actionable by some visitors (otherwise they are just a simple constant Value storage)
            visitor.visit(this);
        }
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}