package tw.jruletest.translation;

import java.util.*;

public class VariableStore {

    private static Map<String, ArrayList<String>> variables = new HashMap<>();

    public static void addVariable(String method, String varName) {
        try {
            variables.get(method).add(varName);
        } catch(NoSuchElementException e) {
            variables.put(method, new ArrayList<>(Collections.singleton(varName)));
        }
    }

    public static String getNextUnusedName(String method, String variableName) {
        int numberSimilar = 0;
        ArrayList<String> methodVars = variables.get(method);
        for(String variable: methodVars) {
            if(variable.contains(variableName)) {
                numberSimilar ++;
            }
        }
        return variableName + (numberSimilar + 1);
    }

    public static void reset() {
        variables = new HashMap<>();
    }
}