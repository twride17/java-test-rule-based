package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class BinaryExpressionNode extends ChildNode implements Rule {

    private static final String[] CONNECTIVES = {" and ", " or "};

    private String connective;

    private ChildNode firstPredicateTree;
    private ChildNode secondPredicateTree;

    @Override
    public String generateCode() {
        String connectiveCode;
        if(connective.trim().equals("and")) {
            connectiveCode = "&&";
        } else {
            connectiveCode = "||";
        }
        return "(" + ((Rule)firstPredicateTree).generateCode() + " " + connectiveCode + " " + ((Rule)secondPredicateTree).generateCode() + ")";
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        int closestIndex = ruleContent.length();
        int bestConnectiveIndex = -1;
        for(int i = 0; i < CONNECTIVES.length; i++) {
            int currentIndex = ruleContent.indexOf(CONNECTIVES[i]);
            if((currentIndex != -1) && (currentIndex < closestIndex)) {
                closestIndex = currentIndex;
                bestConnectiveIndex = i;
            }
        }

        try {
            connective = CONNECTIVES[bestConnectiveIndex];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException("Binary Boolean Expression Node", "Failed to find one of the valid connectives 'and', 'or'");
        }

        String firstPredicateRule = ruleContent.substring(0, closestIndex);
        try {
            firstPredicateTree = RuleNode.getChildNode(firstPredicateRule, RuleNode.BOOLEAN_EXPRESSION_NODE);
            if (firstPredicateTree.getEndIndex() != closestIndex) {
                throw new InvalidRuleStructureException("Binary Boolean Expression Node", "Extra characters found after validation of '"
                                                            + firstPredicateRule + "'");
            }
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Binary Boolean Expression Node", "Could not find valid child node for '"
                                                        + firstPredicateRule + "'. Caused by: ", e);
        }

        String secondPredicateRule = ruleContent.substring(closestIndex + connective.length());
        try {
            secondPredicateTree = RuleNode.getChildNode(secondPredicateRule, RuleNode.BOOLEAN_EXPRESSION_NODE);
            if ((firstPredicateTree.getType() == boolean.class) && (secondPredicateTree.getType() == boolean.class)) {
                endIndex += firstPredicateTree.getEndIndex() + connective.length() + secondPredicateTree.getEndIndex();
            } else {
                throw new InvalidRuleStructureException("Binary Boolean Expression Node", "One or both of the arguments " +
                                                        "found are not of boolean types");
            }
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Binary Boolean Expression Node", "Could not find valid child node for '"
                                                        + secondPredicateRule + "'. Caused by: ", e);
        }
    }

    @Override
    public Type getType() {
        return boolean.class;
    }
}
