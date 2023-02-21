package tw.jruletest.parse;

import tw.jruletest.files.FileFinder;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParserOnExamples {

    private final String[][] RULES = {{"Call method Test.setValue with arguments: 6", "Get value of Test.x",
                                        "Call method Test.setValue with: 56","Get value of Test.x",
                                        "Expect xValue2 to not equal xValue"}};
    private final String[] EXPECTED_CODE = {"Test.setValue(6);\nint xValue = Test.x;\n" +
                                            "Test.setValue(56);\nint xValue2 = Test.x;\n" +
                                            "Expectations.expect(xValue2).toNotEqual(xValue);\n"};

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    // Test with the first example program
    @Test
    public void testParserExample1() {
        String rules = "Call method Example.exampleMethod with arguments: 25\nStore value of Example.example in x\n" +
                        "Call Test.setValue with: 56\nGet Test.x\nExpect xValue to not equal x\nCall Test.setValue with: -100\n" +
                        "Expect value of Test.x to equal -100";
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
