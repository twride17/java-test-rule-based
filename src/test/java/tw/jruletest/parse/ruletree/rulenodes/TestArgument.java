package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.*;

public class TestArgument {

    @Test
    public void testValidString() {
        try {
            TreeNode node = Argument.getArgumentNode("`Hello World`");
            Assert.assertTrue(node instanceof StringNode);
            Assert.assertEquals("`Hello World`", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '`Hello World`': failed");
        }
    }

    @Test
    public void testValidStringPlusExtra() {
        try {
            TreeNode node = Argument.getArgumentNode("`Hello world's` and stuff");
            Assert.assertTrue(node instanceof StringNode);
            Assert.assertEquals("`Hello world's`", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '`Hello world's` and stuff': failed");
        }
    }

    @Test
    public void testInvalidString() {
        try {
            Argument.getArgumentNode("`Hello World");
            Assert.fail("Rule '10x': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidConstant() {
        try {
            TreeNode node = Argument.getArgumentNode("-0.564f");
            Assert.assertTrue(node instanceof ConstantNode);
            Assert.assertEquals("-0.564f", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-0.564f': failed");
        }
    }

    @Test
    public void testValidConstantPlusExtra() {
        try {
            TreeNode node = Argument.getArgumentNode("109 and stuff");
            Assert.assertTrue(node instanceof ConstantNode);
            Assert.assertEquals("109", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '109 and stuff': failed");
        }
    }

    @Test
    public void testInvalidConstant() {
        try {
            Argument.getArgumentNode("-.654f");
            Assert.fail("Rule '-.654f': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidVariable() {
        try {
            TreeNode node = Argument.getArgumentNode("xValue");
            Assert.assertTrue(node instanceof VariableNode);
            Assert.assertEquals("xValue", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'xValue': failed");
        }
    }

    @Test
    public void testValidVariablePlusExtra() {
        try {
            TreeNode node = Argument.getArgumentNode("xValue and stuff");
            Assert.assertTrue(node instanceof VariableNode);
            Assert.assertEquals("xValue", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'xValue and stuff': failed");
        }
    }

    @Test
    public void testInvalidVariable() {
        try {
            Argument.getArgumentNode("10x78v");
            Assert.fail("Rule '10x78v': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testEmptyArgument() {
        try {
            Argument.getArgumentNode("");
            Assert.fail("Rule '': passed");
        } catch(InvalidRuleStructureException e) { }
    }
}
