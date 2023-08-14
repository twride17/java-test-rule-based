package tw.jruletest.variables;

import java.lang.reflect.Type;
import java.util.*;

public class VariableStore {

    private static Map<String, ArrayList<Variable>> variables = new HashMap<>();

    public static void addVariable(String method, String varName, Type varType, boolean declared) {
        try {
            variables.get(method).add(new Variable(varName, varType, declared));
        } catch(NullPointerException e) {
            variables.put(method, new ArrayList<>(Collections.singleton(new Variable(varName, varType, declared))));
        }
    }

    public static void addVariable(String method, String varName, Type varType) {
        addVariable(method, varName, varType, true);
    }

    public static String getNextUnusedVariableName(String method, String variableName, Type type) {
        try {
            ArrayList<String> methodVars = getVarNames(method);
            if(methodVars.contains(variableName)) {
                variableName += countSimilar(methodVars, variableName);
            }
        } catch(NullPointerException e) { }

        addVariable(method, variableName, type);
        return variableName;
    }

    private static int countSimilar(ArrayList<String> vars, String variable) {
        int numSimilar = 0;
        for(String var: vars) {
            if(var.startsWith(variable)) {
                numSimilar ++;
            }
        }
        return numSimilar;
    }

    public static void reset() {
        variables = new HashMap<>();
    }

    public static ArrayList<String> getVarNames(String method) {
        ArrayList<String> varNames = new ArrayList<>();
        for(Variable var: variables.get(method)) {
            varNames.add(var.getName());
        }
        return varNames;
    }

    public static Variable findVariable(String method, String variableName) {
        try {
            ArrayList<Variable> vars = variables.get(method);
            for (Variable var : vars) {
                if (var.getName().equals(variableName)) {
                    return var;
                }
            }
        } catch(NullPointerException e) {}
        return null;
    }

    public static boolean variableExists(String method, String variableName) {
        return findVariable(method, variableName) != null;
    }
}