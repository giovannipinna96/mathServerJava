package projectMathServer.Expression.ExpressionComponents;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Operator extends AbstractNode {

    @Override
    public double evaluate(Map<String, Double> varVal) {
        List<AbstractNode> children = getChildren();
        double[] constants = new double[children.size()];
        constants[0] = children.get(0).evaluate(varVal);
        constants[1] = children.get(1).evaluate(varVal);
        return getType().getFunction().apply(constants);
    }

    public enum Type {
        SUM('+', a -> a[0] + a[1]),
        SUBTRACTION('-', a -> a[0] - a[1]),
        MULTIPLICATION('*', a -> a[0] * a[1]),
        DIVISION('/', a -> a[0] / a[1]),
        POWER('^', a -> Math.pow(a[0], a[1]));
        private final char symbol;
        private final Function<double[], Double> function;

        Type(char symbol, Function<double[], Double> function) {
            this.symbol = symbol;
            this.function = function;
        }

        public char getSymbol() {
            return symbol;
        }

        public Function<double[], Double> getFunction() {
            return function;
        }
    }

    private final Type type;

    public Operator(Type type, List<AbstractNode> children) {
        super(children);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operator operator = (Operator) o;
        return type == operator.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(getChildren().stream()
                .map(AbstractNode::toString)
                .collect(Collectors.joining(" " + Character.toString(type.symbol) + " "))
        );
        sb.append(")");
        return sb.toString();
    }
}
