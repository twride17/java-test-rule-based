package tw.jruletest.parse.rules;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;
import tw.jruletest.parse.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestExpectationRule {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
        Runner.createTestClassLoader();
    }

    @Test
    public void testIntegerVariableValueNotEquals() {
        String rule = "Expect value to not equal 0";
        Assert.assertEquals("Expectations.expect(value).toNotEqual(0);\n", Parser.parseRule(rule));
    }

    @Test
    public void testIntegerVariableValueEquals() {
        String rule = "Expect value to equal 0";
        Assert.assertEquals("Expectations.expect(value).toEqual(0);\n", Parser.parseRule(rule));
    }

    @Test
    public void testIntegerMethodCallNotEquals() {
        String rule = "Expect value of Class.method to not equal 0";
        Assert.assertEquals("Expectations.expect(Class.method()).toNotEqual(0);\n", Parser.parseRule(rule));
    }

    @Test
    public void testIntegerMethodCallEquals() {
        String rule = "Expect value of Class.method to equal 0";
        Assert.assertEquals("Expectations.expect(Class.method()).toEqual(0);\n", Parser.parseRule(rule));
    }

    @Test
    public void testValueNotEqualsIntegerMethodCall() {
        String rule = "Expect 0 to not equal value of Class.method";
        Assert.assertEquals("Expectations.expect(0).toNotEqual(Class.method());\n", Parser.parseRule(rule));
    }

    @Test
    public void testValueEqualsIntegerMethodCall() {
        String rule = "Expect 0 to equal value of Class.method";
        Assert.assertEquals("Expectations.expect(0).toEqual(Class.method());\n", Parser.parseRule(rule));
    }

    @Test
    public void testStringVariableValueNotEquals() {
        String rule = "Expect value to not equal 'Not hello'";
        Assert.assertEquals("Expectations.expect(value).toNotEqual('Hello');\n", Parser.parseRule(rule));
    }

    @Test
    public void testStringVariableValueEquals() {
        String rule = "Expect value to equal 'String'";
        Assert.assertEquals("Expectations.expect(value).toEqual('String');\n", Parser.parseRule(rule));
    }

    @Test
    public void testStringMethodCallNotEquals() {
        String rule = "Expect value of Class.string to not equal 'Other String'";
        Assert.assertEquals("Expectations.expect(Class.string()).toNotEqual('Other String');\n", Parser.parseRule(rule));
    }

    @Test
    public void testStringMethodCallEquals() {
        String rule = "Expect value of Class.string to equal 'String'";
        Assert.assertEquals("Expectations.expect(Class.string()).toEqual('String');\n", Parser.parseRule(rule));
    }

    @Test
    public void testValueNotEqualsStringMethodCall() {
        String rule = "Expect 'Hi String' to not equal value of Class.string";
        Assert.assertEquals("Expectations.expect('Hi String').toNotEqual(Class.string());\n", Parser.parseRule(rule));
    }

    @Test
    public void testValueEqualsStringMethodCall() {
        String rule = "Expect 'String' to equal value of Class.string";
        Assert.assertEquals("Expectations.expect('String').toEqual(Class.string());\n", Parser.parseRule(rule));
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file");
        }
    }
}
