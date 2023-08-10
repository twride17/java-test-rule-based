package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ArgumentNode;

public class ExpectationNode implements TreeNode {

    /**
     * @author Toby Wride
     *
     * Rule tree node for expectation rules
     */

    private int keywordLength = 0;

    private TreeNode expectedValueTree;
    private TreeNode actualValueTree;
    private String comparator;

    private boolean negated = false;

    private static final String[] POSSIBLE_COMPARATORS = {" equal "};

    @Override
    public String generateCode() {
        String code = "Expectations.expect(" + expectedValueTree.generateCode() + ").to";
        if(negated) {
            code += "Not";
        }
        comparator = comparator.trim();
        code += comparator.substring(0, 1).toUpperCase() + comparator.substring(1) + "(";
        return code + actualValueTree.generateCode() + ");";
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException  {
        int comparatorIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            int newIndex = ruleContent.indexOf(comparator);
            if((newIndex >= 0) && (comparatorIndex < 0)) {
                comparatorIndex = newIndex + comparator.length();
                this.comparator = comparator;

                String[] segments = ruleContent.substring(0, newIndex).split(" ");
                negated = segments[segments.length-1].equals("not");
            }
        }

        String comparatorPhrase = " to";
        if(negated) {
            comparatorPhrase += " not";
        }
        comparatorPhrase += comparator;

        String remainingRule = ruleContent;
        if(ruleContent.toLowerCase().startsWith("expect ")) {
            keywordLength = 7;
        }
        remainingRule = remainingRule.substring(keywordLength);

        int phraseIndex = remainingRule.indexOf(comparatorPhrase);
        if(phraseIndex == -1) {
            throw new InvalidRuleStructureException(ruleContent, "Expectation Node");
        }

        String expectedSegment = remainingRule.substring(0, phraseIndex);
        String actualSegment = remainingRule.substring(phraseIndex + comparatorPhrase.length());

        int firstArgumentIndex;
        int secondArgumentIndex;
        TreeNode currentNode;

        try {
            currentNode = new ValueNode();
            firstArgumentIndex = currentNode.validateRule(expectedSegment);
            expectedValueTree = currentNode;
        } catch(InvalidRuleStructureException e) {
            try {
                currentNode = Argument.getArgumentNode(expectedSegment);
                firstArgumentIndex = ((ArgumentNode)currentNode).getEndIndex() + 1;
                expectedValueTree = currentNode;
            } catch(InvalidRuleStructureException e2) {
                throw new InvalidRuleStructureException(expectedSegment, "Expectation Node");
            }
        }

        try {
            currentNode = new ValueNode();
            secondArgumentIndex = currentNode.validateRule(actualSegment);
            actualValueTree = currentNode;
        } catch(InvalidRuleStructureException e) {
            try {
                currentNode = Argument.getArgumentNode(actualSegment);
                secondArgumentIndex = ((ArgumentNode)currentNode).getEndIndex() + 1;
                actualValueTree = currentNode;
            } catch(InvalidRuleStructureException e2) {
                throw new InvalidRuleStructureException(actualSegment, "Expectation Node");
            }
        }

        if(firstArgumentIndex != phraseIndex) {
            throw new InvalidRuleStructureException(remainingRule, "Expectation Node");
        } else {
            return comparatorIndex + secondArgumentIndex;
        }
    }
}
