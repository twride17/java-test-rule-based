package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestJavaClassAnalyzer {

    @Before
    public void setup() {
        //Runner.runCommand("javac -cp src src/test/java/tw/jruletest/testprograms/Example.java");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
        Runner.createTestClassLoader();
    }

    @Test
    public void testIsMethodWithMethod() {
        Assert.assertTrue(JavaClassAnalyzer.isMethodCall("Example.exampleMethod"));
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
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Example.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
