package tw.jruletest.parse.ruletree.argumentnodes;

public class ArgumentNode {

    protected String argumentString;

    public int getEndIndex() {
        return argumentString.length() - 1;
    }
}
