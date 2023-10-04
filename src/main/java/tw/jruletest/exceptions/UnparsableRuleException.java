package tw.jruletest.exceptions;

/**
 * Exception to be thrown when a rule cannot be parsed
 *
 * @author Toby Wride
 * */

public class UnparsableRuleException extends Exception {

    /**
     * Instance field for the rule that could not be parsed
     * */

    private String rule;

    /**
     * Exception constructor
     *
     * @param rule the rule which failed to be parsed
     * */

    public UnparsableRuleException(String rule) {
        this.rule = rule;
    }

    /**
     * Prints out an error message stating that the specified rule could not be parsed
     * */

    public void printError() {
        System.out.println("Could not parse rule: " + rule);
    }
}
