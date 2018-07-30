package pipeline;

import java.util.logging.Logger;

/**
 * Merge blocks are special as they need external execution (there is no single predecessor that could induce it by itself)
 *
 * @param <I1>
 * @param <I2>
 * @param <O>
 */
public abstract class Merge<I1, I2, O> implements ConnectBefore, ConnectAfter<O>, Executable {
    private static final Logger LOG = Logger.getLogger(Merge.class.getName());

    ConnectAfter<I1> input1;
    ConnectAfter<I2> input2;
    public ConnectBefore<O> output;

    public String ID;
    /**
     * Storage of the (intermediate) result of calculation of this Pipe. It will only be not null once someone has
     * actually run (called accept) this Pipe.
     */
    O outputReady;

    public Merge(String id) {
        ID = id;
    }

    @Override
    @Deprecated
    public void run() {
        I1 i1 = input1.get();
        I2 i2 = input2.get();
        accept(i1, i2);
    }

    public O get() {
        if (outputReady == null) {
            LOG.severe("The result of this Merge " + ID + " is requested but not yet calculated");
            LOG.severe("Pipeline is broken");
            System.exit(3);
        }
        return outputReady;
    }

    /**
     * Call by either one of the inputs
     *
     * @param o
     */
    public void accept(Object o) {
        I1 i1;
        I2 i2;
        if ((i1 = input1.get()) != null && (i2 = input2.get()) != null)
            accept(i1, i2);
        else
            LOG.warning("Trying to run a Merge " + ID + " by Object " + o + " without both inputs provided");
    }

    public void accept(I1 input1, I2 input2) {
        outputReady = merge(input1, input2);
        if (output != null)
            output.accept(outputReady);
    }



/*
    private Stream<O> merge(Stream<I1> input1, I2 input2) {
        return input1.map(i1 -> merge(i1, input2));
    }

    private Stream<O> merge(I1 input1, Stream<I2> input2) {
        return input2.map(i2 -> merge(input1, i2));
    }

    private Stream<O> merge(Stream<I1> input1, Stream<I2> input2) {
        return Utilities.zipStreams(input1, input2, this::merge);
    }
*/
    protected abstract O merge(I1 input1, I2 input2);

    @Override
    public ConnectBefore<O> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<O> prev) {
        output = prev;
    }

    //Note - it will be probably better to explicitly differentiate between I1 and I2
    /*
    private final Class<I1> i1;
    private final Class<I2> i2;

    public <T> ConnectAfter<T> connectBefore(ConnectAfter<T> prev, T cls){
        if (cls.getClass() == i1.getClass())
        return prev;
    }
    */

    //TODO next
    public ConnectAfter<I1> connectBeforeL(ConnectAfter<I1> i1){
        input1 = i1;
        i1.setOutput(this);
        return i1;
    }

    public ConnectAfter<I2> connectBeforeR(ConnectAfter<I2> i2){
        input2 = i2;
        i2.setOutput(this);
        return i2;
    }

}
