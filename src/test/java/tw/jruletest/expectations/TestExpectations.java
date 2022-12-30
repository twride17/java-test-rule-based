package tw.jruletest.expectations;

import org.junit.*;

import tw.jruletest.testprograms.Example;
import tw.jruletest.files.FileFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestExpectations {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    @Test
    public void testIntegerExpectationSatisfied() {
        try {
            Example.exampleMethod(20);
            Expectations.expect(Example.example).toEqual(20);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail();
        }
    }

    @Test
    public void testIntegerExpectationNotSatisfied() {
        try {
            Example.exampleMethod(20);
            Expectations.expect(Example.example).toEqual(21);
            Assert.fail();
        } catch(UnsatisfiedExpectationError e) {}
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "\\src\\test\\java\\testprograms\\Example.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file");
        }
    }
}
