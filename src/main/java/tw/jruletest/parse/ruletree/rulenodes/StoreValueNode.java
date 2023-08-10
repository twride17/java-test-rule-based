package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreValueNode implements TreeNode {

    private TreeNode valueTree;
    private TreeNode variableTree;

    @Override
    public String generateCode() {
        // TODO add typing of variables
        return variableTree.generateCode() + " = " + valueTree.generateCode() + ";";
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^(((S|s)tore\\s)?(.+))\\s(in)\\s([a-z][a-zA-Z0-9]*)").matcher(ruleContent);
        if(matcher.find()) {
            String requiredSegment;
            int endIndex;
            if(ruleContent.toLowerCase().startsWith("store")) {
                endIndex = 6;
            } else {
                endIndex = 0;
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.isEmpty()) {
                throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
            }

            valueTree = new GetValueNode();
            try {
                endIndex += valueTree.validateRule(requiredSegment);
            } catch(InvalidRuleStructureException e) {
                try {
                    valueTree = Argument.getArgumentNode(requiredSegment);
                    endIndex += valueTree.generateCode().length();
                } catch(InvalidRuleStructureException e2) {
                    throw new InvalidRuleStructureException(requiredSegment, "Get Value Node");
                }
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.startsWith(" in") && !requiredSegment.trim().equals("in")) {
                requiredSegment = requiredSegment.substring(4);
                endIndex += 4;
            }

            variableTree = new VariableNode();
            return endIndex + variableTree.validateRule(requiredSegment);
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
        }
    }
}
