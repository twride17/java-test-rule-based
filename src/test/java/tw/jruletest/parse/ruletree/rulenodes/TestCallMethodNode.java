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

public class TestCallMethodNode {

    private CallMethodNode node;

    /* Testing rule validation for Call Method node */

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

        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", int.class);
    }

    @Test
    public void testCallMethodRuleWithKeyword() {
        String rule = "Call method Class.method with arguments: `Hello`, true and -0.78f";
        node = new CallMethodNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testCallMethodRuleWithNoKeyword() {
        String rule = "Class.method: 0";
        node = new CallMethodNode();
        try {
            node.validateRule(rule);
            Assert.fail(rule + ": passed when should fail");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidCallMethodRuleWithExtraRule() {
        String rule = "Call method Class.method with arguments: `Hello`, true and -0.78f and expect x to equal 0";
        node = new CallMethodNode();
        try {
            Assert.assertEquals(65, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    /* Testing code generation for Call Method node */
    @Test
    public void testCodeGeneration() {

        String[] rules = {"call method Class.method", "call method Class.method: `Hello world`", "call method Class.method with arguments: 101.971f",
                            "Call Class.method with: -24", "call Class.method: xValue", "Call Class.method: -90.45f", "Call method Class.method: true",
                            "call method Class.method: `Hello, world and his wife`, -90.5f", "call Class.method with: `Hello and goodbye` and `String`",
                            "call method Class.method: `Hello world`, 32, -67.5f", "call Class.method with: 101.971f, true and `This`",
                            "call Class.method: 123, `hello, it's me`, -0.98f and true and store value in y", "call Class.method, store",
                            "Call Class.method: 1 in dummy", "call Example.exampleMethod: 1, -2 and 45.6f, true and `New` and 56",
                            "Call method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`"};

        String[] expectedStrings = {"Class.method();", "Class.method(\"Hello world\");", "Class.method(101.971f);",
                                    "Class.method(-24);", "Class.method(xValue);", "Class.method(-90.45f);",
                                    "Class.method(true);", "Class.method(\"Hello, world and his wife\", -90.5f);",
                                    "Class.method(\"Hello and goodbye\", \"String\");", "Class.method(\"Hello world\", 32, -67.5f);",
                                    "Class.method(101.971f, true, \"This\");", "Class.method(123, \"hello, it's me\", -0.98f, true);",
                                    "Class.method();", "Class.method(1);", "Example.exampleMethod(1, -2, 45.6f, true, \"New\", 56);", "Class.method();"};

        for(int i = 0; i < rules.length; i++) {
            node = new CallMethodNode();
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
