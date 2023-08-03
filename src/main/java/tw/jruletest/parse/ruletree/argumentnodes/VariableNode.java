package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Pattern;

public class VariableNode implements TreeNode {

    private String variableString;

    @Override
    public String generateCode() {
        return variableString;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO allow for boolean expressions
        // TODO Check whether variable exists
        if(Pattern.compile("[a-z][A-Z0-9a-z]*").matcher(rule).matches()) {
            variableString = rule;
            return 0;
        } else {
            throw new InvalidRuleStructureException(rule, "Variable Node");
        }

    }
}
