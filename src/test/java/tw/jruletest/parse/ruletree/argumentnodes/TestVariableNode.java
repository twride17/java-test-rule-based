package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.translation.VariableStore;

public class TestVariableNode {

    private VariableNode node = new VariableNode();

    /* Testing rule validation for Variable node */

    @Before
    public void setup() {
        String[] variables = {"xValue1", "value1", "xValue", "x", "value1", "x1", "floatValue"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testValidLocalVariable() {
        String rule = "xValue1";
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
            Assert.assertEquals(rule, node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
    }

    @Test
    public void testFieldResult() {
        String rule = "Example.x";
        try {
            node.validateRule(rule);
            Assert.fail("Rule '" + rule + "': passed when expected to fal");
        } catch (InvalidRuleStructureException e) {}
    }

    @Test
    public void testValidLocalVariablePlusExtraRule() {
        String rule = "xValue and other stuff";
        try {
            Assert.assertEquals(6, node.validateRule(rule));
            Assert.assertEquals("xValue", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
    }

    @Test
    public void testInvalidVariables() {
        String[] rules = {"10x", "", "1", "example.X", "Xvalue"};
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Rule '" + rule + "': passed when expected to fal");
            } catch (InvalidRuleStructureException e) {}
        }
    }

    /* Testing code generation for Variable node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"value1 and value2", "xValue", "x, y", "value1", "x1 and x2 and x3", "floatValue"};
        String[] expectedVariables = {"value1", "xValue", "x", "value1", "x1", "floatValue"};
        for(int i = 0; i < rules.length; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedVariables[i]);
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
