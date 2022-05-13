package projectMathServer.Expression.ExpressionComponents;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class Constant extends AbstractNode {

    private final double value;

    public Constant(double value) {
        super(Collections.emptyList());
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Constant constant = (Constant) o;
        return Double.compare(constant.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public double evaluate(Map<String, Double> varVal) {
        return getValue();
    }
}
