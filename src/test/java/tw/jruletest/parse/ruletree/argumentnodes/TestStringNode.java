package tw.jruletest.parse.ruletree.argumentnodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;

public class TestStringNode {

    private StringNode node = new StringNode();

    @Test
    public void testValidString() {
        try {
            node.validateRule("`Hello world.`");
        } catch(InvalidRuleStructureException e) {
            Assert.fail("Rule '`Hello world.`': failed");
        }
    }

    @Test
    public void testInvalidString() {
        try {
            node.validateRule("`Hello \" world.`");
            Assert.fail("Rule '`Hello \" world.`': passed");
        } catch(InvalidRuleStructureException e) { }
    }
}
