package tw.jruletest.analyzers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.TestClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRuleExtractor {

    private Map<String, Map<String, String>> expectedRules = new HashMap<>();
    private List<File> files = new ArrayList<>();

    @Before
    public void setup() {
        JavaClassLoader.createLoader();

        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
        //Runner.runCommand("javac -cp src src\\test\\java\\tw\\jruletest\\examples\\TestClass1.java");
        //Runner.runCommand("javac -cp src src\\test\\java\\tw\\jruletest\\examples\\TestClass3.java");


        JavaClassLoader.setLoaderRootPackage("tw");
        TestClassLoader.loadClasses("examples");

        files.add(new File(System.getProperty("user.dir") + "\\src\\test\\java\\tw\\jruletest\\examples\\TestClass1.java"));
        files.add(new File(System.getProperty("user.dir") + "\\src\\test\\java\\tw\\jruletest\\examples\\TestClass3.java"));

        HashMap<String, String> map = new HashMap<>();
        map.put("rule","Call method Example.exampleMethod with 12\nGet value of Example.example");
        expectedRules.put("tw.jruletest.examples.TestClass1", map);
        map = new HashMap<>();
        map.put("currentRuleRule1", "Get value of Class.field");
        map.put("currentRuleRule2", "Get value of Class.method");
        map.put("currentRuleRule3", "Get value of Class.field");
        expectedRules.put("tw.jruletest.examples.TestClass3", map);
    }

    @Test
    public void testExtractionOfRulesFromFiles() {
        RuleExtractor.extractRules(files);
        Map<String, Map<String, String>> rules = Runner.getRuleSets();

        for(String className: rules.keySet()) {
            Map<String, String> ruleSet = rules.get(className);
            for(String ruleName: ruleSet.keySet()) {
                Assert.assertEquals(expectedRules.get(className).get(ruleName), ruleSet.get(ruleName));
            }
        }
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass1.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/TestClass3.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
