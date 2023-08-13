package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;
import tw.jruletest.translation.VariableStore;

import java.lang.reflect.Type;

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
        Type valueType;
        if(subNode instanceof MethodNode) {
            SourceMethod method = ((MethodNode) subNode).getMethod();
            valueType = method.getType();
            valueCall = method.getName();
        } else if(subNode instanceof FieldNode) {
            SourceField field = ((FieldNode) subNode).getField();
            valueType = field.getType();
            valueCall = field.getName();
        } else {
            valueCall = ((VariableNode)subNode).getArgument();
            valueType = VariableStore.findVariable(methodName, valueCall).getType();
        }

        type = TypeIdentifier.getType(valueType) + " ";

        if (!(valueCall.endsWith("Value") || valueCall.endsWith("value"))) {
            valueCall += "Value";
        }

        return type + VariableStore.getNextUnusedVariableName(methodName, valueCall, valueType) + " = " + valueNode.generateCode() + ";";
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
