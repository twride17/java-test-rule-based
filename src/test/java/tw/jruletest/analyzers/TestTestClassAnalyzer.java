package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import org.junit.*;
import tw.jruletest.files.FileFinder;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestTestClassAnalyzer {

    private List<File> files = new ArrayList<>();

    private final String[][] EXPECTED_RULES = {{"Call method Example.exampleMethod with 12\nGet value of Example.example"},
                                                {"Call Test.setValue with arguments: 1\nCall Test.setValue with arguments: 2\nGet value of Test.x\n"},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Test.setValue with 10", "Get value of Test.x", "Call Test.setValue with -10",
                                                        "Get value of Test.x"},
                                                {"Get value of Class.field", "Get value of Class.method", "Get value of Class.field"},
                                                {"Call Class.method", "Call Test.setValue with 2000", "Get value of Test.x",
                                                        "Call Example.exampleMethod with 100", "Get value of Example.example"}};

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        files = FileFinder.getFiles("\\test\\java\\tw\\jruletest\\examples\\");
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        JavaClassLoader.changeLoaderDirectory("\\test\\java\\");
        JavaClassLoader.loadClasses("test\\java\\tw\\jruletest\\examples\\");
        JavaClassLoader.changeLoaderDirectory("\\main\\java\\");
        JavaClassLoader.loadClasses("programs");
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
        String className = "tw.jruletest.examples.TestClass" + (testNumber+1);

        RuleExtractor.extractRules(new ArrayList<>(Collections.singleton(files.get(testNumber))));
        Map<String, Map<String, String>> rules = Runner.getRuleSets();

        Map<String, String> ruleSet = rules.get(className);
        Assert.assertEquals(EXPECTED_RULES[testNumber].length, ruleSet.size());

        for (String methodKey: ruleSet.keySet()) {
            try {
                int methodNum = Integer.parseInt(methodKey.substring(methodKey.length()-1));
                Assert.assertEquals(ruleSet.get(methodKey), EXPECTED_RULES[testNumber][methodNum-1]);
            } catch(NumberFormatException e) {
                Assert.assertEquals(EXPECTED_RULES[testNumber][0], ruleSet.get(methodKey));
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
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Class.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Test.class"));
        } catch (IOException e) {
            System.out.println("Class file does not exist.");
        }
    }
}
