package tw.jruletest.parse.ruletree.innernodes.expressionnodes.mathematicalnodes;

import org.junit.Assert;
import org.junit.Test;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestMathematicalExpressionNode {

    private MathematicalExpressionNode node;

    /* Testing rule validation for Method node */

    @Test
    public void testAdditionRuleValidation() {
        String[] expressions = {"3 + 7", "-3.56f + 2.01f", "2+-6", "0.3+-4", "-89+ 45", "67 +4", "-19 +-45", "0.00005+5f"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testSubtractionValidation() {
        String[] expressions = {"2--2", "3-2", "1 - 6", "2 -4", "34- 3", "-0.987f - 6", "67 - 3f", "-89 - 89", "0.5f- 6"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testMultiplicationValidation() {
        String[] expressions = {"2*-2", "3*2", "1 * 6", "2 *4", "34* 3", "-0.987f * 6", "67 * 3f", "-89 * 89", "0.5f* 6"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testDivisionValidation() {
        String[] expressions = {"2/-2", "3/-2", "1 / 6", "2 /4", "34/ 3", "-0.987f / 6", "67 / 3f", "-89 / 89", "0.5f/ 6"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testCompoundExpressions() {
        String[] expressions = {"-2--56+4", "2 -4 +6", "45f- 8+ 34f", "3 * 6 - 7", "9 / -6 -45", "-89 -90 -67 +0.5f/3"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
            } catch (InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] expressions = {"-2 + + -2", "3**2", "1-*6", "*2 / -4", "/34 - 3", "89 -", "+ 9", "-78", "90 = 9"};
        for(String expression: expressions) {
            try {
                node = new MathematicalExpressionNode();
                node.validateRule(expression);
                Assert.fail("Failed: '" + expression + "' passed but should have failed validation");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Test for code generation of MathematicalExpressionNode */

    @Test
    public void testCodeGeneration() {

    }
}
