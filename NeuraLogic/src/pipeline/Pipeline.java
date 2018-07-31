package pipeline;

import ida.utils.tuples.Pair;
import settings.Settings;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Execution pipeline DAG with nodes as Tasks and edges as pipes. It is your responsibility to create and connect the pipes
 * correctly, and register the terminal (non-streaming) pipes in topological order for scheduling.
 * <p>
 * This is a custom implementation with some hacks but also customization,
 * possible, more generic, 3rd party library : https://dexecutor.github.io/
 * <p>
 * Created by gusta on 14.3.17.
 */
public class Pipeline<S, T> implements ConnectBefore<S>, ConnectAfter<T> {
    // pipeline - vstup a vystup by byl Pipe/Merge/Branch?

    private static final Logger LOG = Logger.getLogger(Pipeline.class.getName());

    public String ID;

    protected Settings settings;
    public ConnectBefore<S> start;
    public ConnectAfter<T> terminal;

    ConcurrentHashMap<String, Branch> branches;
    ConcurrentHashMap<String, Merge> merges;
    ConcurrentHashMap<String, Pipe> pipes;

    ConcurrentHashMap<String, Pipeline> pipelines;

    public ConnectAfter<S> input;
    public ConnectBefore<T> output;

    public Pipeline(String id) {
        this.ID = id;
    }

    /**
     * List of points in the pipeline that need to be called externally, i.e. where streams are terminated.
     */
    //ConcurrentLinkedQueue<Executable> executionQueue;
    public Pair<String, T> execute(S source) {
        //start the whole pipeline
        start.accept(source);
        /*while (!executionQueue.isEmpty()) {
            Executable poll = executionQueue.poll();
            poll.run();
        }*/

        //collect and return all the terminal results
        return new Pair<String, T>(ID, terminal.get());
    }

    public <I, O, A extends Pipe<I,O>> A register(A p) {
        pipes.put(p.ID, p);
        return p;
    }

    public <I, O1, O2> Branch<I, O1, O2> register(Branch<I, O1, O2> b) {
        branches.put(b.ID, b);
        return b;
    }

    public <I1, I2, O, A extends Merge<I1, I2, O>> A register(A m) {
        merges.put(m.ID, m);
        //executionQueue.add(p);
        return m;
    }

    public <I, O> Pipeline<I, O> register(Pipeline<I, O> p) {
        pipelines.put(p.ID, p);
        return p;
    }

    public <O> Pipe<S, O> registerStart(Pipe<S, O> p) {
        start = p;
        register(p);
        return p;
    }

    public <O1, O2, A extends Branch<S, O1, O2>> A registerStart(A p) {
        start = p;
        register(p);
        return p;
    }

    public Pipeline<S, T> registerStart(Pipeline<S, T> p) {
        start = p.start;
        register(p);
        return p;
    }

    public <I, A extends Pipe<I,T>> A registerEnd(A p) {
        terminal = p;
        register(p);
        return p;
    }

    public <I1, I2, A extends Merge<I1, I2, T>> A registerEnd(A p) {
        terminal = p;
        register(p);
        return p;
    }

    public <U> Pipeline<S, U> connectAfter(Pipeline<T, U> next) throws IOException {
        Pipeline<S, U> pipeline = new Pipeline(this.ID + "+" + next.ID);
        pipeline.start = this.start;
        pipeline.terminal = next.terminal;
        pipeline.settings = this.settings; //TODO
        pipeline.input = this.input;
        pipeline.output = next.output;

        pipeline.pipelines.put(this.ID, this);
        pipeline.pipelines.put(next.ID, next);
        return pipeline;
    }

    @Override
    public void accept(S sources) {
        start.accept(sources);
        //TODO no need to wait here?
        if (this.output != null) {
            this.output.accept(terminal.get());
        }
    }

    @Override
    public T get() {
        return terminal.get();
    }

    @Override
    public ConnectBefore<T> getOutput() {
        return output;
    }

    @Override
    public void setOutput(ConnectBefore<T> prev) {
        output = prev;
    }

    @Override
    public ConnectAfter<S> getInput() {
        return input;
    }

    @Override
    public void setInput(ConnectAfter<S> prev) {
        input = prev;
    }
}