package tw.jruletest.parse.parser;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.Parser;
import tw.jruletest.variables.VariableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TestParserRuleValidations {

    private void loadCLass(String className) {
        try {
            Class<?> c = Runner.getLoader().loadClass(className);
            JavaClassAnalyzer.sourceFiles.put(className, new SourceClass(className, c));
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find " + className);
        } catch (LinkageError e) {
            System.out.println("Linkage error detected for: " + className);
        }
    }

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\main\\java");
        Runner.createTestClassLoader();
        Runner.runCommand("javac -cp src " + System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\testprograms\\*.java ");
        Runner.getLoader().setTopPackage("tw");

        String[] variables = {"x", "y", "z", "xValue", "value2", "yValue", "value", "value1", "string"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    public void testRules(String[] rules, String[][] expectedSubRules) {
        for(int i = 0; i < rules.length; i++) {
            try {
                ArrayList<String> subRules = Parser.generateTrees(rules[i]);
                Assert.assertEquals(expectedSubRules[i].length, subRules.size());
                for(int j = 0; j < subRules.size(); j++) {
                    Assert.assertEquals(expectedSubRules[i][j], subRules.get(j));
                }
                System.out.println(rules[i]);
            } catch (UnparsableRuleException e) {
                System.out.println();
                Assert.fail("Failed to parse: " + rules[i]);
            }
        }
    }

    @Test
    public void testStoreValueRules() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[][] subRules = {{"Store value of Class.method with arguments: 67, `Hello world`, true and -0.5f in test"},
                               {"store 10.5 in xValue", "store -10.65f in other", "store value of Class.method in variable"},
                               {"Store true in value1", "store false in value2", "store value of Class.method: value1 and value2 in value3"},
                               {"Store value of Example.x in test", "store 1 in y", "store `Hello world` in z"},
                               {"Store value of x in y", "Store value of y in z"}};

        String[] rules = {"Store value of Class.method with arguments: 67, `Hello world`, true and -0.5f in test",
                          "store 10.5 in xValue, store -10.65f in other then store value of Class.method in variable",
                          "Store true in value1, store false in value2 then store value of Class.method: value1 and value2 in value3",
                          "Store value of Example.x in test then store 1 in y and store `Hello world` in z ",
                          "      Store value of x in y then Store value of y in z    "};

        testRules(rules, subRules);
    }

    @Test
    public void testGetValueRules() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[][] subRules = {{"Get value of Class.method with arguments: 67, `Hello world`, true and -0.5f"},
                               {"get xValue", "get value of Class.method with arguments: -0.98f, 45, false and `New string`", "get yValue"},
                               {"Get result of Example.x", "get Class.method with: `New string`, 56", "get Class.method"},
                               {"Get result of Class.method with arguments: x, y and z", "get value of xValue"},
                               {"Get x", "get y", "get value of z", "get value of Class.method: `Hello, me`, -0.98f, true and 123"}};

        String[] rules = {"Get value of Class.method with arguments: 67, `Hello world`, true and -0.5f",
                          "get xValue, get value of Class.method with arguments: -0.98f, 45, false and `New string` and get yValue",
                          "Get result of Example.x and get Class.method with: `New string`, 56, get Class.method",
                          "  Get result of Class.method with arguments: x, y and z then get value of xValue",
                          "Get x and get y, get value of z then get value of Class.method: `Hello, me`, -0.98f, true and 123   "};

        testRules(rules, subRules);
    }

    @Test
    public void testMethodCallRules() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        loadCLass("tw.jruletest.testexamples.testprograms.Test");
        String[][] subRules = {{"Call method Example.exampleMethod", "call method Example.exampleMethod with: 0, `Hello` and 4"},
                               {"call method Example.exampleMethod with arguments: `Hello world, this is a cool and nice string`, 1f"},
                               {"call Example.exampleMethod: 1, 2 and 3", "call method Example.exampleMethod", "call Class.example with: -0.89f"},
                               {"call Example.exampleMethod", "call Class.method with: 1 and 2", "call method Example.m", "call Test.exampleMethod"},
                               {"Call method Example.exampleMethod with arguments: 1", "call Class.method", "call Example.exampleMethod: xValue"}};

        String[] rules = {"Call method Example.exampleMethod, call method Example.exampleMethod with: 0, `Hello` and 4",
                          "  call method Example.exampleMethod with arguments: `Hello world, this is a cool and nice string`, 1f",
                          "call Example.exampleMethod: 1, 2 and 3 then call method Example.exampleMethod and call Class.example with: -0.89f",
                          "call Example.exampleMethod, call Class.method with: 1 and 2 and call method Example.m then call Test.exampleMethod",
                          "Call method Example.exampleMethod with arguments: 1 then call Class.method and call Example.exampleMethod: xValue"};

        testRules(rules, subRules);
    }

    @Test
    public void testExpectationRules() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[][] subRules = {{"Expect 1 to equal xValue", "Expect value2 to not equal 3", "expect value of Class.method to equal -0.98f"},
                               {"Expect x to equal 1", "expect y to not equal `Hello`", "expect result of Example.exampleMethod to equal false"},
                               {"Expect value of Example.exampleMethod with: 56 and 0.98 to equal 3", "expect xValue to equal `New string`"},
                               {"expect Class.method with arguments: 109, `New and cool string, this is` and -90.2f to not equal -0.9f"},
                               {"Expect 30.5 to not equal xValue", "expect string to equal Class.method", "expect Class.method: 1 to equal 0"}};

        String[] rules = {"Expect 1 to equal xValue, Expect value2 to not equal 3 and expect value of Class.method to equal -0.98f",
                          "  Expect x to equal 1, expect y to not equal `Hello` then expect result of Example.exampleMethod to equal false",
                          "Expect value of Example.exampleMethod with: 56 and 0.98 to equal 3 and expect xValue to equal `New string`     ",
                          "      expect Class.method with arguments: 109, `New and cool string, this is` and -90.2f to not equal -0.9f",
                          "Expect 30.5 to not equal xValue, expect string to equal Class.method and expect Class.method: 1 to equal 0"};

        testRules(rules, subRules);
    }

    @Test
    public void testDifferentRuleCombinations() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[][] subRules = {{"Get value of Class.method: 1 and `Hello`", "store value in xValue", "expect xValue to equal 1"},
                               {"Call method Example.exampleMethod", "store value of Example.x in xValue", "expect xValue to equal `New string`"},
                               {"call Example.exampleMethod: -0.987f", "call Class.method", "store value of Example.exampleMethod with: true in x", "expect x to not equal `String`"},
                               {"Store -100 in x", "store 0.5f in y", "store value of Example.exampleMethod with arguments: false and `New string`, -3.4 in test"},
                               {"store Example.x in x", "store Example.exampleMethod with: `Hello and goodbye`, 1.5 and false in z", "expect x to equal z"}};

        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                          "Call method Example.exampleMethod then store value of Example.x in xValue and expect xValue to equal `New string`",
                          "call Example.exampleMethod: -0.987f, call Class.method then store value of Example.exampleMethod with: true in x and expect x to not equal `String`",
                          "  Store -100 in x, store 0.5f in y, store value of Example.exampleMethod with arguments: false and `New string`, -3.4 in test",
                          "store Example.x in x, store Example.exampleMethod with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};
        testRules(rules, subRules);
    }

    @Test
    public void testInvalidRuleStructureEffects() {
        String[] rules = {"get x get y", "store 100 and expect 0 to equal x", "expect .5 to equal 3", "store x in y, z",
                          "Example.method with: 1", "expect store x in y to equal 3", "call Example.method, expect to equal 1",
                          "Call method x", "Store value and expect value to equal 0", "get result of Class.method and expect 0",
                          "expect x  to equal 0", "call x", "call 0.5f", "store value of `hello` in z", "get value of 100",
                          "tore x", "cal", "except 0 to equal 1", "x", "before calling x", "get x store in y",
                          "expect 0 not to equal 1", "get value of x and store", "store value in y and expect", "get x and get"};
        for(String rule: rules) {
            try {
                Parser.generateTrees(rule);
                Assert.fail("'" + rule + "': passed validation");
            } catch (UnparsableRuleException e) { }
        }
    }

    @After
    public void teardown() {
        VariableStore.reset();
        JavaClassAnalyzer.sourceFiles = new HashMap<>();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/testprograms/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/testprograms/Test.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/testprograms/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
