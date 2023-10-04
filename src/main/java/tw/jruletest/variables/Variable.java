package tw.jruletest.variables;

import java.lang.reflect.Type;

/**
 * Utility class for storing and managing details of a variable declared within the test case generation.
 *
 * @author Toby Wride
 * */

public class Variable {

    private String name;
    private Type type;
    private boolean declared;
    /**
     * Constructor for a variable with a given name, type and flag for if the variable has been declared or not.
     *
     * @param name the name of the variable
     * @param type the variable's type
     * @param declared flag determining if the variable has been declared
     * */

    public Variable(String name, Type type, boolean declared) {
        this.name = name;
        this.type = type;
        this.declared = declared;
    }

    /**
     * Gets the name of the variable
     *
     * @return the name of the variable
     * */

    public String getName() {
        return name;
    }

    /**
     * Gets the type of the variable
     *
     * @return the type of the variable
     * */

    public Type getType() {
        return type;
    }

    /**
     * Determines if the variable has been declared
     *
     * @return true if the variable has been declared or false if not
     * */

    public boolean isDeclared() {
        return declared;
    }

    /**
     * Changes the variable's status to declared
     * */

    public void makeDeclared() {
        declared = true;
    }
}
