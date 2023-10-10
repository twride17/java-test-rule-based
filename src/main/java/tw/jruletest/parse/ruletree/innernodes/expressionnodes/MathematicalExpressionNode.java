package tw.jruletest.parse.ruletree.innernodes.expressionnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class MathematicalExpressionNode extends ChildNode implements Rule {

    private static final char[] OPERATORS = {'+', '-', '*', '/', '%'};

    private ChildNode firstValueNode;
    private ChildNode secondValueNode;

    private char operator;

    @Override
    public String generateCode() {
        return "(" + ((Rule)firstValueNode).generateCode() + " " + operator + " " + ((Rule)secondValueNode).generateCode() + ")";
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

        firstValueNode = RuleNode.getChildNode(firstValue, RuleNode.OPERABLE_NODE);
        secondValueNode = RuleNode.getChildNode(secondValue, RuleNode.OPERABLE_NODE);

        endIndex += firstValueNode.getEndIndex() + 1 + secondValueNode.getEndIndex();
    }

    public static void main(String[] args) {
        String[] rules = {"2 + -2", "3-2", "1*6", "2 / 4", "34 % 3", "-0.987f + 6", "67 - 3 + 4", "2+ 3", "4 *5"};
        for(String rule: rules) {
            System.out.println(rule);
            RuleNode node = new MathematicalExpressionNode();
            try {
                ((Rule)node).validateRule(rule);
                System.out.println(rule.substring(0, node.getEndIndex()));
                System.out.println(((Rule)node).generateCode());
            } catch(InvalidRuleStructureException e) {
                System.out.println("Failed to validate");
            }
        }
    }

    @Override
    public Type getType() {
        return firstValueNode.getType();
    }
}
