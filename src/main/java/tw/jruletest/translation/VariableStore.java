package tw.jruletest.translation;

import java.util.*;

public class VariableStore {

    private static Map<String, ArrayList<String>> variables = new HashMap<>();

    public static void addVariable(String method, String varName) {
        try {
            variables.get(method).add(varName);
        } catch(NullPointerException e) {
            variables.put(method, new ArrayList<>(Collections.singleton(varName)));
        }
    }

    public static String getNextUnusedName(String method, String variableName) {
        try {
            int numberSimilar = 0;
            ArrayList<String> methodVars = variables.get(method);
            for (String variable : methodVars) {
                if (variable.contains(variableName)) {
                    numberSimilar++;
                }
            }
            String newName = variableName + (numberSimilar + 1);
            addVariable(method, newName);
            return newName;
        } catch(NullPointerException e) {
            addVariable(method, variableName);
            return variableName;
        }
    }

    public static boolean variableExists(String variable, String method) {
        return variables.get(method).contains(variable);
    }

    public static void reset() {
        variables = new HashMap<>();
    }
}