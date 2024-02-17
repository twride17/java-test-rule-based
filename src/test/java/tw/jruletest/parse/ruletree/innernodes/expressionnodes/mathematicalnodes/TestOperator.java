package tw.jruletest.parse.ruletree.innernodes.expressionnodes.mathematicalnodes;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;
import tw.jruletest.parse.ruletree.innernodes.argumentnodes.ConstantNode;
import tw.jruletest.parse.ruletree.innernodes.argumentnodes.StringNode;

public class TestOperator {

    private static ConstantNode intNode;

    private static ConstantNode doubleNode;

    private static ConstantNode floatNode;

    private static StringNode stringNode;

    private Operator operator;

    @BeforeClass
    public static void setup() {
        try {
            intNode = new ConstantNode();
            intNode.validateRule("10");
            doubleNode = new ConstantNode();
            doubleNode.validateRule("8.5");
            floatNode = new ConstantNode();
            floatNode.validateRule("40.45f");
            stringNode = new StringNode();
            stringNode.validateRule("`Hello World`");
        } catch (InvalidRuleStructureException e) {}
    }

    @Test
    public void testGetFinalTypeStringConcatenation() {
        operator = new Operator('+');
        ChildNode[] leftNodes = {intNode, doubleNode, floatNode, stringNode, stringNode, stringNode, stringNode};
        ChildNode[] rightNodes = {stringNode, stringNode, stringNode, stringNode, intNode, doubleNode, floatNode};
        for(int i = 0; i < leftNodes.length; i++) {
            Assert.assertEquals(String.class, operator.getFinalType(leftNodes[i], rightNodes[i]));
        }
    }

    @Test
    public void testGetFinalTypeEquivalentOperands() {
        ChildNode[] nodes = {intNode, doubleNode, floatNode};
        Class<?>[] expectedTypes = {int.class, double.class, float.class};
        operator = new Operator('+');
        for(int i = 0; i < nodes.length; i++) {
            Assert.assertEquals(expectedTypes[i], operator.getFinalType(nodes[i], nodes[i]));
        }
    }

    @Test
    public void testGetFinalTypeDoubleLeftOperand() {
        char[] operators = {'+', '-', '*', '/'};
        ChildNode[] rightNodes = {intNode, floatNode, doubleNode};

        for(char operatorSymbol: operators) {
            operator = new Operator(operatorSymbol);
            for(ChildNode rightNode: rightNodes) {
                Assert.assertEquals(double.class, operator.getFinalType(doubleNode, rightNode));
            }
        }
    }

    @Test
    public void testGetFinalTypeDoubleRightOperand() {
        char[] operators = {'+', '-', '*', '/'};
        ChildNode[] leftNodes = {intNode, floatNode, doubleNode};

        for(char operatorSymbol: operators) {
            operator = new Operator(operatorSymbol);
            for(ChildNode leftNode: leftNodes) {
                Assert.assertEquals(double.class, operator.getFinalType(leftNode, doubleNode));
            }
        }
    }

    @Test
    public void testGetFinalTypeBooleanOperands() {
        char[] operators = {'+', '-', '*', '/'};
        try {
            ConstantNode leftBooleanNode = new ConstantNode();
            leftBooleanNode.validateRule("true");
            ConstantNode rightBooleanNode = new ConstantNode();
            rightBooleanNode.validateRule("false");

            for(char operatorSymbol: operators) {
                operator = new Operator(operatorSymbol);
                Assert.assertNull(operator.getFinalType(leftBooleanNode, intNode));
                Assert.assertNull(operator.getFinalType(doubleNode, rightBooleanNode));
                Assert.assertNull(operator.getFinalType(leftBooleanNode, rightBooleanNode));
            }
        } catch (InvalidRuleStructureException e) {}
    }

    @Test
    public void testInvalidTypeCombinations() {
        char[] operators = {'-', '*', '/'};
        ChildNode[] leftNodes = {stringNode, stringNode, stringNode, stringNode, intNode, doubleNode, floatNode};
        ChildNode[] rightNodes = {intNode, doubleNode, floatNode, stringNode, stringNode, stringNode, stringNode};

        for(int i = 0; i < operators.length; i++) {
            operator = new Operator(operators[i]);
            for(int j = 0; j < leftNodes.length; j++) {
                Assert.assertNull(operator.getFinalType(leftNodes[j], rightNodes[j]));
            }
        }
    }
}
