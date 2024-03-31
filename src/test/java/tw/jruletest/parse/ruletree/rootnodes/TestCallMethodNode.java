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
                SourceClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {}

        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", int.class);
    }

    @Test
    public void testValidCallMethodRule() {
        String rule = "method Class.method2 with arguments: `Hello`, true and -0.78f";
        node = new CallMethodNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(e.getErrorMessage());
            Assert.fail("Failed");
        }
    }

    @Test
    public void testValidCallMethodRuleWithExtraRule() {
        String rule = "method Class.method2 with arguments: `Hello`, true and -0.78f and expect x to equal 0";
        node = new CallMethodNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(61, node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(e.getErrorMessage());
            Assert.fail("Failed");
        }
    }

    /* Testing code generation for Call Method node */
    @Test
    public void testCodeGeneration() {

        String[] rules = {"method Class.method", "method Class.method3: `Hello world`", "method Example.m3 with arguments: 101.971f",
                            "Class.method3 with: `Hello ` + -24", "Class.method3: `Hello ` + xValue", "Example.m3: -90.45f",
                            "method Class.method2: `Hello, world and his wife`, xValue is equal to 1 and -90.5f",
                            "method Class.method2: `Hello world`, Class.isTrue: 90 is greater than xValue, -67.5f",
                            "Class.method2 with: `Hello` + `World` and value of Class.isTrue with: false or xValue is equal to 1 and 101.971f * 7",
                            "Class.method2: Example.m + `hello, it's me`, true and -0.987f + 9 and store value in y", "Class.method, store",
                            "Class.method3: `1` in dummy", "method Class.method and call method Example.exampleMethod with arguments: `Hello` and `World`"};

        String[] expectedStrings = {"Class.method();", "Class.method3(\"Hello world\");", "Example.m3(101.971f);",
                                    "Class.method3((\"Hello \" + -24));", "Class.method3((\"Hello \" + xValue));", "Example.m3(-90.45f);",
                                    "Class.method2(\"Hello, world and his wife\", (xValue == 1), -90.5f);",
                                    "Class.method2(\"Hello world\", Class.isTrue((90 > xValue)), -67.5f);",
                                    "Class.method2((\"Hello\" + \"World\"), (Class.isTrue(false) || (xValue == 1)), (101.971f * 7));",
                                    "Class.method2((Example.m() + \"hello, it's me\"), true, (-0.987f + 9));", "Class.method();",
                                    "Class.method3(\"1\");", "Class.method();"};

        for(int i = 0; i < rules.length; i++) {
            node = new CallMethodNode();
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(expectedStrings[i], node.generateCode());
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
                System.out.println(e.getErrorMessage());
            }
        }
    }

    @After
    public void teardown() {
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
