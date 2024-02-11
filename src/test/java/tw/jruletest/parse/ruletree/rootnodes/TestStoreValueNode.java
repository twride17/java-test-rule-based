package tw.jruletest.parse.ruletree.rootnodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.ruletree.rootnodes.StoreValueNode;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestStoreValueNode {

    private StoreValueNode node;

    /* Testing rule validation for Get Value node */

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

        VariableStore.addVariable(Runner.getCurrentMethod(), "z", char.class);
        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", boolean.class);
        VariableStore.addVariable(Runner.getCurrentMethod(), "x", int.class);
    }

    @Test
    public void testStoreMethodResultInVariable() {
        String[] rules = {"Store value of Class.method in xValue", "store value of Class.method: 1 in xValue1",
                            "result of Class.method with: 1 and `Hello` in value", "store result of Class.method with arguments: 0 in test"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testStoreConstantInVariable() {
        String[] rules = {"store 109.5f in test", "0.5f in test", "Store `Hello` in test", "true in test"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testStoreVariableValueInVariable() {
        String[] rules = {"store value of xValue in test", "store xValue in testValue", "store x in y", "x in xValue"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testStoreValueInConstantResult() {
        String[] rules = {"store x in 1", "`hello` in 123f", "store 1 in true", "Store true in false", "test in 100"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    @Test
    public void testStoreValueInMethodResult() {
        String[] rules = {"store x in Class.method", "`hello` in Class.method with: x", "store Class.method in Class.method2"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    @Test
    public void testStoreValueRulesPlusExtraEnding() {
        String[] rules = {"Store 1 in x and expect", "store Class.method in test, expect", "Class.method: 1 in dummy and expect",
                "store `New string` in xValue and store", "Class.method with arguments: 1, 2 and 3 in test and expect"};
        int[] indices = {12, 26, 24, 28, 47};
        node = new StoreValueNode();
        for(int i = 0; i < 5; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(indices[i], node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                e.printStackTrace();
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"store", " store", "store x", "store in x", "in test", "test", "store value of Class.method:",
                            "store `hello in y", "store .67f in y", "store hello' in z", "store `hello` in 1value"};
        node = new StoreValueNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Store Value node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"Store value of Class.method in xValue", "result of Class.method with: 1 and `Hello` in value",
                            "store result of Class.method with arguments: 0 in test", "store 109.5f in test1", "Store `Hello` in test2",
                            "true in test", "store value of xValue in test3", "store z in y", "store Class.method in test, expect",
                            "Class.method with arguments: 1, 2 and 3 in test and expect", "Store 1 in x and expect"};

        String[] expectedStrings = {"xValue = Class.method();", "int value = Class.method(1, \"Hello\");", "int test = Class.method(0);",
                                    "float test1 = 109.5f;", "String test2 = \"Hello\";", "test = true;", "int test3 = xValue;",
                                    "char y = z;", "test = Class.method();", "test = Class.method(1, 2, 3);", "x = 1;"};

        for(int i = 0; i < rules.length; i++) {
            node = new StoreValueNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals( expectedStrings[i], node.generateCode());
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
