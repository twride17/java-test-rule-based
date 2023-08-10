package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestValueNode {

    private ValueNode node;

    /* Testing rule validation for Value node */

    @Test
    public void testGetValueOfValidVariable() {
        String rule = "value of xValue";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidVariable() {
        String rule = "value of 10x4y";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfValidMethod() {
        String rule = "Class.method";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidMethod() {
        String rule = "result of class.Method";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfMethodWithValidArguments() {
        String rule = "Class.method with arguments: `Hello world`, 10 and -0.89f, xValue";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfRulePlusExtraRule() {
        String rule = "value of Example.x and store in y";
        node = new ValueNode();
        try {
            Assert.assertEquals(18, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"value x", "of x", "result of", "result x", "value", "result"};
        node = new ValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Value node */
    @Test
    public void testCodeGeneration() {
        // TODO Update tests to use class fields eg: Example.x
        // Currently incorrectly determined to be Method nodes
        String[] rules = {"value of xValue", "Class.method", /*"Get value of Example.x and store in y",*/
                "Class.method with arguments: `Hello world`, 10 and -0.89f, xValue"};

        String[] expectedStrings = {"xValue", "Class.method()", /*"Example.x",*/
                "Class.method(\"Hello world\", 10, -0.89f, xValue)"};

        for(int i = 0; i < rules.length; i++) {
            node = new ValueNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }
}
