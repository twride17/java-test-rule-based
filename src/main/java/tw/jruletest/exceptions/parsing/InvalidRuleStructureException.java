package tw.jruletest.exceptions.parsing;

/**
 * Exception to be thrown when a rule has an invalid structure.
 *
 * @author Toby Wride
 * */

public class InvalidRuleStructureException extends Exception implements RuleParsingError {

    private RuleParsingError causingError;

    private String nodeType;

    private String cause;

    /**
     * Exception constructor
     *
     * @param throwingNode String representation of the node that threw this exception
     * @param cause the reason for which this exception was thrown
     * @param causingError
     * */

    public InvalidRuleStructureException(String throwingNode, String cause, RuleParsingError causingError) {
        nodeType = throwingNode;
        this.cause = cause;
        this.causingError = causingError;
    }

    /**
     * Exception constructor
     *
     * @param throwingNode String representation of the node that threw this exception
     * @param cause the reason for which this exception was thrown
     * */

    public InvalidRuleStructureException(String throwingNode, String cause) {
        this(throwingNode, cause, null);
    }

    /**
     * Gets the error message containing the problematic segment, the node from which this exception was thrown
     * and any causes of this error
     *
     * @return error message for this exception
     * */

    public String getErrorMessage() {
        String error = nodeType + ": " + cause;
        if(causingError != null) {
            for(String errorMessage: causingError.getErrorMessage().split("\n")) {
                error += "\n\t" + errorMessage;
            }
        }
        return error;
    }
}
