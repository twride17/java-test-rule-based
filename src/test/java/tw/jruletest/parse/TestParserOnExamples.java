package tw.jruletest.parse;

import tw.jruletest.files.FileFinder;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParserOnExamples {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    @Test
    public void testParserExample1() {
        String rules = "Call method Example.exampleMethod with arguments: 25\nStore value of Example.example in x\n" +
                        "Call Test.setValue with: 56\nGet Test.x\nExpect xValue to not equal x\nCall Test.setValue with: -100\n" +
                        "Expect value of Test.x to equal -100";
        String expectedCode = "Example.exampleMethod(25);\nint x = Example.example;\nTest.setValue(56);\nint xValue = Test.x;\n" +
                                "Expectations.expect(xValue).toNotEqual(x);\nTest.setValue(-100);\nExpectations.expect(Test.x).toEqual(-100);\n";
        Assert.assertEquals(expectedCode, Parser.parseRules(rules.split("\n")));
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Test.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file");
        }
    }
}
