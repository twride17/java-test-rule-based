package tw.jruletest.analyzers;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import org.junit.*;

public class TestJavaClassAnalyzer {

    @Test
    public void testIsMethodWithMethod() {
        Assert.assertTrue(JavaClassAnalyzer.isMethodCall("Example.methodName"));
    }

    @Test
    public void testIsMethodWithField() {
        Assert.assertFalse(JavaClassAnalyzer.isMethodCall("Example.example"));
    }

    @Test
    public void testIsFieldWithField() {
        Assert.assertTrue(JavaClassAnalyzer.isField("Example.example"));
    }

    @Test
    public void testIsFieldWithMethod() {
        Assert.assertFalse(JavaClassAnalyzer.isField("Example.methodName"));
    }
}
