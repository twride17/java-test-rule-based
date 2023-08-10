package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestGetValueNode {

    private GetValueNode node;

    /* Testing rule validation for Get Value node */

    @Test
    public void testGetValueOfValidVariable() {
        String rule = "Get value of xValue";
        node = new GetValueNode();
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
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfValidMethod() {
        String rule = "Get Class.method";
        node = new GetValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidMethod() {
        String rule = "get result of class.Method";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfMethodWithValidArguments() {
        String rule = "Get Class.method with arguments: `Hello world`, 10 and -0.89f, xValue";
        node = new GetValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfString() {
        String rule = "Get value of `String`";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfConstant() {
        String rule = "Get value of -78.45f";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfRulePlusExtraRule() {
        String rule = "Get value of Example.x and store in y";
        node = new GetValueNode();
        try {
            Assert.assertEquals(22, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidGetValueRuleStructures() {
        String[] rules = {"get", "get value x", "get of x", "value x", "of x", "get result", "result"};
        node = new GetValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Get Value node */
    @Test
    public void testCodeGeneration() {
        // TODO Update tests to use class fields eg: Example.x
        // Currently incorrectly determined to be Method nodes
        String[] rules = {"Get value of xValue", "Get Class.method", /*"Get value of Example.x and store in y",*/
                            "Get Class.method with arguments: `Hello world`, 10 and -0.89f, xValue"};

        String[] expectedStrings = {"xValue", "Class.method()", /*"Example.x",*/ "Class.method(\"Hello world\", 10, -0.89f, xValue)"};

        for(int i = 0; i < rules.length; i++) {
            node = new GetValueNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }
}
