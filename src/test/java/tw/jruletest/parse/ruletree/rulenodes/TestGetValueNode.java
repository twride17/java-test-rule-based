package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.translation.VariableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestGetValueNode {

    private GetValueNode node;

    /* Testing rule validation for Get Value node */

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

        VariableStore.addVariable(Runner.getCurrentMethod(), "xValue", int.class);
    }

    @Test
    public void testGetValueRuleWithKeyword() {
        String rule = "Get value of xValue";
        node = new GetValueNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueRuleWithNoKeyword() {
        String rule = "value of xValue";
        node = new GetValueNode();
        try {
            node.validateRule(rule);
            Assert.fail(rule + ": passed when should fail");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidGetValueRuleWithExtraRule() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        String rule = "Get value of Class.method and get value of y";
        node = new GetValueNode();
        try {
            Assert.assertEquals(25, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    /* Testing code generation for Get Value node */
    @Test
    public void testCodeGeneration() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");
        String[] rules = {"Get value of xValue", "Get Class.method", "Get value of Example.x and store in y",
                            "Get Class.method with arguments: `Hello world`, 10 and -0.89f, xValue"};

        String[] expectedStrings = {"int xValue1 = xValue;", "int methodValue = Class.method();", "int xValue2 = Example.x;",
                                    "int methodValue1 = Class.method(\"Hello world\", 10, -0.89f, xValue);"};

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
