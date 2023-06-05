package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class ExpectationNode implements TreeNode {

    private static final String[] POSSIBLE_COMPARATORS = {" equal "};

    public ExpectationNode() {

    }

    @Override
    public String generateCode() {
        return null;
    }

    public static int validateRule(String ruleContent) throws InvalidRuleStructureException  {
        int comparatorIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            int newIndex = ruleContent.indexOf(comparator);
            if((newIndex >= 0) && (comparatorIndex < 0)) {
                comparatorIndex = newIndex + comparator.length();
            }
        }

        // TODO Change to allow for strings as expected value
        comparatorIndex += ruleContent.substring(comparatorIndex).split(" ")[0].length();
        try {
            if (ruleContent.charAt(comparatorIndex-1) == ',') {
                comparatorIndex -= 1;
            }
        } catch(StringIndexOutOfBoundsException e) {}

        return comparatorIndex;
    }
}
