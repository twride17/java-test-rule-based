package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestVariableNode {

    private VariableNode node = new VariableNode();

    @Test
    public void testValidLocalVariable() {
        try {
            node.validateRule("xValue1");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'xValue1': failed");
        }
    }

    @Test
    public void testValidField() {
        try {
            node.validateRule("Example.x");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'Example.x': failed");
        }
    }

    @Test
    public void testValidLocalVariablePlusExtraRule() {
        try {
            Assert.assertEquals(6, node.validateRule("xValue and other stuff"));
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'xValue and other stuff': failed");
        }
    }

    @Test
    public void testInvalidVariable() {
        try {
            node.validateRule("10x");
            Assert.fail("Rule '10x': passed");
        } catch(InvalidRuleStructureException e) { }
    }
}
