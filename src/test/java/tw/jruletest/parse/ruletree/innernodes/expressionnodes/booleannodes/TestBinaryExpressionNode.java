package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import org.junit.Assert;
import org.junit.Test;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;

public class TestBinaryExpressionNode {

    private BinaryExpressionNode node;

    /* Testing rule validation for BinaryExpressionNode */

    @Test
    public void testConjunctionExpression() {
        String[] expressions = {"1 is less than 2 and false", "true and false", "true and not 1 is equal to 9", "1 is equal to 3 and false"};
        for(String expression: expressions) {
            node = new BinaryExpressionNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testDisJunctionExpression() {
        String[] expressions = {"1 is less than 2 or false", "true or false", "true or not 1 is equal to 9", "1 is equal to 3 or false"};
        for(String expression: expressions) {
            node = new BinaryExpressionNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testCompoundExpressions() {
        String[] expressions = {"true and false or true", "true or false and true", "true and not false", "true or not true"};
        for(String expression: expressions) {
            node = new BinaryExpressionNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testInvalidExpressions() {
        String[] expressions = {"and", "or", "true not false", "and true", "or false", "not or false", "not and true", "true"};
        for(String expression: expressions) {
            node = new BinaryExpressionNode();
            try {
                node.validateRule(expression);
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            } catch(InvalidRuleStructureException e) { }
        }
    }

    /* Test for code generation of BinaryExpressionNode */

    @Test
    public void testCodeGeneration() {
        String[] expressions = {"1 is less than 2 or false", "true or false", "true or not 1 is equal to 9", "1 is equal to 3 or false",
                                "true and not false", "not true and false or true", "true or true or false", "true and false and true"};
        String[] expectedCode = {"((1 < 2) || false)", "(true || false)", "(true || !(1 == 9))", "((1 == 3) || false)",
                                "(true && !false)", "(!true && (false || true))", "(true || (true || false))", "(true && (false && true))"};
        for(int i = 0; i < expressions.length; i++) {
            node = new BinaryExpressionNode();
            try {
                node.validateRule(expressions[i]);
                Assert.assertEquals(expectedCode[i], node.generateCode());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expressions[i] + "' failed but should have passed validation");
            }
        }
    }
}
