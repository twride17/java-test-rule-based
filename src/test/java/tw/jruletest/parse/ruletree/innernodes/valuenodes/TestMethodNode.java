package tw.jruletest.parse.ruletree.innernodes.valuenodes;

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

    /* Testing rule validation for MethodNode */

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
        String[] rules = {"method Class.method", "Class.example", "Example.m"};
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
        String[] rules = {"method Class.method3: `Hello world`", "method Example.m3 with arguments: 101.971f",
                            "Class.method1 with: -24", "Class.method1: xValue", "Example.m3: -90.45f", "Class.isTrue: true"};
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
        String[] rules = {"method Test.example4: `Hello, world and his wife`, -90.5f",
                            "method Class.method4 with arguments: 101.971f and false", "Test.example2: x1 and x2",
                            "Test.example2: 123.45, -90", "Example.concat with: `Hello and goodbye` and `String`",
                            "Test.example2 with: 12.3 and xValue", "Class.method4: -90.45f and true"};
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
        String[] rules = {"method Example.m2: `Hello world`, x, -67.5f", "Class.method2 with: `This`, true and 101.971f",
                            "Test.example3 with arguments: false and 0.67f, true", "Class.method2: `That`, false and -1f"};
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
        String[] rules = {"Class.method and store value in y", "Class.method, store", "Class.method1: 1 in dummy",
                            "Class.method2 with arguments: `String`, true and 0.0f and store", "Class.example and get",
                            "Test.example2: 45.6, 1 in xValue", "method Example.concat with: `Hello` and `World` and call",
                            "method Class.method and call method Example.concat with arguments: `Hello` and `World`"};
        int[] indices = {12, 12, 16, 53, 13, 22, 47, 19};
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
    public void testValidRuleStructuresInvalidParameterTypes() {
        String[] rules = {"Class.method: 3", "Class.example: 3 and 4", "Class.method3 with: 67.5f", "Test.setValue: -0.5",
                            "Example.m2 with arguments: `Hello` and 23", "Class.isTrue: 45", "Test.example4: 90.45f, `Hi`"};
        for(String rule: rules) {
            try {
                node = new MethodNode();
                node.validateRule(rule);
                Assert.fail("'" + rule + "': passed when expected to fail");
            } catch (InvalidRuleStructureException e) { }
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
                Assert.fail("'" + rule + "': passed when expected to fail");
            } catch (InvalidRuleStructureException e) { }
        }
    }

    @Test
    public void testComplexArguments() {
        String[] rules = {"method Test.example2 with: 67, value of Class.method", "Test.example4: `Hello` + `World` and 0.545f * 3 / 5",
                "method Class.isTrue with arguments: value of Class.example is greater than 0 and value of x is equal to xValue + 7",
                "Test.example2: value of Example.m2: Example.concat with: value of Example.m and `String`, -5 - Class.method and Class.example / -1.5f and x2 + 10 * x1",
                "Class.method4: 3.5f * -3.5f * -9, value of x is not equal to x1 or not x is greater than 0.9f and x is equal to xValue",
                "Test.example3 with: x is less than x2 and not Class.isTrue with: x is greater than 1, 0.5f * xValue, Class.isTrue with: false"};
        for (String rule : rules) {
            try {
                node = new MethodNode();
                System.out.println(rule);
                node.validateRule(rule);
                System.out.println("Completed");
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rule + "': failed");
            }
        }
    }

    /* Testing code generation for MethodNode */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"method Class.method", "Class.example", "Example.m", "method Class.method3: `Hello world`",
                            "method Example.m3 with arguments: 101.971f", "Class.method1 with: -24", "Class.method1: xValue",
                            "Example.m3: -90.45f", "Class.isTrue: true", "method Test.example4: `Hello, world and his wife`, -90.5f",
                            "method Class.method4 with arguments: 101.971f and false", "Test.example2: x1 and x2",
                            "Test.example2: 123.45, -90", "Example.concat with: `Hello and goodbye` and `String`",
                            "Test.example2 with: 12.3 and xValue", "Class.method4: -90.45f and true",
                            "method Example.m2: `Hello world`, x, -67.5f", "Class.method2 with: `This`, true and 101.971f",
                            "Test.example3 with arguments: false and 0.67f, true", "Class.method2: `That`, false and -1f",
                            "method Test.example2 with: 67, value of Class.method", "Test.example4: `Hello` + `World` and 0.545f * 3 / 5",
                            "method Class.isTrue with arguments: value of Class.example is greater than 0 and value of x is equal to xValue + 7",
                            "Test.example2: value of Example.m2: Example.concat with: value of Example.m and `String`, -5 - Class.method and Class.example / -1.5f and x2 + 10 * x1",
                            "Class.method4: 3.5f * -3.5f * -9, value of x is not equal to x1 or not x is greater than 0.9f and x is equal to xValue",
                            "Test.example3 with: x is less than x2 and not Class.isTrue with: x is greater than 1, 0.5f * xValue, Class.isTrue with: false"};

        String[] expectedStrings = {"Class.method()", "Class.example()", "Example.m()", "Class.method3(\"Hello world\")",
                                    "Example.m3(101.971f)", "Class.method1(-24)", "Class.method1(xValue)", "Example.m3(-90.45f)",
                                    "Class.isTrue(true)", "Test.example4(\"Hello, world and his wife\", -90.5f)",
                                    "Class.method4(101.971f, false)", "Test.example2(x1, x2)", "Test.example2(123.45, -90)",
                                    "Example.concat(\"Hello and goodbye\", \"String\")", "Test.example2(12.3, xValue)",
                                    "Class.method4(-90.45f, true)", "Example.m2(\"Hello world\", x, -67.5f)",
                                    "Class.method2(\"This\", true, 101.971f)", "Test.example3(false, 0.67f, true)",
                                    "Class.method2(\"That\", false, -1f)", "Test.example2(67, Class.method())",
                                    "Test.example4((\"Hello\" + \"World\"), (0.545f * (3 / 5)))",
                                    "Class.isTrue(((Class.example() > 0) && (x == (xValue + 7))))",
                                    "Test.example2(Example.m2(Example.concat(Example.m(), \"String\"), (-5 - Class.method()), (Class.example() / -1.5f)), (x2 + (10 * x1)))",
                                    "Class.method4((3.5f * (-3.5f * -9)), ((x != x1) || (!(x > 0.9f) && (x == xValue))))",
                                    "Test.example3(((x < x2) && !Class.isTrue((x > 1))), (0.5f * xValue), Class.isTrue(false))"};

        for(int i = 0; i < rules.length; i++) {
            node = new MethodNode();
            try {
                System.out.println(rules[i]);
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
