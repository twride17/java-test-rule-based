package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

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
        variableString = rule;
        return 0;
    }
}
