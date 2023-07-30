package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Pattern;

public class ConstantNode implements TreeNode {

    private String constantString;

    @Override
    public String generateCode() {
        return constantString;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO Previously defined constants???
        if(Pattern.compile("(-?)([0-9]+)((\\.[0-9]+)?)(f?)").matcher(rule).matches()) {
            constantString = rule;
            return 0;
        } else if(rule.equals("true") || rule.equals("false")) {
            constantString = rule;
            return 0;
        } else {
            throw new InvalidRuleStructureException(rule, "Constant Node");
        }
    }

    public static void main(String[] args) {
        // All work properly
        testValid("100");
        testValid("-100");
        testValid("-10.7");
        testValid("0.7f");
        testValid("-4.708765f");
        testValid("true");
        testValid("false");
        testValid("f");
        testValid(".");
        testValid("5.");
    }

    public static void testValid(String rule) {
        try {
            ConstantNode n = new ConstantNode();
            n.validateRule(rule);
            System.out.println(rule);
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }
}
