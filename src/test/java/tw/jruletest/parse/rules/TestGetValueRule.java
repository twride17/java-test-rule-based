package tw.jruletest.parse.rules;

import tw.jruletest.parse.rules.GetValueRule;
import org.junit.*;

public class TestGetValueRule {
    @Test
    public void testGetValueWithIntegerType() {
        String code = new GetValueRule().decodeRule("value of Class.field");
        Assert.assertEquals("int value = Class.field;", code);
    }

    @Test
    public void testGetValueOfIntegerReturnedValue() {
        String code = new GetValueRule().decodeRule("value of Class.method");
        Assert.assertEquals("int value = Class.method();", code);
    }
}
