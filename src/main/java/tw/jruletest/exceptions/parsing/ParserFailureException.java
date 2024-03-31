package tw.jruletest.exceptions.parsing;

import java.util.List;

public class ParserFailureException extends Exception implements RuleParsingError{

    private List<UnparsableRuleException> errors;

    public ParserFailureException(List<UnparsableRuleException> errors) {
        this.errors = errors;
    }

    public String getErrorMessage() {
        String fullError = "";
        for(UnparsableRuleException error: errors) {
            fullError += error.getMessage() + "\n";
        }
        return fullError;
    }

    public List<UnparsableRuleException> getErrors() {
        return errors;
    }
}
