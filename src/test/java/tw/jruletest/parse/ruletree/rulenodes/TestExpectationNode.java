package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.SourceClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

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
                JavaClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {}

        String[] variables = {"x", "value", "xValue"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testIntegerValueAsActual() {
        String rule = "expect value to equal 1";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testDoubleValueAsActual() {
        String rule = "value to equal -11.567";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsActual() {
        String rule = "expect value to equal -11.5f";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsActual() {
        String rule = "value to equal true";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testStringValueAsActual() {
        String rule = "expect value to not equal `Hello World`";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
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
            Assert.assertEquals(rule.length(), node.validateRule(rule));
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
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFloatValueAsExpected() {
        String rule = "expect -11.5f to equal value";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testBooleanValueAsExpected() {
        String rule = "true to not equal x";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
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
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testMethodReturnValueAsExpected() {
        String rule = "expect Class.method: 5, -0.9f and `String` to not equal -98f";
        node = new ExpectationNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
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
            Assert.assertEquals(rule.length(), node.validateRule(rule));
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
            Assert.assertEquals(47, node.validateRule(rule));
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
        String[] rules = {"expect Example.x to equal 1", "value to equal -11.567", "expect value to equal -11.5f", "value to equal true",
                            "expect value to not equal `Hello World`", "expect 1 to equal x", "expect -11.567 to not equal xValue",
                            "expect -11.5f to equal value", "true to not equal x", "expect `Hello World` to not equal xValue",
                            "expect Class.method: 5, -0.9f and `String` to not equal -98f", "Expect 8765.5678 to equal value of Class.method",
                            "Expect -4501.2345f to equal value of Class.method: 12.5f and `New string`, false and 123 and expect x to equal 2"};

        String[] expectedStrings = {"Expectations.expect(Example.x).toEqual(1);", "Expectations.expect(value).toEqual(-11.567);",
                                    "Expectations.expect(value).toEqual(-11.5f);", "Expectations.expect(value).toEqual(true);",
                                    "Expectations.expect(value).toNotEqual(\"Hello World\");", "Expectations.expect(1).toEqual(x);",
                                    "Expectations.expect(-11.567).toNotEqual(xValue);", "Expectations.expect(-11.5f).toEqual(value);",
                                    "Expectations.expect(true).toNotEqual(x);", "Expectations.expect(\"Hello World\").toNotEqual(xValue);",
                                    "Expectations.expect(Class.method(5, -0.9f, \"String\")).toNotEqual(-98f);",
                                    "Expectations.expect(8765.5678).toEqual(Class.method());",
                                    "Expectations.expect(-4501.2345f).toEqual(Class.method(12.5f, \"New string\", false, 123));"};

        for(int i = 0; i < rules.length; i++) {
            node = new ExpectationNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }

    @After
    public void teardown() {
        VariableStore.reset();
        JavaClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Test.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
