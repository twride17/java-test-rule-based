package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;
import tw.jruletest.translation.VariableStore;

public class GetValueNode implements TreeNode {

    private TreeNode valueNode;
    private String methodName;

    public GetValueNode(String method) {
        methodName = method;
    }

    @Override
    public String generateCode() {
        // TODO derive required variable type
        TreeNode subNode = ((ValueNode) valueNode).getSubNode();
        // Find method call if method - use valueNode
        String valueCall;
        if(subNode instanceof MethodNode) {
            String methodCall = ((MethodNode) subNode).getMethod();
            valueCall = methodCall.split("\\.")[1];
        } else {
            valueCall = ((VariableNode) subNode).getArgument();
        }

        // Append 'value' to call/variable/field
        if(!(valueCall.endsWith("Value") || valueCall.endsWith("value"))) {
            valueCall += "Value";
        }

        // Find next variable for VariableStore and add to code
        return VariableStore.getNextUnusedVariableName(methodName, valueCall) + " = " + valueNode.generateCode() + ";";
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
