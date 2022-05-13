package projectMathServer.Expression.ExpressionComponents;


import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractNode {
    private final List<AbstractNode> children;

    public AbstractNode(List<AbstractNode> children) {
        this.children = children;
    }

    public List<AbstractNode> getChildren() {
        return children;
    }

    public abstract double evaluate(Map<String, Double> varVal);// throws MapValueNotFoundException;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractNode node = (AbstractNode) o;
        return Objects.equals(children, node.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

}
