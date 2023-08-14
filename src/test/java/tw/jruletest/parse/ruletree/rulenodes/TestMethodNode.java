package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.variables.VariableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestMethodNode {

    private MethodNode node;

    /* Testing rule validation for Method node */

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

        String[] variables = {"x", "x1", "x2", "xValue"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testMethodCallNoArguments() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[] rules = {"method Class.method", "Class.method2", "Example.m"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallOneArgument() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String[] rules = {"method Class.method: `Hello world`", "method Class.method with arguments: 101.971f",
                            "Class.method with: -24", "Class.method: xValue", "Class.method: -90.45f", "Class.method: true"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallTwoArguments() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String[] rules = {"method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: x1 and x2",
                            "Class.method: true, 123.45", "Class.method with: `Hello and goodbye` and `String`",
                            "Class.method: 12.3 and xValue", "Class.method: -90.45f and true", "Class.method: true and 0"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallThreeArguments() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String[] rules = {"method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method with arguments: 24 and false, xValue", "Class.method: xValue and -0.9f and false"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                Assert.assertEquals(rule.length(), node.validateRule(rule));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidRulesPlusExtraRule() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[] rules = {"Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "Class.method, store",
                            "Class.method: 1 in dummy", "Class.example with arguments: 0.0f, `String` and store",
                            "Class.method and store", "Example.exampleMethod: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "method Class.method, call method Example.exampleMethod with arguments: `Hello` and `World`",
                            "method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`"};
        int[] indices = {52, 12, 15, 44, 12, 61, 19, 19};
        node = new MethodNode();
        for(int i = 0; i < rules.length; i++) {
            try {
                Assert.assertEquals(indices[i], node.validateRule(rules[i]));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        loadCLass("tw.jruletest.testexamples.testprograms.Test");
        String[] rules = {"call", "call method", "call method method", "call xValue", "call Example.method with x",
                            "call Example.x with arguments x and y", "method Example.x: x, and y", "call Class.",
                            "Call Example.method with arguments: and 7", "Call Example.method with arguments: 7 and",
                            "Example.method arguments: x", " call Example.method with: x", "Example.method : x", ": x",
                            "call example.method", "call example.Method", "call Example.Method", "Call method .method",
                            "call example.method, with arguments: 1", "call example.method and with arguments: 1",
                            "call example.method with arguments: 1", "call example.method then with arguments: 1"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                System.out.println(rule);
                node.validateRule(rule);
                Assert.fail("Failed: '" + rule + "' passed but should have failed");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    /* Testing code generation for Method node */
    @Test
    public void testCodeGeneration() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        loadCLass("tw.jruletest.testexamples.testprograms.Test");
        String[] rules = {"method Class.method", "Test.exampleMethod", "method Class.method: `Hello world`",
                            "method Class.method with arguments: 101.971f", "Class.method with: -24", "Class.method: xValue",
                            "Class.method: -90.45f", "method Class.method: true", "method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: true, 123.45",
                            "Class.method with: `Hello and goodbye` and `String`", "Class.method: 12.3 and xValue", "Class.method: true and 0",
                            "method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "Class.method, store",
                            "Class.method: 1 in dummy", "Class.method and store", "Example.exampleMethod: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`"};

        String[] expectedStrings = {"Class.method()", "Test.exampleMethod()", "Class.method(\"Hello world\")",
                                    "Class.method(101.971f)", "Class.method(-24)", "Class.method(xValue)",
                                    "Class.method(-90.45f)", "Class.method(true)", "Class.method(\"Hello, world and his wife\", -90.5f)",
                                    "Class.method(101.971f, false)", "Class.method(true, 123.45)", "Class.method(\"Hello and goodbye\", \"String\")",
                                    "Class.method(12.3, xValue)", "Class.method(true, 0)", "Class.method(\"Hello world\", 32, -67.5f)",
                                    "Class.method(101.971f, true, \"This\")", "Class.method(123, \"hello, it's me\", -0.98f, true)",
                                    "Class.method()", "Class.method(1)", "Class.method()",
                                    "Example.exampleMethod(1, -2, 45.6f, true, \"New\", 56)", "Class.method()"};

        for(int i = 0; i < rules.length; i++) {
            node = new MethodNode();
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
