package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class NegatedExpressionNode extends ChildNode implements Rule {

    private ChildNode negatedExpressionTree;

    @Override
    public String generateCode() {
        return "!" + ((Rule)negatedExpressionTree).generateCode();
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        String[] words = ruleContent.trim().split(" ");
        try {
            if(words[0].equals("not") && (words.length != 1)) {
                negatedExpressionTree = RuleNode.getChildNode(ruleContent.substring(4), RuleNode.BOOLEAN_EXPRESSION_NODE);
                endIndex = 4 + negatedExpressionTree.getEndIndex();
            } else {
                throw new InvalidRuleStructureException("Negated Boolean Expression Node", "Rule must contain more than " +
                                                        "one word, the first of which must be 'not'");
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException("Negated Boolean Expression Node", "Rule cannot be empty");
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Negated Boolean Expression Node", "Could not find valid node for '"
                                                    + ruleContent.substring(4) + "'. Caused by: ", e);
        }
    }

    @Override
    public Type getType() {
        return boolean.class;
    }
}
