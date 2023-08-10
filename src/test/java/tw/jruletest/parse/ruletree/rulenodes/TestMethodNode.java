package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestMethodNode {

    private MethodNode node;

    /* Testing rule validation for Method node */

    @Test
    public void testMethodCallNoArguments() {
        String[] rules = {"Call method Class.method", "call method Class.method", "method Class.method", "Call Class.method",
                            "call Class.method", "Class.method", "call Example.m"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallOneArgument() {
        String[] rules = {"Call method Class.method: `Hello world`", "method Class.method with arguments: 101.971f",
                            "call Class.method with: -24", "Class.method: xValue", "Class.method: -90.45f", "Class.method: true"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallTwoArguments() {
        String[] rules = {"Call method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: x1 and x2",
                            "Class.method: true, 123.45", "call Class.method with: `Hello and goodbye` and `String`",
                            "Class.method: 12.3 and xValue", "Class.method: -90.45f and true", "Class.method: true and 0"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallThreeArguments() {
        String[] rules = {"Call method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method with arguments: 24 and false, xValue", "call Class.method: xValue and -0.9f and false"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidRulesPlusExtraRule() {
        String[] rules = {"Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "call Class.method, store",
                            "Class.method: 1 in dummy", "Class.example with arguments: 0.0f, `String` and store",
                            "Class.method and store", "call Example.method: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "Call method Class.method, call method Example.method with arguments: `Hello` and `World`",
                            "Call method Class.method and call method Example.method with arguments: `Hello` and `World`"};
        int[] indices = {52, 17, 15, 44, 12, 59, 24};
        node = new MethodNode();
        for(int i = 0; i < 7; i++) {
            try {
                Assert.assertEquals(indices[i], node.validateRule(rules[i]));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"call", "call method", "call method method", "call xValue", "call Example.method with x",
                            "call Example.x with arguments x and y", "method Example.x: x, and y", "call Class.",
                            "Call Example.method with arguments: and 7", "Call Example.method with arguments: 7 and",
                            "Example.method arguments: x", " call Example.method with: x", "Example.method : x", ": x",
                            "call example.method", "call example.Method", "call Example.Method", "Call method .method",
                            "call example.method, with arguments: 1", "call example.method and with arguments: 1",
                            "call example.method in with arguments: 1", "call example.method then with arguments: 1"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                System.out.println(rule);
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Method node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"Call method Class.method", "call Test.exampleMethod", "Call method Class.method: `Hello world`",
                            "method Class.method with arguments: 101.971f", "call Class.method with: -24", "Class.method: xValue",
                            "Class.method: -90.45f", "method Class.method: true", "Call method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: true, 123.45",
                            "call Class.method with: `Hello and goodbye` and `String`", "Class.method: 12.3 and xValue", "Class.method: true and 0",
                            "Call method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "call Class.method, store",
                            "Class.method: 1 in dummy", "Class.method and store", "call Example.method: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "Call method Class.method and call method Example.method with arguments: `Hello` and `World`"};

        String[] expectedStrings = {"Class.method()", "Test.exampleMethod()", "Class.method(\"Hello world\")",
                                    "Class.method(101.971f)", "Class.method(-24)", "Class.method(xValue)",
                                    "Class.method(-90.45f)", "Class.method(true)", "Class.method(\"Hello, world and his wife\", -90.5f)",
                                    "Class.method(101.971f, false)", "Class.method(true, 123.45)", "Class.method(\"Hello and goodbye\", \"String\")",
                                    "Class.method(12.3, xValue)", "Class.method(true, 0)", "Class.method(\"Hello world\", 32, -67.5f)",
                                    "Class.method(101.971f, true, \"This\")", "Class.method(123, \"hello, it's me\", -0.98f, true)",
                                    "Class.method()", "Class.method(1)", "Class.method()",
                                    "Example.method(1, -2, 45.6f, true, \"New\", 56)", "Class.method()"};

        for(int i = 0; i < rules.length; i++) {
            node = new MethodNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }
}
