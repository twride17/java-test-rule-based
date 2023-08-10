package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestVariableNode {

    private VariableNode node = new VariableNode();

    /* Testing rule validation for Variable node */
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
    public void testValidField() {
        String rule = "Example.x";
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
            Assert.assertEquals(rule, node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
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
    public void testValidFieldPlusExtraRule() {
        String rule = "Example.x, other stuff";
        try {
            Assert.assertEquals(9, node.validateRule(rule));
            Assert.assertEquals("Example.x", node.generateCode());
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
        String[] rules = {"value1 and value2", "xValue", "x, y", "value1", "x1 and x2 and x3", "floatValue", "Example.x"};
        String[] expectedConstants = {"value1", "xValue", "x", "value1", "x1", "floatValue", "Example.x"};
        for(int i = 0; i < rules.length; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedConstants[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }
}
