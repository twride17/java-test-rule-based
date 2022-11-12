package test.java.parse.rules;

import main.java.tw.jruletest.parse.rules.GetValueRule;
import org.junit.*;

public class TestGetValueRule {

    @Test
    public void testGetValueWithIntegerType() {
        String code = new GetValueRule().decodeRule("value of Class.field");
        Assert.assertEquals("int value = Class.field;", code);
    }
}
