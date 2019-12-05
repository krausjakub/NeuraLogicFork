package networks.structure.components.neurons.types;

import networks.computation.iteration.visitors.neurons.NeuronVisitor;
import networks.structure.components.neurons.BaseNeuron;
import networks.structure.components.weights.Weight;
import networks.structure.metadata.states.State;

import java.util.ArrayList;

/**
 * Created by gusta on 14.3.17.
 */
public class NegationNeuron<S extends State.Neural> extends BaseNeuron<AtomFact, S> implements AtomFact<AtomFact, S> {

    public NegationNeuron(AtomFact atom, int index, S state) {
        super(index, "neg_" + atom.getName(), state);
        inputs = new ArrayList<>(1);
        inputs.add(atom);
    }

    @Override
    public final Weight getOffset() {
        return null;
    }

    public void visit(NeuronVisitor.Weighted.Detailed visitor) {
        visitor.visit(this);
    }
}