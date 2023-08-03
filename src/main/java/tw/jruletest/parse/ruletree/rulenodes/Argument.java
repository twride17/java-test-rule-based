package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ConstantNode;
import tw.jruletest.parse.ruletree.argumentnodes.StringNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;

import java.util.regex.Pattern;

public class Argument {

    public static TreeNode getArgumentNode(String argument) throws InvalidRuleStructureException {
        TreeNode possibleNode;

        int possibleQuoteIndex = argument.indexOf("`");
        if((possibleQuoteIndex == 0) && (argument.charAt(argument.length()-1) == '`')) {
            possibleNode =  new StringNode();
        } else if(Pattern.compile("[a-z][a-z0-9A-Z]*").matcher(argument).matches()){
            possibleNode = new VariableNode();
        } else {
            possibleNode = new ConstantNode();
        }

        possibleNode.validateRule(argument);
        return possibleNode;
    }

    public static void main(String[] args) {
        // All work
        test("`Hello World`");
        test("`Hello \" world`");
        test("0.7f");
        test("-3.7856");
        test("x");
        test("x'");
        test("`Hello it's me`");
        test("xValue");
        test("xValue1x2");
        test("10x");
        test("h``h");
    }

    public static void test(String rule) {
        try {
            TreeNode node = getArgumentNode(rule);
            System.out.println(rule);
            System.out.println(node.getClass());
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }

        System.out.println();
    }
}
