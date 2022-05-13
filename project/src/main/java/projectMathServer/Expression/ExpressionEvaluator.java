package projectMathServer.Expression;

import projectMathServer.Exception.VariableValuesNotEqualSizeException;
import projectMathServer.Expression.ExpressionComponents.AbstractNode;
import projectMathServer.Server.ComputeServerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static projectMathServer.Expression.VariableValuesUtils.gridValuesTuples;
import static projectMathServer.Expression.VariableValuesUtils.listValuesTuples;

public class ExpressionEvaluator {
    private final String expression;
    private final Set<VariableValues> variableValues;
    private final ComputeServerUtils.ValuesKind valuesKind;

    public ExpressionEvaluator(String expression, Set<VariableValues> variableValues, ComputeServerUtils.ValuesKind valuesKind) {
        this.expression = expression;
        this.variableValues = variableValues;
        this.valuesKind = valuesKind;
    }

    public String getExpression() {
        return expression;
    }

    public Set<VariableValues> getVariableValues() {
        return variableValues;
    }

    public ComputeServerUtils.ValuesKind getValuesKind() {
        return valuesKind;
    }

    /**
     * This methods that evaluates an expression given its variableValues function.
     * For variable use the field variableValues inside used for construct the object ExpressionEvaluator.
     * The expression could be evaluated in two way:
     * LIST: the variable values are merged. (the variables must have the same number of values)
     * GRID: the variables values are combined with the cartesian product
     *
     * @return is a Map where the key is the map(String,Double) that is the variable tuple in GRID or LIST mode.
     * and the value is a double that represent the value of the expression for that tuple
     */
    public Map<Map<String, Double>, Double> evaluate() throws VariableValuesNotEqualSizeException {
        AbstractNode expressionNodes = new Parser(expression).parse();
        Map<Map<String, Double>, Double> values = new HashMap<>();

        switch (valuesKind) {
            case LIST:
                List<Map<String, Double>> listListValues = listValuesTuples(variableValues);
                values = evaluator(expressionNodes, listListValues);
                break;
            case GRID:
                List<Map<String, Double>> listGridValues = gridValuesTuples(variableValues);
                values = evaluator(expressionNodes, listGridValues);
                break;
        }
        return values;
    }

    /**
     * The method created the output value that is a map where the key is another map that represent the variable tuple
     * and the key is a Double that represent the value of the expression for that tuple.
     *
     * @param expressionNode node that represent the entire expression
     * @param listValues     list of tuples variables in which the expression must be evaluated
     * @return is a Map where the key is the input tuple (that is another map),
     * and the value is the value of the expression for that tuple
     */
    private Map<Map<String, Double>, Double> evaluator(AbstractNode expressionNode, List<Map<String, Double>> listValues) {
        Map<Map<String, Double>, Double> values = new HashMap<>();
        for (Map<String, Double> value : listValues) {
            //methods evaluate(value) -> it is implemented for each class that extends the AbstractNode abstract class
            values.put(value, expressionNode.evaluate(value));
        }
        return values;
    }

    @Override
    public String toString() {
        return "ExpressionEvaluator{" +
                "expression='" + expression + '\'' +
                ", variableValues=" + variableValues +
                ", valuesKind=" + valuesKind +
                '}';
    }
}
