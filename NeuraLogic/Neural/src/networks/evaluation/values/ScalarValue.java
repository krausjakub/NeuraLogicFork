package networks.evaluation.values;

/**
 * Created by gusta on 8.3.17.
 */
public class ScalarValue extends Value {
    public double value;

    @Override
    protected Value multiplyByScalar(ScalarValue val2) {
        return null;
    }

    @Override
    protected Value multiplyByMatrix(MatrixValue val2) {
        return null;
    }

    @Override
    protected Value multiplyByVector(VectorValue val2) {
        return null;
    }

    @Override
    protected Value addMatrix(MatrixValue val2) {
        return null;
    }

    @Override
    protected Value addVector(VectorValue val2) {
        return null;
    }

    @Override
    protected Value addScalar(ScalarValue val2) {
        return null;
    }

    public ScalarValue(double val) {
        value = val;
    }
}