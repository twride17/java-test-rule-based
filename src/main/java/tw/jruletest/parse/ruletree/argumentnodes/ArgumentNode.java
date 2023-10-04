package tw.jruletest.parse.ruletree.argumentnodes;

import java.lang.reflect.Type;

/**
 * Abstract class for nodes that represent an argument such as a primitive constant, String or variable
 *
 * @author Toby Wride
 * */

public abstract class ArgumentNode {

    /**
     * Instance field storing the content of the argument for use in code generation
     * */

    protected String argumentString;

    /**
     * Gets the index where the validity of the rule ends
     *
     * @return the index of the last valid character
     * */

    public int getEndIndex() {
        return argumentString.length() - 1;
    }

    /**
     * Abstract method for getting the type of the argument
     *
     * @return the type of the stored argument
     * */

    public abstract Type getType();
}
