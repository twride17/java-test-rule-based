package tw.jruletest.exceptions;

public class InvalidRuleStructureException extends Exception {
    private String ruleSegment;
    private String nodeType;

    public InvalidRuleStructureException(String rule, String throwingNode) {
        ruleSegment = rule;
        nodeType = throwingNode;
    }

    public void printError() {
        System.out.println("Invalid format found in: " + ruleSegment + ".\nThrown from: " + nodeType);
    }
}
