package tw.jruletest.parse.ruletree.argumentnodes;

import java.lang.reflect.Type;

public abstract class ArgumentNode {

    protected String argumentString;

    public int getEndIndex() {
        return argumentString.length() - 1;
    }

    public abstract Type getType();
}
