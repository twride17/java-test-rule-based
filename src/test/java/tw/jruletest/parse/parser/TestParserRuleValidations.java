package tw.jruletest.parse.parser;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.parsing.UnparsableRuleException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.Parser;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestParserRuleValidations {

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        try {
            ArrayList<String> classes = JavaClassLoader.loadClasses("sourceclasses");
            for(String name: classes) {
                SourceClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {}

        String[] variables = {"x", "y", "z", "xValue", "value2", "yValue", "value", "value1", "string"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    private void testRules(String[] rules, String[][] expectedSubRules) {
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
        String[][] subRules = {{"Store value of Class.method2 with arguments: `Hello world`, true and -0.5f in test"},
                               {"store 10.5 in xValue", "store -10.65f in other", "store value of Class.method in variable"},
                               {"Store true in value1", "store false in value2", "store value of Class.isTrue: value1 is greater than value2 in value3"},
                               {"Store value of Example.x in test", "store 1 in y", "store `Hello world` in z"},
                               {"Store value of x in y", "Store value of y in z"}};

        String[] rules = {"Store value of Class.method2 with arguments: `Hello world`, true and -0.5f in test",
                          "store 10.5 in xValue, store -10.65f in other then store value of Class.method in variable",
                          "Store true in value1, store false in value2 then store value of Class.isTrue: value1 is greater than value2 in value3",
                          "Store value of Example.x in test then store 1 in y and store `Hello world` in z ",
                          "      Store value of x in y then Store value of y in z    "};

        testRules(rules, subRules);
    }

    @Test
    public void testGetValueRules() {
        String[][] subRules = {{"Get value of Class.method2 with arguments: `Hello world`, true and -0.5f"},
                               {"get xValue", "get value of Example.m2 with arguments: `New string`, 45, -0.98f", "get yValue"},
                               {"Get result of Example.x", "get Example.concat with: `New string`, `Other String` + 56", "get Class.method"},
                               {"Get result of Class.isTrue with arguments: x is equal to y and z is less than 1.5f", "get value of xValue"},
                               {"Get x", "get y", "get value of z", "get value of Class.method2: `Hello, me`, not x is equal to 1 and -0.98f"}};

        String[] rules = {"Get value of Class.method2 with arguments: `Hello world`, true and -0.5f",
                          "get xValue, get value of Example.m2 with arguments: `New string`, 45, -0.98f and get yValue",
                          "Get result of Example.x and get Example.concat with: `New string`, `Other String` + 56, get Class.method",
                          "  Get result of Class.isTrue with arguments: x is equal to y and z is less than 1.5f then get value of xValue",
                          "Get x and get y, get value of z then get value of Class.method2: `Hello, me`, not x is equal to 1 and -0.98f   "};

        testRules(rules, subRules);
    }

    @Test
    public void testMethodCallRules() {
        String[][] subRules = {{"Call method Class.example", "call method Example.exampleMethod with: 0"},
                               {"call method Class.method2 with arguments: `Hello world, this is a cool and nice string`, true and 1f"},
                               {"call Test.example3: true, 2f and false", "call method Class.method", "call Example.exampleMethod with: -9--34"},
                               {"call Class.example", "call Test.setValue with: 1 + 2", "call method Example.m", "call Class.example"},
                               {"Call method Class.method1 with arguments: 1", "call Class.method", "call Test.exampleMethod: xValue"}};

        String[] rules = {"Call method Class.example, call method Example.exampleMethod with: 0",
                          "  call method Class.method2 with arguments: `Hello world, this is a cool and nice string`, true and 1f",
                          "call Test.example3: true, 2f and false then call method Class.method and call Example.exampleMethod with: -9--34",
                          "call Class.example, call Test.setValue with: 1 + 2 and call method Example.m then call Class.example",
                          "Call method Class.method1 with arguments: 1 then call Class.method and call Test.exampleMethod: xValue"};

        testRules(rules, subRules);
    }

    @Test
    public void testExpectationRules() {
        String[][] subRules = {{"Expect 1 to equal xValue", "Expect value2 to not equal 3", "expect value of Class.method to equal -0.98f"},
                               {"Expect x to equal 1", "expect y to not equal `Hello`", "expect result of Example.exampleMethod with: 10 - 7 to equal false"},
                               {"Expect value of Example.exampleMethod with: 56 * 98 to equal 3", "expect xValue to equal `New string`"},
                               {"expect Class.method2 with arguments: `New and cool string, this is` and xValue is equal to 10 and -90.2f to not equal -0.9f"},
                               {"Expect 30.5 to not equal xValue", "expect string to equal Example.m", "expect Class.method1: 1 to equal 0"}};

        String[] rules = {"Expect 1 to equal xValue, Expect value2 to not equal 3 and expect value of Class.method to equal -0.98f",
                          "  Expect x to equal 1, expect y to not equal `Hello` then expect result of Example.exampleMethod with: 10 - 7 to equal false",
                          "Expect value of Example.exampleMethod with: 56 * 98 to equal 3 and expect xValue to equal `New string`     ",
                          "      expect Class.method2 with arguments: `New and cool string, this is` and xValue is equal to 10 and -90.2f to not equal -0.9f",
                          "Expect 30.5 to not equal xValue, expect string to equal Example.m and expect Class.method1: 1 to equal 0"};

        testRules(rules, subRules);
    }

    @Test
    public void testDifferentRuleCombinations() {
        String[][] subRules = {{"Get value of Class.method3: 1 + `Hello`", "store 100 + 10 * 3 in xValue", "expect xValue to equal 1"},
                               {"Call method Class.example", "store value of Example.x in xValue", "expect xValue to equal `New string`"},
                               {"call Example.m3: -0.987f", "call Class.method", "store value of Class.isTrue with: true in x", "expect x to not equal `String`"},
                               {"Store -100 in x", "store 0.5f in y", "store value of Example.m2 with arguments: `New string`, 1 and -3.4f in test"},
                               {"store Example.x in x", "store Example.concat with: `Hello and goodbye` and `and good luck` in z", "expect x to equal z"}};

        String[] rules = {"Get value of Class.method3: 1 + `Hello`, store 100 + 10 * 3 in xValue then expect xValue to equal 1",
                          "Call method Class.example then store value of Example.x in xValue and expect xValue to equal `New string`",
                          "call Example.m3: -0.987f, call Class.method then store value of Class.isTrue with: true in x and expect x to not equal `String`",
                          "  Store -100 in x, store 0.5f in y, store value of Example.m2 with arguments: `New string`, 1 and -3.4f in test",
                          "store Example.x in x, store Example.concat with: `Hello and goodbye` and `and good luck` in z then expect x to equal z  "};
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
                System.out.println("get x and get");
                Parser.generateTrees("get x and get");

                Assert.fail("'" + rule + "': passed validation");
            } catch (UnparsableRuleException e) { }
        }
    }

    @After
    public void teardown() {
        VariableStore.reset();
        SourceClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Test.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Class.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/ExampleClass.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
