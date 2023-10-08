package tw.jruletest.parse.ruletree.expressionnodes;

import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ConstantNode;
import tw.jruletest.parse.ruletree.rulenodes.ValueNode;

public class NegatedExpressionNode extends TreeNode {

    private TreeNode negatedExpressionTree;

    @Override
    public String generateCode() {
        return "!" + negatedExpressionTree.generateCode();
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        String[] words = ruleContent.split(" ");
        try {
            if(words[0].equals("not") && (words.length != 1)) {
                negatedExpressionTree = TreeNode.getChildNode(ruleContent.substring(4), TreeNode.BOOLEAN_EXPRESSION_NODE);
                endIndex = 4 + negatedExpressionTree.getEndIndex();
            } else {
                throw new InvalidRuleStructureException(ruleContent, "Negated Boolean Expression Node");
            }
        } catch(ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException(ruleContent, "Negated Boolean Expression Node");
        }
    }

    public static void main(String[] args) {
        String[] rules = {"not true and false", "not true and false or true", "not true or not false and true"};
        for(String rule: rules) {
            System.out.println(rule);
            TreeNode node = new NegatedExpressionNode();
            try {
                node.validateRule(rule);
                System.out.println(rule.substring(0, node.getEndIndex()));
                System.out.println(node.generateCode());
            } catch(InvalidRuleStructureException e) {
                System.out.println("Failed to validate");
            }
        }
    }
}