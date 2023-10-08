package tw.jruletest.parse.ruletree.expressionnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class BinaryBooleanExpressionNode extends TreeNode {

    private static final String[] CONNECTIVES = {" and ", " or "};

    private String connective;

    private TreeNode firstPredicateTree;
    private TreeNode secondPredicateTree;

    @Override
    public String generateCode() {
        String connectiveCode;
        if(connective.trim().equals("and")) {
            connectiveCode = "&&";
        } else {
            connectiveCode = "||";
        }
        return "(" + firstPredicateTree.generateCode() + " " + connectiveCode + " " + secondPredicateTree.generateCode() + ")";
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
            throw new InvalidRuleStructureException(ruleContent, "Mathematical Expression Node");
        }

        firstPredicateTree = TreeNode.getChildNode(ruleContent.substring(0, closestIndex), TreeNode.BOOLEAN_EXPRESSION_NODE);
        secondPredicateTree = TreeNode.getChildNode(ruleContent.substring(closestIndex + connective.length()), TreeNode.BOOLEAN_EXPRESSION_NODE);

        endIndex += firstPredicateTree.getEndIndex() + connective.length() + secondPredicateTree.getEndIndex();
    }

    public static void main(String[] args) {
        String[] rules = {"true and false", "true and false or true", "true and false and true", "true and not false",
                            "not true and false", "not true and not false", "not true or not false", "true or not false and true"};
        for(String rule: rules) {
            System.out.println(rule);
            TreeNode node = new BinaryBooleanExpressionNode();
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
