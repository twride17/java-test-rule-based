package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestConstantNode {

    private ConstantNode node = new ConstantNode();

    @Test
    public void testValidTrueConstant() {
        try {
            node.validateRule("true");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'true': failed");
        }
    }

    @Test
    public void testValidFalseConstant() {
        try {
            node.validateRule("false");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'false': failed");
        }
    }

    @Test
    public void testValidPositiveIntegerConstant() {
        try {
            node.validateRule("109");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '109': failed");
        }
    }

    @Test
    public void testValidNegativeIntegerConstant() {
        try {
            node.validateRule("-1");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-1': failed");
        }
    }

    @Test
    public void testValidPositiveDoubleConstant() {
        try {
            node.validateRule("109.5");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '109.5': failed");
        }
    }

    @Test
    public void testValidNegativeDoubleConstant() {
        try {
            node.validateRule("-56.78");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-56.78': failed");
        }
    }

    @Test
    public void testValidPositiveIntegerFloatConstant() {
        try {
            node.validateRule("19f");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '19f': failed");
        }
    }

    @Test
    public void testValidNegativeIntegerFloatConstant() {
        try {
            node.validateRule("-19f");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-19f': failed");
        }
    }

    @Test
    public void testValidPositiveFloatConstant() {
        try {
            node.validateRule("0.678f");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '19f': failed");
        }
    }

    @Test
    public void testValidNegativeFloatConstant() {
        try {
            node.validateRule("-1.9f");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-19f': failed");
        }
    }

    @Test
    public void testValidConstantPlusExtraAtRuleEnd() {
        try {
            Assert.assertEquals(7, node.validateRule("-8.345f and something else"));
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '-8.345f and something else': failed");
        }
    }

    @Test
    public void testValidBooleanPlusExtraAtRuleEnd() {
        try {
            Assert.assertEquals(5, node.validateRule("false, something else"));
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule 'false, something else': failed");
        }
    }

    @Test
    public void testInvalidConstant() {
        String[] rules = {"f", ".", ".67", "67.", "-67.f", "-.67", "-.67f"};
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Rule '" + rule + "': passed");
            } catch (InvalidRuleStructureException e) { }
        }
    }
}
