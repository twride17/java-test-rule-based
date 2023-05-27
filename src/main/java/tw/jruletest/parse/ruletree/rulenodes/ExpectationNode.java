package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.Locale;

public class ExpectationNode implements TreeNode {

    private static final String[] POSSIBLE_COMPARATORS = {" equal "};

    public ExpectationNode() { }

    @Override
    public String generateCode() throws UnparsableRuleException {
        return null;
    }

    public static int validateRule(String ruleContent) {
        int comparatorIndex = -1;
        for(String comparator: POSSIBLE_COMPARATORS) {
            int newIndex = ruleContent.indexOf(comparator);
            if((newIndex >= 0) && (comparatorIndex < 0)) {
                comparatorIndex = newIndex + comparator.length();
            }
        }

        comparatorIndex += ruleContent.substring(comparatorIndex).split(" ")[0].length();
        try {
            System.out.println(ruleContent.charAt(comparatorIndex));
            if (ruleContent.charAt(comparatorIndex-1) == ',') {
                comparatorIndex -= 1;
            }
        } catch(StringIndexOutOfBoundsException e) {}

        return comparatorIndex;
    }
}
