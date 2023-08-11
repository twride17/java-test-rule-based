package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnidentifiedCallException;
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
        TreeNode subNode = ((ValueNode) valueNode).getSubNode();
        String valueCall;
        String type = "";
        String call;
        if(subNode instanceof MethodNode) {
            call = ((MethodNode) subNode).getMethod();
            valueCall = call.split("\\.")[1];
        } else {
            call = ((VariableNode) subNode).getArgument();
            valueCall = call;
        }

        try {
            type = TypeIdentifier.getType(JavaClassAnalyzer.getReturnType(call)) + " ";

            // Append 'value' to call/variable/field
            if (!(valueCall.endsWith("Value") || valueCall.endsWith("value"))) {
                valueCall += "Value";
            }
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            System.out.println(e.getMessage());
        }

        // Find next variable for VariableStore and add to code
        return type + VariableStore.getNextUnusedVariableName(methodName, valueCall) + " = " + valueNode.generateCode() + ";";
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
