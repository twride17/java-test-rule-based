package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import org.junit.Assert;
import org.junit.Test;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestNegatedExpressionNode {

    private NegatedExpressionNode node;

    /* Testing rule validation for NegatedExpressionNode */

    @Test
    public void testValidNegationExpressions() {
        String[] expressions = {"not true", "not not true", "not 2 is not equal to 4", "not `x` is equal to `y`"};
        for(String expression: expressions) {
            node = new NegatedExpressionNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testInvalidNegationExpressions() {
        String[] expressions = {"no", "not", "is not"};
        for(String expression: expressions) {
            node = new NegatedExpressionNode();
            try {
                node.validateRule(expression);
                Assert.fail("Failed: '" + expression + "' passed but should have failed validation");
            } catch(InvalidRuleStructureException e) { }
        }
    }

    /* Test for code generation of NegatedExpressionNode */

    @Test
    public void testCodeGeneration() {
        String[] expressions = {"not true", "not not true", "not 2 is not equal to 4", "not `x` is equal to `y`"};
        String[] expectedCode = {"!true", "!!true", "!(2 != 4)", "!(\"x\" == \"y\")"};
        for(int i = 0; i < expressions.length; i++) {
            node = new NegatedExpressionNode();
            try {
                node.validateRule(expressions[i]);
                Assert.assertEquals(expectedCode[i], node.generateCode());
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expressions[i] + "' failed but should have passed validation");
            }
        }
    }
}
