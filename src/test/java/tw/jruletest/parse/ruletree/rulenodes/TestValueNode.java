package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestValueNode {

    private ValueNode node;

    /* Testing rule validation for Value node */

    private void loadCLass(String className) {
        try {
            Runner.getLoader().loadClass(className);
            JavaClassAnalyzer.sourceFiles.put(className, new SourceClass(className));
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
    }

    @Test
    public void testGetValueOfValidVariable() {
        String rule = "value of xValue";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidVariable() {
        String rule = "value of 10x4y";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfValidMethod() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String rule = "Class.method";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidMethod() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String rule = "result of class.Method";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfMethodWithValidArguments() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String rule = "Class.method with arguments: `Hello world`, 10 and -0.89f, xValue";
        node = new ValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfRulePlusExtraRule() {
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String rule = "value of Example.x and store in y";
        node = new ValueNode();
        try {
            Assert.assertEquals(18, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"value x", "of x", "result of", "result x", "value", "result"};
        node = new ValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Value node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"value of xValue", "Class.method", /*"Get value of Example.x and store in y",*/
                "Class.method with arguments: `Hello world`, 10 and -0.89f, xValue"};

        String[] expectedStrings = {"xValue", "Class.method()", /*"Example.x",*/
                "Class.method(\"Hello world\", 10, -0.89f, xValue)"};

        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        for(int i = 0; i < rules.length; i++) {
            node = new ValueNode();
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
