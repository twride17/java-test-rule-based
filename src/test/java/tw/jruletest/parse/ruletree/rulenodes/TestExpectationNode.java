package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestExpectationNode {

    @Test
    public void testIntegerValueAsActual() {
        String rule = "expect value to equal 1";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testDoubleValueAsActual() {
        String rule = "value to equal -11.567";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsActual() {
        String rule = "expect value to equal -11.5f";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsActual() {
        String rule = "value to equal true";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testStringValueAsActual() {
        String rule = "expect value to not equal `Hello World`";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testIntegerValueAsExpected() {
        String rule = "expect 1 to equal x";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testDoubleValueAsExpected() {
        String rule = "expect -11.567 to not equal xValue";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsExpected() {
        String rule = "expect -11.5f to equal value";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsExpected() {
        String rule = "true to not equal x";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testStringValueAsExpected() {
        String rule = "expect `Hello World` to not equal xValue";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testMethodReturnValueAsExpected() {
        String rule = "expect Class.method: 5, -0.9f and `String` to not equal -98f";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testMethodReturnValueAsActual() {
        String rule = "Expect 8765.5678 to equal value of Class.method";
        try {
            Assert.assertEquals(rule.length(), (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testValidExpectationPlusExtraEndRule() {
        String rule = "Expect Example.x to equal value of Class.method and expect...";
        try {
            Assert.assertEquals(47, (new ExpectationNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidRules() {
        String[] rules = {"expect value", "expect value equals 0", "expect value to 4", "expect class.method: to equal 6",
                            "value of Class.method with arguments: `Hello World` to equal .5f", "expect Class.method: 5, and to equal 5"};
        ExpectationNode node = new ExpectationNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }
}
