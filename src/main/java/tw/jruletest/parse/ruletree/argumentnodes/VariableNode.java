package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.ruletree.TreeNode;

public class VariableNode implements TreeNode {

    @Override
    public String generateCode() throws UnparsableRuleException {
        return null;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO allow for boolean expressions
        // TODO Check whether variable exists
        return 0;
    }
}
