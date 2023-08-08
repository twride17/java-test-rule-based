package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestStringNode {

    private StringNode node = new StringNode();

    @Test
    public void testValidString() {
        String rule = "`Hello world.`";
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
            Assert.assertEquals(rule, node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
    }

    @Test
    public void testValidStringPlusExtraEndRule() {
        String rule = "`Hello world` and store";
        try {
            Assert.assertEquals(13, node.validateRule(rule));
            Assert.assertEquals("`Hello world`", node.generateCode());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
    }

    @Test
    public void testStringWithSpeechMark() {
        try {
            node.validateRule("`Hello \" world.`");
            Assert.fail("Rule '`Hello \" world.`': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testExptyRule() {
        try {
            node.validateRule("");
            Assert.fail("Rule '': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testStringWithOnlyLeftQuote() {
        try {
            node.validateRule("`Hello world");
            Assert.fail("Rule '`Hello world': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testStringWithOnlyRightQuote() {
        try {
            node.validateRule("Hello world`");
            Assert.fail("Rule 'Hello world`': passed");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testStringWithNoQuotes() {
        try {
            node.validateRule("Hello world");
            Assert.fail("Rule Hello world': passed");
        } catch(InvalidRuleStructureException e) { }
    }
}
