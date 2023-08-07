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

    public ExpectationNode() {

    }

    @Override
    public String generateCode() {
        return null;
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
            currentNode = new GetValueNode();
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
            currentNode = new GetValueNode();
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

    public static void testValid(String rule) {
        try {
            ExpectationNode n = new ExpectationNode();
            System.out.println(rule);
            System.out.println(n.validateRule(rule));
            System.out.println(rule.length());
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        testValid("Expect 2 to equal 4");
        testValid("Expect x to equal -0.9874f");
        testValid("Expect string to equal `Hello there`");
        testValid("Expect value of Class.method with: `Hello, this is cool` to equal 4");
        testValid("Expect Class.method to equal 3");
        testValid("Expect Class.method: to equal 3");
        testValid("Expect result of Class.method to equal `New and cool string`");
        testValid("Expect -982 to not equal 4");
        testValid("Expect xValue1 to equal 5");
        testValid("Expect Example.method with arguments: 3, 56 and `Hello` to equal 4");
        testValid("Expect Example.method: 3, 56 and Hello` to equal 4");
        testValid("Expect 4 to not equal Example.method: 3, 56 and `Hello`");
        testValid("Expect value Example.method: 3, 56 and `Hello` to equal 4");
        testValid("Expect of Example.method: 3, 56 and `Hello` to equal 4");
        testValid("Expect value of Example.method: `Hello and 56 to equal 4");
        testValid("0 to not equal value of Class.method");
        testValid("0 to equal value of Class.method");
    }
}
