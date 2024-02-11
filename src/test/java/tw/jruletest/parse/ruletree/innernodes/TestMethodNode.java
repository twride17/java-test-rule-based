package tw.jruletest.parse.ruletree.innernodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.ruletree.innernodes.valuenodes.MethodNode;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestMethodNode {

    private MethodNode node;

    /* Testing rule validation for Method node */

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

        String[] variables = {"x", "x1", "x2", "xValue"};
        for(String variable: variables) {
            VariableStore.addVariable(Runner.getCurrentMethod(), variable, int.class);
        }
    }

    @Test
    public void testMethodCallNoArguments() {
        String[] rules = {"method Class.method", "Class.method2", "Example.m"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallOneArgument() {
        String[] rules = {"method Class.method: `Hello world`", "method Class.method with arguments: 101.971f",
                            "Class.method with: -24", "Class.method: xValue", "Class.method: -90.45f", "Class.method: true"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallTwoArguments() {
        String[] rules = {"method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: x1 and x2",
                            "Class.method: true, 123.45", "Class.method with: `Hello and goodbye` and `String`",
                            "Class.method: 12.3 and xValue", "Class.method: -90.45f and true", "Class.method: true and 0"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testMethodCallThreeArguments() {
        String[] rules = {"method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method with arguments: 24 and false, xValue", "Class.method: xValue and -0.9f and false"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testValidRulesPlusExtraRule() {
        String[] rules = {"Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "Class.method, store",
                            "Class.method: 1 in dummy", "Class.example with arguments: 0.0f, `String` and store",
                            "Class.method and store", "Example.exampleMethod: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "method Class.method, call method Example.exampleMethod with arguments: `Hello` and `World`",
                            "method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`"};
        int[] indices = {52, 12, 15, 44, 12, 61, 19, 19};
        for(int i = 0; i < rules.length; i++) {
            try {
                node = new MethodNode();
                node.validateRule(rules[i]);
                Assert.assertEquals(indices[i], node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @Test
    public void testInvalidRuleStructures() {
        String[] rules = {"call", "call method", "call method method", "call xValue", "call Example.method with x",
                            "call Example.x with arguments x and y", "method Example.x: x, and y", "call Class.",
                            "Call Example.method with arguments: and 7", "Call Example.method with arguments: 7 and",
                            "Example.method arguments: x", " call Example.method with: x", "Example.method : x", ": x",
                            "call example.method", "call example.Method", "call Example.Method", "Call method .method",
                            "call example.method, with arguments: 1", "call example.method and with arguments: 1",
                            "call example.method with arguments: 1", "call example.method then with arguments: 1"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    @Test
    public void testComplexArguments() {
        String[] rules = {"method Test.example2 with: 67, value of Class.method, `Hello`",
                            "method Test.example2 with: value of Example.m2 with arguments: `Test String` and Class.method, -90 and 805 and `Hello`",
                            "Test.example3: not true and false, 54 + 0.9f - 6 and true and false or not false",
                            "Test.example3: true and Class.method is equal to 10 and 0.9f * 8.9f / 23, not Class.method is not equal to 89",
                            "Test.example2 with: value of Example.m2 with arguments: `Test String` and Class.method, -100 + 56 and 78 * 9 + 4 - 56 and `Hello` + 4"};
        node = new MethodNode();
        for(String rule: rules) {
            try {
                node.validateRule(rule);
                System.out.println(rule.substring(0, node.getEndIndex()));
                System.out.println(node.generateCode());
                System.out.println();
                Assert.assertEquals(rule.length(), node.getEndIndex());
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    /* Testing code generation for Method node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"method Class.method", "Test.exampleMethod", "method Class.method: `Hello world`",
                            "method Class.method with arguments: 101.971f", "Class.method with: -24", "Class.method: xValue",
                            "Class.method: -90.45f", "method Class.method: true", "method Class.method: `Hello, world and his wife`, -90.5f",
                            "method Class.method with arguments: 101.971f and false", "Class.method: true, 123.45",
                            "Class.method with: `Hello and goodbye` and `String`", "Class.method: 12.3 and xValue", "Class.method: true and 0",
                            "method Class.method: `Hello world`, 32, -67.5f", "Class.method with: 101.971f, true and `This`",
                            "Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "Class.method, store",
                            "Class.method: 1 in dummy", "Class.method and store", "Example.exampleMethod: 1, -2 and 45.6f, true and `New` and 56 in xValue",
                            "method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`",
                            "Test.example3: not true and false, 54 + 0.9f - 6 and true and false or not false",
                            "Test.example3: true and Class.method is equal to 10 and 0.9f * 8.9f / 23, not Class.method is not equal to 89",
                            "Test.example2 with: value of Example.m2 with arguments: `Test String` and Class.method, -100 + 56 and 78 * 9 + 4 - 56 and `Hello` + 4"};

        String[] expectedStrings = {"Class.method()", "Test.exampleMethod()", "Class.method(\"Hello world\")",
                                    "Class.method(101.971f)", "Class.method(-24)", "Class.method(xValue)",
                                    "Class.method(-90.45f)", "Class.method(true)", "Class.method(\"Hello, world and his wife\", -90.5f)",
                                    "Class.method(101.971f, false)", "Class.method(true, 123.45)", "Class.method(\"Hello and goodbye\", \"String\")",
                                    "Class.method(12.3, xValue)", "Class.method(true, 0)", "Class.method(\"Hello world\", 32, -67.5f)",
                                    "Class.method(101.971f, true, \"This\")", "Class.method(123, \"hello, it's me\", -0.98f, true)",
                                    "Class.method()", "Class.method(1)", "Class.method()",
                                    "Example.exampleMethod(1, -2, 45.6f, true, \"New\", 56)", "Class.method()",
                                    "Test.example3(!(true && false), (54 + (0.9f - 6)), (true && (false || !false)))",
                                    "Test.example3((true && (Class.method == 10)), (0.9f * (8.9f / 23)), !(Class.method != 89))",
                                    "Test.example2(Example.m2(\"Test String\", Class.method(), (-100 + 56)), (78 * (9 + (4-56))), (\"Hello\" + 4))"};

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
