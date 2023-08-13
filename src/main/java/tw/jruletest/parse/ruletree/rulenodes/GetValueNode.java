package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.translation.VariableStore;

import java.lang.reflect.Type;

public class GetValueNode implements TreeNode {

    private ValueNode valueNode;

    @Override
    public String generateCode() {
        Type type = valueNode.getType();
        String valueCall = valueNode.getCallName();

        if (!(valueCall.endsWith("Value") || valueCall.endsWith("value"))) {
            valueCall += "Value";
        }

        return TypeIdentifier.getType(type) + " " + VariableStore.getNextUnusedVariableName(Runner.getCurrentMethod(), valueCall, type) +
                                                " = " + valueNode.generateCode() + ";";
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
