package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.app.Runner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestJavaClassAnalyzer {

    @Before
    public void setup() {
        Runner.createTestClassLoader();
    }

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

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "src/test/java/tw/jruletest/testprograms/Example.class"));
        } catch(IOException e) {}
    }
}
