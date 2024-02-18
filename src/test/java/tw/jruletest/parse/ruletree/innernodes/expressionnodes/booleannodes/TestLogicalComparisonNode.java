package tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes;

import org.junit.Assert;
import org.junit.Test;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestLogicalComparisonNode {

    private LogicalComparisonNode node;

    /* Testing rule validation for LogicalComparisonNode */

    @Test
    public void testEqualityExpression() {
        String[] expressions = {"56 is equal to -90", "`Hello` is equal to `World`", "1+2 is equal to 3", "0 is equal to 10 + -10"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testInequalityExpression() {
        String[] expressions = {"56 is not equal to -90", "`Hello` is not equal to `World`", "1.5/6 is not equal to 3.5f"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testGreaterThanEqualExpression() {
        String[] expressions = {"56 is greater than or equal to 7", "1.5 - 9 is greater than or equal to 3.5f"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testLessThanEqualExpression() {
        String[] expressions = {"56 is less than or equal to 7", "1.5 - 9 is less than or equal to 3.5f"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testGreaterThanExpression() {
        String[] expressions = {"56 is greater than 7", "1.5 - 9 is greater than 3.5f", "0 is greater than -90 - 8"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testLessThanExpression() {
        String[] expressions = {"56 is less than 7", "1.5 - 9 is less than 3.5f", "0 is less than -90 - 8"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expression + "' failed but should have passed validation");
            }
        }
    }

    @Test
    public void testInvalidExpressions() {
        String[] expressions = {"56 is 7", "9 to 3.5f", "equal 6", "5 not equal", "5 greater equal than 9", "7 not not equal 9"};
        for(String expression: expressions) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expression);
                Assert.fail("Failed: '" + expression + "' passed but should have failed validation");
            } catch(InvalidRuleStructureException e) { }
        }
    }

    /* Test for code generation of LogicalComparisonNode */

    @Test
    public void testCodeGeneration() {
        String[] expressions = {"2 is equal to 4", "`x` is not equal to `y`", "2 is less than 56", "-3.2 is less than or equal to 56.5f",
                                "34 * 3 is greater than 5", "45 is greater than 32 + 90", "1+2 is not equal to 3 *1", "-90 is equal to 32"};
        String[] expectedCode = {"(2 == 4)", "(\"x\" != \"y\")", "(2 < 56)", "(-3.2 <= 56.5f)", "((34 * 3) > 5)",
                                "(45 > (32 + 90))", "((1 + 2) != (3 * 1))", "(-90 == 32)"};

        for(int i = 0; i < expressions.length; i++) {
            node = new LogicalComparisonNode();
            try {
                node.validateRule(expressions[i]);
                Assert.assertEquals(expectedCode[i], node.generateCode());
            } catch(InvalidRuleStructureException e) {
                Assert.fail("Failed: '" + expressions[i] + "' failed but should have passed validation");
            }
        }
    }
}
