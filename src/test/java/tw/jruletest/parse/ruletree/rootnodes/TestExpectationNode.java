package tw.jruletest.parse.ruletree.rootnodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.ruletree.rootnodes.ExpectationNode;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestExpectationNode {

    private ExpectationNode node;

    /* Testing rule validation for Expectation node */

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        try {
            ArrayList<String> classes = JavaClassLoader.loadClasses("programs");
            for(String name: classes) {
                SourceClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {}

        String[] variables = {"x", "xValue"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testIntegerValueAsActual() {
        String rule = "expect x to equal 1";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testDoubleValueAsActual() {
        String rule = "xValue to equal -11.567";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsActual() {
        String rule = "expect xValue to equal -11.5f";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsActual() {
        String rule = "x to equal true";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testStringValueAsActual() {
        String rule = "expect x to not equal `Hello World`";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testIntegerValueAsExpected() {
        String rule = "expect 1 to equal x";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testDoubleValueAsExpected() {
        String rule = "expect -11.567 to not equal xValue";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsExpected() {
        String rule = "expect -11.5f to equal xValue";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsExpected() {
        String rule = "true to not equal xValue";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testStringValueAsExpected() {
        String rule = "expect `Hello World` to not equal xValue";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testMethodReturnValueAsExpected() {
        String rule = "expect Class.method2: `String`, Class.method is less than or equal to 12 and -0.9f to not equal -98f";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testMethodReturnValueAsActual() {
        String rule = "Expect 8765.5678 to equal value of Class.method";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testValidExpectationPlusExtraEndRule() {
        String rule = "Expect Example.x to equal value of Class.method and expect...";
        node = new ExpectationNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(47, node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidRules() {
        String[] rules = {"expect value", "expect value equals 0", "expect value to 4", "expect class.method: to equal 6",
                            "value of Class.method with arguments: `Hello World` to equal .5f", "expect Class.method: 5, and to equal 5"};
        node = new ExpectationNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Expectation node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"expect Example.x to equal 1", "xValue to equal -11.567", "expect xValue to equal -11.5f", "xValue to equal true",
                            "expect xValue to not equal `Hello World`", "expect 1 to equal x", "expect -11.567 to not equal xValue",
                            "expect -11.5f to equal xValue", "true to not equal x", "expect `Hello World` to not equal xValue",
                            "expect Example.m2: `String`, 5+ 6 and -89.9f - 89 to not equal -98f", "Expect 8765.5678 to equal value of Class.method",
                            "Expect -4501.2345f to equal value of Class.example + value of Class.method2: `Hello`, x is equal to xValue or x is greater than -1 and 50f"};

        String[] expectedStrings = {"Expectations.expect(Example.x).toEqual(1);", "Expectations.expect(xValue).toEqual(-11.567);",
                                    "Expectations.expect(xValue).toEqual(-11.5f);", "Expectations.expect(xValue).toEqual(true);",
                                    "Expectations.expect(xValue).toNotEqual(\"Hello World\");", "Expectations.expect(1).toEqual(x);",
                                    "Expectations.expect(-11.567).toNotEqual(xValue);", "Expectations.expect(-11.5f).toEqual(xValue);",
                                    "Expectations.expect(true).toNotEqual(x);", "Expectations.expect(\"Hello World\").toNotEqual(xValue);",
                                    "Expectations.expect(Example.m2(\"String\", (5 + 6), (-89.9f - 89))).toNotEqual(-98f);",
                                    "Expectations.expect(8765.5678).toEqual(Class.method());",
                                    "Expectations.expect(-4501.2345f).toEqual((Class.example() + Class.method2(\"Hello\", ((x == xValue) || (x > -1)), 50f)));"};

        for(int i = 0; i < rules.length; i++) {
            node = new ExpectationNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(expectedStrings[i], node.generateCode());
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
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
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
