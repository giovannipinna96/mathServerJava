package projectMathServer.Expression;

import projectMathServer.Exception.VariableValuesNotEqualSizeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VariableValuesUtils {

    /**
     * The method first check if the set of variableValues are empty and if there are at least two variable.
     * Check if every variable has the same number of values (necessary condition to continue),
     * if yes merge the variables Values else there will be an exception.
     *
     * @param variableValues variable that will be used for merge them values
     * @return A list of map (which represent tuples) that has for key a string that represent the name of the variable in that tuple
     * and for value the value of that variable in that tuple.
     */
    public static List<Map<String, Double>> listValuesTuples(Set<VariableValues> variableValues) throws VariableValuesNotEqualSizeException {
        int size = 0;
        if (variableValues.isEmpty()) {
            throw new IllegalArgumentException("there is not value in the set");
        } else if (!variableValues.iterator().hasNext()) {
            throw new IllegalArgumentException("not given variable");
        } else {
            size = variableValues.iterator().next().getAllIntervalValues().size();
        }

        //initializing to empty
        List<Map<String, Double>> listTuples = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            listTuples.add(new HashMap<>());
        }

        for (VariableValues varVal : variableValues) {
            List<Double> valuesInVariableValue = varVal.getAllIntervalValues();
            if (valuesInVariableValue.size() != size) {
                throw new VariableValuesNotEqualSizeException("not compatible size");
            } else {
                for (int i = 0; i < size; i++) {
                    listTuples.get(i).put(varVal.getName(), valuesInVariableValue.get(i));
                }
            }
        }
        return listTuples;
    }

    /**
     * The method first check if the set of variableValues are empty and if there are at least two variable.
     * After that create a list of map where in every map there will be the cartesian product between variables values.
     * For make the cartesian product first create a list of map and populate every map with the values of the first variable
     * after that duplicate the list of map and add to all map the values of the second variable and replace the first map
     * with the duplicate. Repeat for all the variables.
     *
     * @param variableValues variable that will be used for merge them values
     * @return A list of map (which represent tuples) that has for key a string that represent the name of the variable in that tuple
     * and for value the value of that variable in that tuple.
     */
    public static List<Map<String, Double>> gridValuesTuples(Set<VariableValues> variableValues) {
        Iterator<VariableValues> variableIterator = variableValues.iterator();
        if (variableValues.isEmpty()) {
            throw new IllegalArgumentException("there is not value in the set");
        } else if (!variableIterator.hasNext()) {
            throw new IllegalArgumentException("not given variable");
        } else {
            //initializing to empty
            List<Map<String, Double>> tuples = new ArrayList<>();
            VariableValues firstVariableValue = (VariableValues) variableIterator.next();
            for (int i = 0; i < firstVariableValue.getAllIntervalValues().size(); i++) {
                tuples.add(new HashMap<>());
                tuples.get(i).put(firstVariableValue.getName(), firstVariableValue.getAllIntervalValues().get(i));
            }
            while (variableIterator.hasNext()) {
                VariableValues nextVariableValue = (VariableValues) variableIterator.next();
                for (double nextValue : nextVariableValue.getAllIntervalValues()) {
                    for (int i = 0; i < firstVariableValue.getAllIntervalValues().size(); i++) {
                        tuples.get(i).put(nextVariableValue.getName(), nextValue);
                    }
                }
            }
            return tuples;
        }
    }
}


