package test.java.parse.rules;

import main.java.tw.jruletest.parse.rules.GetValueRule;
import org.junit.*;

public class TestGetValueRule {

    // Will fail until reflection is used
    @Test
    public void testGetValueWithIntegerType() {
        String code = new GetValueRule().decodeRule("value of Class.field");
        Assert.assertEquals("int value = Class.field;", code);
    }

    // Will fail until reflection is used
    @Test
    public void testGetValueOfIntegerReturnedValue() {
        String code = new GetValueRule().decodeRule("value of Class.method");
        Assert.assertEquals("int value = Class.field;", code);
    }
}
