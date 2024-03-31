package tw.jruletest.parse.ruletree.rootnodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
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
        String[] rules = {"value of Class.method in xValue", "value of Class.isTrue: false in xValue1",
                            "result of Class.method2 with: `Hello`, true and 0.5f in value", "result of Class.isTrue with arguments: not false in test"};
        for(String rule: rules) {
            node = new StoreValueNode();
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
        String[] rules = {"109.5f in test", "0.5f in test", "`Hello` in test", "true in test"};
        for(String rule: rules) {
            node = new StoreValueNode();
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
        String[] rules = {"value of xValue in test", "xValue in testValue", "x in y", "x in xValue"};
        for(String rule: rules) {
            node = new StoreValueNode();
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
        String[] rules = {"x in 1", "`hello` in 123f", "1 in true", "true in false", "test in 100"};
        for(String rule: rules) {
            node = new StoreValueNode();
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    @Test
    public void testStoreValueInMethodResult() {
        String[] rules = {"x in Class.method", "`hello` in Class.method with: x", "Class.method in Class.method2"};
        for(String rule: rules) {
            node = new StoreValueNode();
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    @Test
    public void testStoreValueRulesPlusExtraEnding() {
        String[] rules = {"1 in x and expect", "Class.method in test, expect", "Class.isTrue: true in dummy and expect",
                            "`New string` in xValue and store", "Class.method2 with arguments: `1`, 2 is equal to 3 and 3.5f in test and expect"};
        int[] indices = {6, 20, 27, 22, 67};
        for(int i = 0; i < 5; i++) {
            node = new StoreValueNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(indices[i], node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"store", "x", "store in x", "in test", "test", "store value of Class.method in z",
                            "`hello in y", ".67f in y", "hello' in z", "`hello` in 1value"};
        for(String rule: rules) {
            node = new StoreValueNode();
            System.out.println(rule);
            try {
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Store Value node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"value of Class.method in xValue", "result of Example.concat with: `Hello` and `World` in value",
                            "result of Class.isTrue with arguments: not false in test", "109.5f in test1", "`Hello` in test2",
                            "true in test", "value of xValue in test3", "z in y", "Class.method in test, expect",
                            "Class.method2 with arguments: `1`, 2 is equal to 3 and 3.5f in test and expect", "1 in x and expect"};

        String[] expectedStrings = {"xValue = Class.method();", "String value = Example.concat(\"Hello\", \"World\");", "boolean test = Class.isTrue(!false);",
                                    "float test1 = 109.5f;", "String test2 = \"Hello\";", "test = true;", "int test3 = xValue;",
                                    "char y = z;", "test = Class.method();", "test = Class.method2(\"1\", (2 == 3), 3.5f);", "x = 1;"};

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
