package tw.jruletest.parse.ruletree.rootnodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;

/**
 * Rule node that deals with creating expectation objects.
 * This node is designed to be the root node of a tree generated from rules that start with the keyword 'expect'.
 *
 * @author Toby Wride
 * */

public class ExpectationNode extends RootNode implements Rule {

    private static final String[] POSSIBLE_COMPARATORS = {" equal "};

    private RuleNode expectedValueTree;
    private RuleNode actualValueTree;
    private String comparator;

    private boolean negated = false;

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
        int bestComparatorIndex = 0;
        String comparatorPhrase = "";
        int comparingPhraseIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            String ruleEnding = ruleContent;
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

                    comparingPhraseIndex = ruleEnding.indexOf(currentComparatorPhrase);
                    if (comparingPhraseIndex != -1) {
                        this.comparator = comparator;
                        comparatorPhrase = currentComparatorPhrase;
                        bestComparatorIndex += comparingPhraseIndex;
                        finishedSearch = true;
                    } else {
                        ruleEnding = ruleEnding.substring(currentIndex + comparator.length());
                        bestComparatorIndex += currentIndex + comparator.length();
                        String x = ruleContent.substring(0, bestComparatorIndex);
                        System.out.println(x);
                    }
                }
            }
        }

        int firstArgumentIndex;
        try {
            String expectedSegment = ruleContent.substring(0, bestComparatorIndex);
            expectedValueTree = RuleNode.getChildNode(expectedSegment, RuleNode.CHILD_NODE);
            firstArgumentIndex = expectedValueTree.getEndIndex();
            if (firstArgumentIndex != bestComparatorIndex) {
                throw new InvalidRuleStructureException("Expectation Node",
                                                        "Length of validated first argument doesn't match location of " +
                                                                "found comparator. Check rule for unexpected extra characters");
            }
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Expectation Node - Expected Argument", "Caused by:", e);
        } catch(StringIndexOutOfBoundsException e) {
            throw new InvalidRuleStructureException("Expectation Node - Expected Argument",
                                                    "Could not find any of the valid comparators: equal");
        }

        int secondArgumentIndex;
        try {
            String actualSegment = ruleContent.substring(bestComparatorIndex + comparatorPhrase.length());
            actualValueTree = RuleNode.getChildNode(actualSegment, RuleNode.CHILD_NODE);
            secondArgumentIndex = actualValueTree.getEndIndex();
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Expectation Node - Actual Argument", "Caused by:", e);
        }
        endIndex = bestComparatorIndex + comparatorPhrase.length() + secondArgumentIndex;
    }
}
