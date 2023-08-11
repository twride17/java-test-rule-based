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

    public static String getNextUnusedVariableName(String method, String variableName) {
        try {
            ArrayList<String> methodVars = getVars(method);
            if(methodVars.contains(variableName)) {
                variableName += countSimilar(methodVars, variableName);
            }
        } catch(NullPointerException e) { }

        addVariable(method, variableName);
        return variableName;
    }

    private static int countSimilar(ArrayList<String> vars, String variable) {
        int numSimilar = 1;
        for(String var: vars) {
            if(var.startsWith(variable)) {
                numSimilar ++;
            }
        }
        return numSimilar;
    }

    private static boolean variableExists(ArrayList<String> variables, String variable) {
        return variables.contains(variable);
    }

    public static void reset() {
        variables = new HashMap<>();
    }

    public static ArrayList<String> getVars(String method) {
        return variables.get(method);
    }
}