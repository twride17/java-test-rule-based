package tw.jruletest.translation;

import org.junit.*;

public class TestRuleManipulator {

    @Test
    public void testRemovingValueOfRuleDetails() {
        Assert.assertEquals("Get a call", RuleManipulator.removeValueOfDetail("Get value of a call"));
    }

    @Test
    public void testGetVariableName() {
        Assert.assertEquals("call", RuleManipulator.getVariableName("This is a method call"));
    }
}
