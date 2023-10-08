package tw.jruletest.parse.ruletree.expressionnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ConstantNode;
import tw.jruletest.parse.ruletree.argumentnodes.StringNode;
import tw.jruletest.parse.ruletree.rulenodes.ValueNode;

public class MathematicalExpressionNode extends TreeNode {

    private static final char[] OPERATORS = {'+', '-', '*', '/', '%'};

    private TreeNode firstValueNode;
    private TreeNode secondValueNode;

    private char operator;

    @Override
    public String generateCode() {
        return "(" + firstValueNode.generateCode() + " " + operator + " " + secondValueNode.generateCode() + ")";
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        int closestIndex = ruleContent.length();
        int bestOperatorIndex = -1;
        for(int i = 0; i < OPERATORS.length; i++) {
            int currentIndex = ruleContent.indexOf(OPERATORS[i]);
            if((currentIndex != -1) && (currentIndex < closestIndex)) {
                if(!(currentIndex == 0 && OPERATORS[i] == '-')) {
                    closestIndex = currentIndex;
                    bestOperatorIndex = i;
                }
            }
        }

        try {
            operator = OPERATORS[bestOperatorIndex];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException(ruleContent, "Mathematical Expression Node");
        }

        String firstValue = ruleContent.substring(0, closestIndex);
        if(firstValue.charAt(firstValue.length()-1) == ' ') {
            firstValue = firstValue.trim();
            endIndex += 1;
        }

        String secondValue = ruleContent.substring(closestIndex + 1);
        if(secondValue.charAt(0) == ' ') {
            secondValue = secondValue.trim();
            endIndex += 1;
        }

        firstValueNode = TreeNode.getChildNode(firstValue, TreeNode.OPERABLE_NODE);
        secondValueNode = TreeNode.getChildNode(secondValue, TreeNode.OPERABLE_NODE);

        endIndex += firstValueNode.getEndIndex() + 1 + secondValueNode.getEndIndex();
    }

    public static void main(String[] args) {
        String[] rules = {"2 + -2", "3-2", "1*6", "2 / 4", "34 % 3", "-0.987f + 6", "67 - 3 + 4", "2+ 3", "4 *5"};
        for(String rule: rules) {
            System.out.println(rule);
            TreeNode node = new MathematicalExpressionNode();
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
