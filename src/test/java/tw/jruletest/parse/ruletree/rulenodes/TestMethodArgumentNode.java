package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestMethodArgumentNode {

    @Test
    public void testSingleStringArguments() {
        String[] rules = {"`Hello world`", "`Hello, world`", "`Hello and goodbye`", "`Hello world, and goodbye's`"};
        MethodArgumentNode node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testSingleConstantArguments() {
        String[] rules = {"1005", "-10.5", "105f", "-10.67f"};
        MethodArgumentNode node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testSingleVariableArguments() {
        String[] rules = {"x", "Example.x", "xValue", "xValue1"};
        MethodArgumentNode node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidDoubleArgumentRules() {
        String[] rules = {"`hello, and goodbye` and 67", "-0.567f, xValue", "false and `new string`"};
        MethodArgumentNode node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidTripleArgumentRules() {
        String[] rules = {"value1, 1234, `example`", "`hello world, who's there?`, -90.56 and true",
                            "xValue and `hello`, false", "true and 145.4f and -90.84"};
        MethodArgumentNode node = new MethodArgumentNode();
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
        String[] rules = {"123, `hello, it's me`, -0.98f and true and store value in y", "123 and 6, store", "1 in dummy"};
        int[] indices = {38, 9, 1};
        MethodArgumentNode node = new MethodArgumentNode();
        for(int i = 0; i < 3; i++) {
            try {
                Assert.assertEquals(indices[i], node.validateRule(rules[i]));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testEmptyRule() {
        String rule = "";
        try {
            (new MethodArgumentNode()).validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"hello`", "`hello", ",", "and", "and value", "and value and", "value and", "value,",
                            "value and value1 and and", "value and value1 and", "value1, and 2", ".5 and 5", "value1, ,",
                            "value1 and 10x", "value1 value2", "value1, value2 and value3 value4", "value1,value2", ",value1"};
        MethodArgumentNode node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }
}