package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.ruletree.innernodes.valuenodes.ValueNode;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestValueNode {

    private ValueNode node;

    /* Testing rule validation for Value node */

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

        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", int.class);
    }

    @Test
    public void testGetValueOfValidVariable() {
        String rule = "value of xValue";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
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
        String rule = "Class.method";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfInvalidMethod() {
        String rule = "result of class.Method";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.fail("Failed: '" + rule + "' passed but should have failed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testGetValueOfMethodWithValidArguments() {
        String rule = "Class.method with arguments: `Hello world`, 10 and -0.89f, xValue";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueOfRulePlusExtraRule() {
        String rule = "value of Example.x and store in y";
        node = new ValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(18, node.getEndIndex());
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
        VariableStore.reset();
        SourceClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/subpackage1/SubExample.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/subpackage2/SubExample.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/ExampleClass.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Test.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples/sourceclasses/programs/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
