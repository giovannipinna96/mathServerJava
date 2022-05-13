package projectMathServer.Server;

import projectMathServer.Expression.Computator;
import projectMathServer.Expression.VariableValues;

import java.util.HashSet;
import java.util.Set;

import static projectMathServer.Server.StatComputeServerUtils.isMatchingPattern;

public class ComputeServerUtils {
    public enum ComputationKind {
        MIN,
        MAX,
        AVG,
        COUNT;
    }

    public enum ValuesKind {
        LIST,
        GRID;
    }

    /**
     * @param variableVal string that represent the variable and its interval.
     *                    The correct syntax is VarName:JavaNum:JavaNum:JavaNum
     *                    where VarName = '[a-z][a-z0-9]*'
     *                    and JavaNum  is a string that can be correctly parsed to a double
     *                    The JavaNum represent the min:step:max of the interval in which to consider the variable
     *                    (example -> x0:-1:0.1:1 or y:-10:1:20)
     * @return VariableValues object that have a field for name, for min, for step and for max
     */
    private static VariableValues transformInVariableValues(String variableVal) {
        String[] tokensVariableVal = variableVal.split(":");
        String nameVariableVal = tokensVariableVal[0];
        try {
            double min = Double.parseDouble(tokensVariableVal[1]);
            double step = Double.parseDouble(tokensVariableVal[2]);
            double max = Double.parseDouble(tokensVariableVal[3]);

            if (max <= min) {
                throw new IllegalArgumentException("the minimum ( " + min + " )" +
                        " value can not been bigger or equal to the maximum ( " + max + " )value");
            } else if ((max - min) < step) {
                throw new IllegalArgumentException("the step ( " + step + " )" +
                        " can not be smaller than the interval (max - min)");
            } else if (step <= 0) {
                throw new IllegalArgumentException("the step ( " + step + " )" +
                        " can not be smaller or equal to zero");
            }
            return new VariableValues(nameVariableVal, max, min, step);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("impossible to parse the numbers provided");
        }
    }

    /**
     * This method checks if the request respects the syntax. If yes, the request string is divided
     * and processed in its entirety in order to create the various objects that will make up the Computator
     *
     * @param request client request
     * @return Computator object which contains all the parts of the request separated and ready to be processed
     */
    public static Computator computationRequestToComputator(String request) {
        String[] tokensComputationRequest = request.split(";");

        //control all request
        if (tokensComputationRequest.length < 3) {
            throw new IllegalArgumentException("not correct syntax request." +
                    " The computation request must have 3 or more argument. In this request there is only " +
                    tokensComputationRequest.length + " argument");
        }

        String[] tokensComputationAndValuesKind = tokensComputationRequest[0].split("_");
        //control Computation kind and Value kind
        if (!(isValidComputationKind(tokensComputationAndValuesKind[0]))) {
            throw new IllegalArgumentException("illegal computation kind argument");
        } else if (!(isValidValuesKind(tokensComputationAndValuesKind[1]))) {
            throw new IllegalArgumentException("illegal values kind argument");
        }
        ComputationKind compKind = ComputationKind.valueOf(tokensComputationAndValuesKind[0]);
        ValuesKind valKind = ValuesKind.valueOf(tokensComputationAndValuesKind[1]);

        //control variable values function
        String variableValuesFunction = tokensComputationRequest[1];
        if (isMatchingPattern(".*,", variableValuesFunction)) {
            throw new IllegalArgumentException("the VariableValuesFunction cannot end with a dot or a comma.");
        }
        String[] tokensVariableValuesFunction = variableValuesFunction.split(",");
        Set<VariableValues> variableValuesSet = new HashSet<>();
        final String variableValuesRegex = "[a-z][a-z0-9]*:[//-]?[0-9]+[//.]?[0-9]*:[0-9]+[//.]?[0-9]*:[//-]?[0-9]+[//.]?[0-9]*";

        //if there are more variable function
        for (String tokenVariableValuesFunction : tokensVariableValuesFunction) {
            if (!isMatchingPattern(variableValuesRegex, tokenVariableValuesFunction)) {
                throw new IllegalArgumentException("error in variable values syntax." +
                        " Variable values must be written like name:number:number:number");
            } else if (variableValuesSet.contains(transformInVariableValues(tokenVariableValuesFunction))) {
                throw new IllegalArgumentException("Multiple definitions for the variable " +
                        transformInVariableValues(tokenVariableValuesFunction).getName());
            }
            variableValuesSet.add(transformInVariableValues(tokenVariableValuesFunction));
        }

        //expression
        if (isMatchingPattern(".*;", request)) {
            throw new IllegalArgumentException("the Expressions part cannot end with a semicolon.");
        }
        int expressionNumber = tokensComputationRequest.length - 2;
        String[] expressions = new String[expressionNumber];
        for (int i = 2; i < tokensComputationRequest.length; i++) {
            expressions[i - 2] = tokensComputationRequest[i];
        }
        return new Computator(expressions, variableValuesSet, valKind, compKind);
    }

    public static boolean isValidComputationKind(String request) {
        String pattern = "MIN|MAX|AVG|COUNT";
        return isMatchingPattern(pattern, request);
    }

    public static boolean isValidValuesKind(String request) {
        String pattern = "LIST|GRID";
        return isMatchingPattern(pattern, request);
    }
}
