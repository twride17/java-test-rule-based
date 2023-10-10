package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.innernodes.argumentnodes.StringNode;

public class TestStringNode {

    private StringNode node = new StringNode();

    /* Testing rule validation for String node */

    @Test
    public void testValidString() {
        String rule = "`Hello world.`";
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
            Assert.assertEquals(rule, node.getArgumentString());
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '" + rule + "': failed");
        }
    }

    @Test
    public void testValidStringPlusExtraEndRule() {
        String rule = "`Hello world` and store";
        try {
            node.validateRule(rule);
            Assert.assertEquals(13, node.getEndIndex());
            Assert.assertEquals("`Hello world`", node.getArgumentString());
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
    public void testEmptyRule() {
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

    /* Testing code generation for String node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"`Hello world`", "`New String`", "`It's a cool string`", "`Hello` and store"};
        String[] expectedStrings = {"\"Hello world\"", "\"New String\"", "\"It's a cool string\"", "\"Hello\""};
        for(int i = 0; i < rules.length; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }
}
