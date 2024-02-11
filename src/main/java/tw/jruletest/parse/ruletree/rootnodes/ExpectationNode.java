package tw.jruletest.parse.ruletree.rootnodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;

/**
 * Rule node that deals with creating expectation objects.
 * This node is designed to be the root node of a tree generated from rules that start with the keyword 'expect'.
 *
 * @author Toby Wride
 * */

public class ExpectationNode extends RootNode implements Rule {

    private int keywordLength = 0;

    private RuleNode expectedValueTree;
    private RuleNode actualValueTree;
    private String comparator;

    private boolean negated = false;

    private static final String[] POSSIBLE_COMPARATORS = {" equal "};

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves code generation from both child nodes (or only one if boolean) and creating the appropriate expectation object.
     *
     * @return a full line of code containing the expectation object with the first and second values as code generated from the child nodes.
     * */

    @Override
    public String generateCode() {
        ImportCollector.addImport("import tw.jruletest.expectations.*;");

        String code = "Expectations.expect(" + ((Rule)expectedValueTree).generateCode() + ").to";
        if(negated) {
            code += "Not";
        }
        comparator = comparator.trim();
        code += comparator.substring(0, 1).toUpperCase() + comparator.substring(1) + "(";
        return code + ((Rule)actualValueTree).generateCode() + ");";
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks if the first word is the keyword 'expect' and one of the possible comparators exists in the rule with possible
     * negation allowed. Assuming the basic structure is valid, the validity of the arguments for the first and second
     * values are checked by the appropriate child nodes.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not start with the 'expect' keyword, does not contain
     * one of allowed comparison keywords or one of the child nodes was not able to find a valid segment
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException  {
        String remainingRule = ruleContent;
        // Throw exception if not???
        if(ruleContent.toLowerCase().startsWith("expect ")) {
            keywordLength = 7;
        }
        remainingRule = remainingRule.substring(keywordLength);

        int bestComparatorIndex = ruleContent.length();
        String comparatorPhrase = "";
        int comparingPhraseIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            String ruleEnding = remainingRule;
            boolean finishedSearch = false;
            while(!finishedSearch) {
                int currentIndex = ruleEnding.indexOf(comparator);
                if(currentIndex == -1) {
                    finishedSearch = true;
                } else {
                    String[] previousSegments = ruleEnding.substring(0, currentIndex).split(" ");
                    negated = previousSegments[previousSegments.length - 1].equals("not");

                    String currentComparatorPhrase = " to";
                    if (negated) {
                        currentComparatorPhrase += " not";
                    }
                    currentComparatorPhrase += comparator;

                    comparingPhraseIndex = remainingRule.indexOf(currentComparatorPhrase);
                    if (comparingPhraseIndex != -1) {
                        this.comparator = comparator;
                        comparatorPhrase = currentComparatorPhrase;
                        bestComparatorIndex = comparingPhraseIndex + currentComparatorPhrase.length();
                        finishedSearch = true;
                    } else {
                        ruleEnding = ruleEnding.substring(currentIndex + comparator.length());
                    }
                }
            }
        }

        try {
            String expectedSegment = remainingRule.substring(0, comparingPhraseIndex);
            String actualSegment = remainingRule.substring(comparingPhraseIndex + comparatorPhrase.length());

            expectedValueTree = RuleNode.getChildNode(expectedSegment, RuleNode.CHILD_NODE);
            actualValueTree = RuleNode.getChildNode(actualSegment, RuleNode.CHILD_NODE);
            int firstArgumentIndex = expectedValueTree.getEndIndex();
            int secondArgumentIndex = actualValueTree.getEndIndex();

            if (firstArgumentIndex != comparingPhraseIndex) {
                throw new InvalidRuleStructureException(remainingRule, "Expectation Node");
            } else {
                endIndex = keywordLength + bestComparatorIndex + secondArgumentIndex;
            }
        } catch(StringIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException(ruleContent, "Expectation Node");
        }
    }
}
