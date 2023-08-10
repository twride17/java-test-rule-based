package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class CallMethodNode implements TreeNode {

    private MethodNode methodNode;

    @Override
    public String generateCode() {
        return methodNode.generateCode() + ";";
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        if(ruleContent.toLowerCase().startsWith("call ")) {
            methodNode = new MethodNode();
            return 5 + methodNode.validateRule(ruleContent.substring(5));
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Method Call Node");
        }
    }
}
