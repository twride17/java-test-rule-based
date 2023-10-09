package tw.jruletest.parse.ruletree.expressionnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class LogicalComparisonNode extends TreeNode {

    private final String[] POSSIBLE_COMPARATORS = {" less than or equal to ", " less than ", "greater than or equal to ",
                                                    " greater than ", " not equal to ", " equal to "};

    private TreeNode firstComparisonArgument;
    private TreeNode secondComparisonArgument;

    private String comparator;

    @Override
    public String generateCode() {
        String code = "(" + firstComparisonArgument.generateCode();
        switch(comparator.trim()) {
            case "equal to":
                code += " == ";
                break;
            case "not equal to":
                code += " != ";
                break;
            case "less than":
                code += " < ";
                break;
            case "less than or equal to":
                code += " <= ";
                break;
            case "greater than":
                code += " > ";
                break;
            case "greater than or equal to":
                code += " >= ";
                break;
        }
        return code + secondComparisonArgument.generateCode() + ")";
    }

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        int comparatorIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            int newIndex = ruleContent.indexOf(comparator);
            if((newIndex >= 0) && (comparatorIndex < 0)) {
                comparatorIndex = newIndex + comparator.length();
                this.comparator = comparator;
            }
        }

        String comparatorPhrase = " is" + comparator;
        int phraseIndex = ruleContent.indexOf(comparatorPhrase);
        if(phraseIndex == -1) {
            throw new InvalidRuleStructureException(ruleContent, "Expectation Node");
        }

        String firstSegment = ruleContent.substring(0, phraseIndex);
        String secondSegment = ruleContent.substring(phraseIndex + comparatorPhrase.length());

        firstComparisonArgument = TreeNode.getChildNode(firstSegment, TreeNode.OPERABLE_NODE);
        secondComparisonArgument = TreeNode.getChildNode(secondSegment, TreeNode.OPERABLE_NODE);
        int firstArgumentIndex = firstComparisonArgument.getEndIndex();
        int secondArgumentIndex = secondComparisonArgument.getEndIndex();

        if(firstArgumentIndex != phraseIndex) {
            throw new InvalidRuleStructureException(ruleContent, "Expectation Node");
        } else {
            endIndex = comparatorIndex + secondArgumentIndex;
        }
    }

    public static void main(String[] args) {
        String[] rules = {"2 is equal to 4", "`x` is not equal to `y`", "2 is less than 56", "-3.2 is less than or equal to 56.5f",
                            "34 % 3 is greater than 5", "45 is greater than 32 + 90", "1+2 is not equal to 3 *1", "-90 is equal to 32"};

        for(String rule: rules) {
            System.out.println(rule);
            TreeNode node = new LogicalComparisonNode();
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
