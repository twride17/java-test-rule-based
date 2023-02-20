package tw.jruletest.parse.rules;

import org.junit.*;

public class TestMethodCallRule {

    @Test
    public void testMethodCallWithNoArguments() {
        String code = new MethodCallRule().decodeRule("Example.method");
        Assert.assertEquals("Example.method()", code);
    }

    @Test
    public void testMethodCallWithOneArgument() {
        String code = new MethodCallRule().decodeRule("Example.method with 1");
        Assert.assertEquals("Example.method(1)", code);
    }

    @Test
    public void testMethodCallWithTwoArguments() {
        String code = new MethodCallRule().decodeRule("Example.method with arguments 2 and 3");
        Assert.assertEquals("Example.method(2, 3)", code);
    }

    @Test
    public void testMethodWithThreeArguments() {
        String code = new MethodCallRule().decodeRule("Example.method with arguments: 2, 3 and 4");
        Assert.assertEquals("Example.method(2, 3, 4)", code);
    }

    @Test
    public void testMethodWithFourArguments() {
        String code = new MethodCallRule().decodeRule("Example.method with: 2, 3 and 4 and 5");
        Assert.assertEquals("Example.method(2, 3, 4, 5)", code);
    }
}
