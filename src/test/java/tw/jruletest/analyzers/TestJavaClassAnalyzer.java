package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.UnidentifiedCallException;
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
        try {
            Assert.assertEquals(CallType.METHOD, JavaClassAnalyzer.getCallType("Example.exampleMethod"));
        } catch(UnidentifiedCallException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIsMethodWithField() {
        try {
            Assert.assertNotEquals(CallType.METHOD, JavaClassAnalyzer.getCallType("Example.example"));
        } catch(UnidentifiedCallException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIsFieldWithField() {
        try {
            Assert.assertEquals(CallType.FIELD, JavaClassAnalyzer.getCallType("Example.example"));
        } catch(UnidentifiedCallException e) {
            Assert.fail();
        }
    }

    @Test
    public void testIsFieldWithMethod() {
        try {
            Assert.assertNotEquals(CallType.FIELD, JavaClassAnalyzer.getCallType("Example.exampleMethod"));
        } catch(UnidentifiedCallException e) {
            Assert.fail();
        }
    }

    @Test
    public void testUnidentifiedCall() {
        try {
            JavaClassAnalyzer.getCallType("Wrong");
            Assert.fail("Should have thrown exception.");
        } catch(UnidentifiedCallException e) {
            Assert.assertEquals("No such field or method: Wrong", e.getUnidentifiedCall());
        }
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
