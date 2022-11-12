package test.java.parse;

import main.java.tw.jruletest.parse.Parser;
import org.junit.*;

public class TestParser {

    @Test
    public void testParseMethodCallCommand() {
        String code = Parser.parseRule("Call Example.methodName with 1234");
        Assert.assertEquals("Example.methodName(1234);\n", code);
    }

    @Test
    public void testParseGetValueCommand() {
        String code = Parser.parseRule("Get value of Example.example");
        Assert.assertEquals("int value = Example.example;\n", code);
    }

    @Test
    public void testParseMethodAndValueCommands() {
        String code = Parser.parseRule("Call Example.methodName with 1234 Get value of Example.example");
        Assert.assertEquals("Example.methodName(1234);\nint value = Example.example;\n", code);
    }
}
