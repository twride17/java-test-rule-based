package test.java.analyzers;

import main.java.tw.jruletest.analyzers.TestClassAnalyzer;
import main.java.tw.jruletest.app.Runner;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestTestClassAnalyzer {

    private List<File> files;

    private final String[][] EXPECTED_RULES = {{"Call method Example.exampleMethod with 12\nGet value of Example.x"},
                                                {"Call Test.method with arguments: 1 Call Test.method with arguments: 2 Get value of Test.x "},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Test.setValue with 10", "Get value of Test.x", "Call Test.setValue with -10",
                                                        "Get value of Test.x"},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Class.method", "Call Test.setValue with 2000", "Get value of Test.x",
                                                        "Call Example.exampleMethod with 100", "Get value of Example.x"}};

    @Before
    public void setup() {
        files = Runner.searchFiles(new File(System.getProperty("user.dir") + "\\src\\test\\java\\examples")
                                                    , new ArrayList<>());
    }

    @Test
    public void testExtractRulesFromSingleField() {
        conductTest(0);
    }

    @Test
    public void testExtractRulesFromSingleArrayField() {
        conductTest(1);
    }

    @Test
    public void testExtractRulesFromSingleFieldWIthMultipleMethods() {
        conductTest(2);
    }

    @Test
    public void testExtractRulesFromMethods() {
        conductTest(3);
    }

    @Test
    public void testExtractRulesFromFieldsWithEqualMethods() {
        conductTest(4);
    }

    @Test
    public void testExtractRulesFromFields() {
        conductTest(5);
    }

    private void conductTest(int testNumber) {
        TestClassAnalyzer.extractRules(new ArrayList<>(Collections.singleton(files.get(testNumber))));
        ArrayList<String> rules = Runner.getRuleSets();
        for(int i = 0; i < rules.size(); i++) {
            Assert.assertEquals(EXPECTED_RULES[testNumber][i], rules.get(i));
        }

        do {
            rules.remove(0);
        } while(!rules.isEmpty());
    }
}
