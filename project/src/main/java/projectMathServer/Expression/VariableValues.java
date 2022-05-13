package projectMathServer.Expression;

import projectMathServer.Expression.ExpressionComponents.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VariableValues extends Variable {
    private final double max;
    private final double min;
    private final double step;

    public VariableValues(String name, double max, double min, double step) {
        super(name);
        this.max = max;
        this.min = min;
        this.step = step;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getStep() {
        return step;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "VariableValues {" +
                "max=" + max +
                ", min=" + min +
                ", step=" + step +
                '}');
    }

    public List<Double> getAllIntervalValues() {
        List<Double> values = new ArrayList<>();
        for (double i = min; i <= max; i += step) {
            values.add(i);
        }
        return values;
    }
}
