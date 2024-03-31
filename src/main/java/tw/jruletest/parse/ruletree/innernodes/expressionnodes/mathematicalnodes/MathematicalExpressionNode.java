package tw.jruletest.parse.ruletree.innernodes.expressionnodes.mathematicalnodes;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class MathematicalExpressionNode extends ChildNode implements Rule {

    private static final char[] OPERATORS = {'+', '-', '*', '/', '%'};

    private ChildNode firstValueNode;
    private ChildNode secondValueNode;

    private Operator operator;

    @Override
    public String generateCode() {
        return "(" + ((Rule)firstValueNode).generateCode() + " " + operator.getOperator()+ " " + ((Rule)secondValueNode).generateCode() + ")";
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        endIndex += ruleContent.length() - ruleContent.trim().length();
        ruleContent = ruleContent.trim();

        int firstValueStartIndex = 0;
        if(ruleContent.charAt(0) == '-') {
            firstValueStartIndex = 1;
        }

        int closestIndex = ruleContent.length();
        int bestOperatorIndex = -1;
        char bestOperator;
        for(int i = 0; i < OPERATORS.length; i++) {
            int currentIndex = ruleContent.substring(firstValueStartIndex).indexOf(OPERATORS[i]);
            if((currentIndex != -1) && (currentIndex < closestIndex)) {
                if(!(currentIndex == 0 && OPERATORS[i] == '-')) {
                    closestIndex = currentIndex;
                    bestOperatorIndex = i;
                }
            }
        }
        closestIndex += firstValueStartIndex;

        try {
            bestOperator = OPERATORS[bestOperatorIndex];
            operator = new Operator(bestOperator);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Could not find one of the valid operators: +, -, *, /, %");
        }

        String firstValue = ruleContent.substring(0, closestIndex);
        if(firstValue.isBlank()) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Expressions cannot start with an operator");
        }

        endIndex += firstValue.length() - firstValue.trim().length();

        try {
            firstValueNode = RuleNode.getChildNode(firstValue.trim(), RuleNode.OPERABLE_NODE);
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Failed to find child node for '" +
                                                    firstValue.trim() + "'. Caused by: ", e);

        }
        if(firstValueNode.getEndIndex() != closestIndex - endIndex) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Extra characters found after validation of '"
                                                    + firstValue.trim() + "'");
        }

        String secondValue = ruleContent.substring(closestIndex + 1);
        if(secondValue.isBlank()) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Expressions cannot end with an operator");
        }

        if(secondValue.charAt(0) == ' ') {
            secondValue = secondValue.trim();
            endIndex += 1;
        }
        endIndex += secondValue.length() - secondValue.trim().length();
        try {
            secondValueNode = RuleNode.getChildNode(secondValue.trim(), RuleNode.OPERABLE_NODE);
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Mathematical Expression Node", "Failed to find child node for '" +
                                                    secondValue.trim() + "'. Caused by: ", e);
        }

        endIndex += firstValueNode.getEndIndex() + 1 + secondValueNode.getEndIndex();
    }

    @Override
    public Type getType() {
        return operator.getFinalType(firstValueNode, secondValueNode);
    }
}
