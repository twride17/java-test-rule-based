package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class GetValueNode implements TreeNode {

    private TreeNode valueNode;

    @Override
    public String generateCode() {
        // TODO derive valid variable name
        // TODO derive required variable type
        return "variable = " + valueNode.generateCode() + ";";
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        if(ruleContent.split(" ")[0].equalsIgnoreCase("get")) {
            if(ruleContent.length() > 4) {
                valueNode = new ValueNode();
                return 4 + valueNode.validateRule(ruleContent.substring(4));
            }
        }
        throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
    }
}
