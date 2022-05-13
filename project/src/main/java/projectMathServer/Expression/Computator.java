package projectMathServer.Expression;

import projectMathServer.Exception.VariableValuesNotEqualSizeException;
import projectMathServer.Server.ComputeServerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static projectMathServer.Expression.VariableValuesUtils.gridValuesTuples;
import static projectMathServer.Expression.VariableValuesUtils.listValuesTuples;

public class Computator {
    private final String[] expressions;
    private final Set<VariableValues> variableValues;
    private final ComputeServerUtils.ValuesKind valueKind;
    private final ComputeServerUtils.ComputationKind computationKind;

    public Computator(String[] expressions, Set<VariableValues> variableValues, ComputeServerUtils.ValuesKind valueKind, ComputeServerUtils.ComputationKind computationKind) {
        this.expressions = expressions;
        this.variableValues = variableValues;
        this.valueKind = valueKind;
        this.computationKind = computationKind;
    }

    public String[] getExpressions() {
        return expressions;
    }

    public Set<VariableValues> getVariableValues() {
        return variableValues;
    }

    public ComputeServerUtils.ValuesKind getValueKind() {
        return valueKind;
    }

    public ComputeServerUtils.ComputationKind getComputationKind() {
        return computationKind;
    }

    private static double averageValue(List<Double> listOfDouble) {
        return listOfDouble.stream().mapToDouble(Double::doubleValue).sum() / listOfDouble.size();
    }

    @Override
    public String toString() {
        return "Computator{" +
                "expressions=" + Arrays.toString(expressions) + System.lineSeparator() +
                ", variableValues=" + variableValues + System.lineSeparator() +
                ", valueKind=" + valueKind + System.lineSeparator() +
                ", computationKind=" + computationKind + System.lineSeparator() +
                '}';
    }

    public double compute() throws VariableValuesNotEqualSizeException {
        double computedValue = Double.NaN;
        switch (computationKind) {
            case MAX:
                computedValue = Collections.max(allResults());
                break;
            case MIN:
                computedValue = Collections.min(allResults());
                break;
            case AVG:
                //specifications require averaging only on the first expression
                String[] firstExpression = {expressions[0]};
                Computator firstExpressionComputator = new Computator(firstExpression, variableValues, valueKind, computationKind);
                computedValue = averageValue(firstExpressionComputator.allResults());
                break;
            case COUNT:
                computedValue = countTuples();
                break;
        }
        return computedValue;
    }

    /**
     * @return return a List of Double that represent the result for each expressions in the Computator object
     */
    public List<Double> allResults() throws VariableValuesNotEqualSizeException {
        List<Double> allResultsValues = new ArrayList<>();
        for (String expression : expressions) {
            ExpressionEvaluator expressionEval = new ExpressionEvaluator(expression, variableValues, valueKind);
            try {
                //evaluate each expression in Computator
                Map<Map<String, Double>, Double> expressionEvalValues = expressionEval.evaluate();
                //add all the result of this expression
                allResultsValues.addAll(expressionEvalValues.values());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The expression " + expression + "has the issue " + e.getMessage());
            }
        }
        return allResultsValues;
    }

    /**
     * @return Return the number of all the tuples in which the expression is to be evaluated
     */
    private int countTuples() throws VariableValuesNotEqualSizeException {
        int numOfTuples = 0;
        switch (valueKind) {
            case GRID:
                numOfTuples = gridValuesTuples(variableValues).size();
                break;
            case LIST:
                numOfTuples = listValuesTuples(variableValues).size();
                break;
        }
        return numOfTuples;
    }

}
