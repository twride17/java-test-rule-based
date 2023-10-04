package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.*;

/**
 * Utility class designed to determine which ArgumentNode child is required.
 *
 * @author Toby Wride
 * */

public class Argument {

    /**
     * Utility method which finds the appropriate ArgumentNode child node to represent the provided argument.
     *
     * @param argument argument requiring validation
     *
     * @return the ArgumentNode child for which the entirety of the rule is valid
     *
     * @throws InvalidRuleStructureException thrown if none of the argument nodes can find a valid segment
     * */

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
