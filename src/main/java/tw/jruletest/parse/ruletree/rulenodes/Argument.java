package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.*;

public class Argument {

    public static TreeNode getArgumentNode(String argument) throws InvalidRuleStructureException {
        TreeNode[] possibleNodes = {new StringNode(), new ConstantNode(), new VariableNode()};
        for(TreeNode possibleNode: possibleNodes) {
            try {
                possibleNode.validateRule(argument);
                return possibleNode;
            } catch(InvalidRuleStructureException e) { }
        }
        throw new InvalidRuleStructureException(argument, "Argument Type Selector");
    }

    public static void main(String[] args) {
        // All work
        test("`Hello World`");
        test("`Hello \" world`");
        test("0.7f");
        test("-3.7856");
        test("x");
        test("`x` and 76");
        test("`Hello it's me` and 6");
        test("xValue");
        test("xValue1x2");
        test("10x");
        test("h``h");
        test("x, y");
    }

    public static void test(String rule) {
        try {
            TreeNode node = getArgumentNode(rule);
            System.out.println(rule);
            System.out.println(node.getClass());
            System.out.println(node.generateCode());
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }

        System.out.println();
    }
}
