package tw.jruletest.parse;

import tw.jruletest.parse.Parser;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParserOnExamples {

    // Define the rules for the program test as:
    // "Call method Test.setValue with arguments: 6"
    // "Get value of Test.x"
    private final String[][] RULES = {{"Call method Test.setValue with arguments: 6", "Get value of Test.x"}};
    private final String[] EXPECTED_CODE = {"Test.setValue(6);\nint value = Test.x;\n"};

    // Test with the first example program
    @Test
    public void testParserExample1() {
        for(int i = 0; i < RULES.length; i++) {
            String code = "";
            String[] ruleSet = RULES[i];
            for(String rule: ruleSet) {
                code += Parser.parseRule(rule);
            }
            Assert.assertEquals(EXPECTED_CODE[i], code);
        }
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
