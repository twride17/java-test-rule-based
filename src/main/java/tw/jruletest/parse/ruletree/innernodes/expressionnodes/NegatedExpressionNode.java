package tw.jruletest.parse.ruletree.innernodes.expressionnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;

public class NegatedExpressionNode extends RuleNode implements Rule {

    private RuleNode negatedExpressionTree;

    @Override
    public String generateCode() {
        return "!" + ((Rule)negatedExpressionTree).generateCode();
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        String[] words = ruleContent.split(" ");
        try {
            if(words[0].equals("not") && (words.length != 1)) {
                negatedExpressionTree = RuleNode.getChildNode(ruleContent.substring(4), RuleNode.BOOLEAN_EXPRESSION_NODE);
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
            RuleNode node = new NegatedExpressionNode();
            try {
                ((Rule)node).validateRule(rule);
                System.out.println(rule.substring(0, node.getEndIndex()));
                System.out.println(((Rule)node).generateCode());
            } catch(InvalidRuleStructureException e) {
                System.out.println("Failed to validate");
            }
        }
    }
}
