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

public class TestGetValueNode {

    private GetValueNode node;

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

        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", int.class);
    }

    @Test
    public void testGetValueRuleWithKeyword() {
        String rule = "value of xValue";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(rule.length(), node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testValidGetValueRuleWithExtraRule() {
        String rule = "value of Class.method and get value of y";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.assertEquals(21, node.getEndIndex());
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    /* Testing code generation for Get Value node */
    @Test
    public void testCodeGeneration() {
        String[] rules = {"value of xValue", "Class.method", "value of Example.x and store in y",
                            "Class.method2 with arguments: `Hello world`, 10 + -0.89f is equal to 90f * 0.5f / 2, 100000.01f"};

        String[] expectedStrings = {"int xValue1 = xValue;", "int methodValue = Class.method();", "int xValue2 = Example.x;",
                                    "int method2Value = Class.method2(\"Hello world\", ((10 + -0.89f) == (90f * (0.5f / 2))), 100000.01f);"};

        for(int i = 0; i < rules.length; i++) {
            node = new GetValueNode();
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
