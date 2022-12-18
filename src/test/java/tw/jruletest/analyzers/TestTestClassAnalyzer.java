package tw.jruletest.analyzers;

import tw.jruletest.analyzers.RuleExtractor;
import tw.jruletest.app.Runner;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestTestClassAnalyzer {

    private List<File> files;

    private final String[][] EXPECTED_RULES = {{"Call method Example.exampleMethod with 12\nGet value of Example.example"},
                                                {"Call Test.method with arguments: 1\nCall Test.method with arguments: 2\nGet value of Test.x\n"},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Test.setValue with 10", "Get value of Test.x", "Call Test.setValue with -10",
                                                        "Get value of Test.x"},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Class.method", "Call Test.setValue with 2000", "Get value of Test.x",
                                                        "Call Example.exampleMethod with 100", "Get value of Example.example"}};

    @Before
    public void setup() {
        files = Runner.searchFiles(new File(System.getProperty("user.dir") + "\\src\\test\\java\\tw\\jruletest\\examples")
                                                    , new ArrayList<>());
        Runner.runCommand("javac -cp src src\\test\\java\\tw\\jruletest\\examples\\*.java");
        Runner.createTestClassLoader();
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
        RuleExtractor.extractRules(new ArrayList<>(Collections.singleton(files.get(testNumber))));
        Map<String, Map<String, String>> rules = Runner.getRuleSets();

        String className = "tw.jruletest.examples.TestClass" + (testNumber+1);
        Map<String, String> ruleSet = rules.get(className);
        Assert.assertFalse(ruleSet.isEmpty());
        if(ruleSet.size() == 1) {
            Assert.assertEquals(EXPECTED_RULES[testNumber][0], ruleSet.get(ruleSet.keySet().toArray(new String[0])[0]));
        } else {
            for (String methodKey : ruleSet.keySet()) {
                int methodNum = Integer.parseInt(methodKey.substring(methodKey.length()-1));
                Assert.assertEquals(ruleSet.get(methodKey), EXPECTED_RULES[testNumber][methodNum-1]);
            }
        }

        String[] keys = rules.keySet().toArray(new String[0]);
        for(String key: keys) {
            rules.remove(key);
        }
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass1.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass3.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass4.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass5.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass6.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Class.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Test.class"));
        } catch (IOException e) {
            System.out.println("Class file does not exist.");
        }
    }
}
