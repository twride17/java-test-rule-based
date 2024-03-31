package tw.jruletest.exceptions.parsing;

import java.util.List;

public class ChildNodeSelectionException extends Exception implements RuleParsingError {

    private List<InvalidRuleStructureException> exceptions;

    private String rule;

    public ChildNodeSelectionException(List<InvalidRuleStructureException> exceptions, String rule) {
        this.rule = rule;
        this.exceptions = exceptions;
    }

    public String getErrorMessage() {
        String message = "Could not select suitable Child Node for '" + rule + "'\n";
        for(InvalidRuleStructureException exception: exceptions) {
            message += "\t" + exception.getErrorMessage() + "\n";
        }
        return message;
    }
}
