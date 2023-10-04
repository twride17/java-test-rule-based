package tw.jruletest.variables;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Utility class designed to store all the variables that exist when the current test suite is being created.
 *
 * @author Toby Wride
 * */

public class VariableStore {

    private static Map<String, ArrayList<Variable>> variables = new HashMap<>();

    /**
     * Adds a new variable to the map of variables, using the method name as the key and a new Variable object as the value
     *
     * @param method the name of the method which created the variable
     * @param varName the name of the variable
     * @param varType the type of the new variable
     * @param declared boolean flag for if the variable has been declared yet
     * */

    public static void addVariable(String method, String varName, Type varType, boolean declared) {
        try {
            variables.get(method).add(new Variable(varName, varType, declared));
        } catch(NullPointerException e) {
            variables.put(method, new ArrayList<>(Collections.singleton(new Variable(varName, varType, declared))));
        }
    }

    /**
     * Adds a new variable to the map of variables, using the method name as the key and a new Variable object as the value.
     * The variable created is stored as already declared by default
     *
     * @param method the name of the method which created the variable
     * @param varName the name of the variable
     * @param varType the type of the new variable
     * */

    public static void addVariable(String method, String varName, Type varType) {
        addVariable(method, varName, varType, true);
    }

    /**
     * Uses the provided variable name to create a new variable with a name nearly similar to the current variable.
     * The Variable object created is assumed to be declared.
     *
     * @param method the name of the method which created the current variable
     * @param variableName the name of the current variable
     * @param type the type of the current variable
     *
     * @return the new name for the variable
     * */

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

    /**
     * Finds the names of all the variables defined in the required method.
     *
     * @param method the name of the method to find the variables for
     *
     * @return a list of the names of all the variables defined in the method
     * */

    public static ArrayList<String> getVarNames(String method) {
        ArrayList<String> varNames = new ArrayList<>();
        for(Variable var: variables.get(method)) {
            varNames.add(var.getName());
        }
        return varNames;
    }

    /**
     * Finds the Variable object representing the provided method's variable identified by the given variable name.
     *
     * @param method the name of the method in which the variable was defined.
     * @param variableName the name of the variable to find
     *
     * @return the Variable object for the variable found with the given name. Returns null if no variable matches.
     * */

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

    /**
     * Determines if there exists a variable with the given name in the specified method.
     *
     * @param method the name of the method
     * @param variableName the name of the variable to check existence of
     *
     * @return true if a variable with this name exists in the given method or false if not.
     * */

    public static boolean variableExists(String method, String variableName) {
        return findVariable(method, variableName) != null;
    }

    /**
     * Resets the storage for the variables
     * */

    public static void reset() {
        variables = new HashMap<>();
    }
}