package projectMathServer.Expression.ExpressionComponents;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Variable extends AbstractNode {

    private final String name;

    public Variable(String name) {
        super(Collections.emptyList());
        this.name = name;
    }

    public String getName() {
        return name;
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
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public double evaluate(Map<String, Double> varVal) {
        String variableName = getName();
        Double value = varVal.get(variableName);
        if (value == null) {
            throw new IllegalArgumentException(String.format(Locale.US, "attention could not found " + variableName));
        }
        return value;
    }
}
