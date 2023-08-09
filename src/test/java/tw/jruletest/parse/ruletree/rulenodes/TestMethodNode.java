package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestMethodNode {

    @Test
    public void testMethodCallNoArguments() {
        String[] rules = {"Call method Class.method", "call method Class.method", "method Class.method", "Call Class.method",
                            "call Class.method", "Class.method"};
        MethodNode node = new MethodNode();
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
        MethodNode node = new MethodNode();
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
        MethodNode node = new MethodNode();
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
        MethodNode node = new MethodNode();
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
                            "Class.method and store", "call Example.method: 1, -2 and 45.6f, true and `New` and 56 in xValue"};
        int[] indices = {52, 17, 15, 44, 12, 59};
        MethodNode node = new MethodNode();
        for(int i = 0; i < 3; i++) {
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
                            "Example.method arguments: x", /*Not working*/ " call Example.method with: x", "Example.method : x", ": x",
                            "call example.method", "call example.Method", "call Example.Method", "Call method .method"};
        MethodNode node = new MethodNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }
}
