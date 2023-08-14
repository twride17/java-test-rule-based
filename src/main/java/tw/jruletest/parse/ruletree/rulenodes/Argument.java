package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.*;

public class Argument {

    public static TreeNode getArgumentNode(String argument) throws InvalidRuleStructureException {
        TreeNode[] possibleNodes = {new StringNode(), new ConstantNode(), new VariableNode()};
        for(TreeNode possibleNode: possibleNodes) {
            try {
                possibleNode.validateRule(argument);
                return possibleNode;
            } catch(InvalidRuleStructureException e) { }
        }
        throw new InvalidRuleStructureException(argument, "Argument Type Selector");
    }
}
