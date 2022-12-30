package tw.jruletest.exceptions;

public class UnparsableRuleException extends Exception {

    private String rule;

    public UnparsableRuleException(String rule) {
        this.rule = rule;
    }

    public void printError() {
        System.out.println("Could not parse rule: " + rule);
    }
}
