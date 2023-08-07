package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestGetValueNode {

    @Test
    public void testGetValueOfValidVariable() {
        String rule = "Get value of xValue";
        try {
            Assert.assertEquals(rule.length(), (new GetValueNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidVariable() {
        String rule = "value of 10x4y";
        try {
            (new GetValueNode()).validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfValidMethod() {
        String rule = "Get Class.method";
        try {
            Assert.assertEquals(rule.length(), (new GetValueNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidMethod() {
        String rule = "get value of class.Method";
        try {
            (new GetValueNode()).validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfMethodWithValidArguments() {
        String rule = "Get Class.method with arguments: `Hello world`, 10 and -0.89f, xValue";
        try {
            Assert.assertEquals(rule.length(), (new GetValueNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfString() {
        String rule = "Get value of `String`";
        try {
            (new GetValueNode()).validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfConstant() {
        String rule = "Get value of -78.45f";
        try {
            (new GetValueNode()).validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfRulePlusExtraRule() {
        String rule = "Get value of Example.x and store in y";
        try {
            Assert.assertEquals(22, (new GetValueNode()).validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidGetValueRuleStructures() {
        String[] rules = {"get", "get value x", "get of x", "value x", "of x"};
        ExpectationNode node = new ExpectationNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }
}
