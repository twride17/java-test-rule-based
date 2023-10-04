package tw.jruletest.exceptions;

/**
 * Exception to be thrown when a rule has an invalid structure.
 *
 * @author Toby Wride
 * */

public class InvalidRuleStructureException extends Exception {

    /**
     * Instance field for the rule segment that has an incorrect structure
     * */

    private String ruleSegment;

    /**
     * Instance field for the name of the node type that the exception was thrown from
     * */

    private String nodeType;

    /**
     * Exception constructor
     *
     * @param rule the rule segment that caused the error
     * @param throwingNode String representation of the node that threw this exception
     * */

    public InvalidRuleStructureException(String rule, String throwingNode) {
        ruleSegment = rule;
        nodeType = throwingNode;
    }

    /**
     * Prints out an error message which includes the problematic segment and the node from which this exception was thrown
     * */

    public void printError() {
        System.out.println("Invalid format found in: " + ruleSegment + ".\nThrown from: " + nodeType);
    }
}
