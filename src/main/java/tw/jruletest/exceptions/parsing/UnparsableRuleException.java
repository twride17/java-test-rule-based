package tw.jruletest.exceptions.parsing;

/**
 * Exception to be thrown when a rule cannot be parsed
 *
 * @author Toby Wride
 * */

public class UnparsableRuleException extends Exception implements RuleParsingError{

    private String rule;

    private InvalidRuleStructureException causingException;

    /**
     * Exception constructor
     *
     * @param rule the rule which failed to be parsed
     * */

    public UnparsableRuleException(String rule, InvalidRuleStructureException structureException) {
        causingException = structureException;
        this.rule = rule;
    }

    /**
     * Returns an error message stating that the specified rule could not be parsed and shows any causes of this
     *
     * @return full error message for the current rule
     * */

    public String getErrorMessage() {
        String message = "Failed to parse rule: " + rule + "\nCause:\n";
        for(String statement: causingException.getErrorMessage().split("\n")) {
            message += statement + "\n";
        }
        return message;
    }
}
