package tw.jruletest.parse.ruletree.innernodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.innernodes.MethodArgumentNode;
import tw.jruletest.variables.VariableStore;

public class TestMethodArgumentNode {

    private MethodArgumentNode node;

    /* Testing rule validation for MethodArgumentNode */

    @Before
    public void setup() {
        String[] variables = {"x", "xValue", "xValue1", "value1"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testSingleStringArguments() {
        String[] rules = {"`Hello world`", "`Hello, world`", "`Hello and goodbye`", "`Hello world, and goodbye's`"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testSingleConstantArguments() {
        String[] rules = {"1005", "-10.5", "105f", "-10.67f"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testSingleVariableArguments() {
        String[] rules = {"x", "xValue", "xValue1"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidDoubleArgumentRules() {
        String[] rules = {"`hello, and goodbye` and 67", "-0.567f, xValue", "false and `new string`"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidTripleArgumentRules() {
        String[] rules = {"value1, 1234, `example`", "`hello world, who's there?`, -90.56 and true",
                            "xValue and `hello`, false", "true and 145.4f and -90.84"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidRulesPlusExtraRule() {
        String[] rules = {"123, `hello, it's me`, -0.98f and true and store value in y", "123 and 6, store", "1 in dummy"};
        int[] indices = {38, 9, 1};
        node = new MethodArgumentNode();
        for(int i = 0; i < 3; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(indices[i], node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testEmptyRule() {
        String rule = "";
        node = new MethodArgumentNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"hello`", "`hello", ",", "and", "and value", "and value and", "value and", "value,",
                            "value and value1 and and", "value and value1 and", "value1, and 2", ".5 and 5", "value1, ,",
                            "value1 and 10x", "value1 value2", "value1, value2 and value3 value4", "value1,value2", ",value1"};
        node = new MethodArgumentNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for MethodArgumentNode */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"`Hello world`", "-10.67f", "`hello, and goodbye` and 67", "-0.567f, xValue", "false and `new string`",
                            "123, `hello, it's me`, -0.98f and true and store value in y", "value1, 12.34f, `example`"};

        String[] expectedStrings = {"\"Hello world\"", "-10.67f", "\"hello, and goodbye\", 67", "-0.567f, xValue",
                            "false, \"new string\"", "123, \"hello, it's me\", -0.98f, true", "value1, 12.34f, \"example\""};

        for(int i = 0; i < rules.length; i++) {
            node = new MethodArgumentNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }

    @After
    public void teardown() {
        VariableStore.reset();
    }
}