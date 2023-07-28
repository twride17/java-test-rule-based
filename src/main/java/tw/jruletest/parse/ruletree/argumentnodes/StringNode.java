package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.ruletree.TreeNode;

public class StringNode implements TreeNode {

    @Override
    public String generateCode() throws UnparsableRuleException {
        return null;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // Assumes any character should be allowed in a string except '"'
        if(!rule.contains("\"")) {
            return 0;
        } else {
            throw new InvalidRuleStructureException(rule, "String Node");
        }
    }

    public static void main(String[] args) {
        // All work properly
        testValid("'Hello world'");
        testValid("'Hello \" this \" world'");
        testValid("'Hello world.'");
    }

    public static void testValid(String rule) {
        try {
            StringNode n = new StringNode();
            n.validateRule(rule);
            System.out.println(rule);
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }
}
