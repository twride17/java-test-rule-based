package tw.jruletest.parse.parser;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.Parser;
import tw.jruletest.translation.VariableStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestParserCodeGeneration {

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
        VariableStore.addVariable("", "value", int.class);
        VariableStore.addVariable("", "xValue", double.class);
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\main\\java");
        Runner.createTestClassLoader();
        Runner.runCommand("javac -cp src " + System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\testprograms\\*.java ");
        Runner.getLoader().setTopPackage("tw");
    }

    @Test
    public void testCodeGenerationOfSingleRules() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");

        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                            "Call method Example.exampleMethod then store value of xValue in xValue1 and expect xValue1 to equal `New string`",
                            "call Example.exampleMethod: -0.987f, call Class.method then store value of Example.m with: true in x and expect x to not equal `String`",
                            "  Store -100 in x, store 0.5f in y, store value of Class.method with arguments: false and `New string`, -3.4 in test",
                            "Get value of xValue, store Class.method with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};

        String[] expectedCodeBlocks = {"int methodValue = Class.method(1, \"Hello\");\nint xValue = value;\nExpectations.expect(xValue).toEqual(1);\n",
                                        "Example.exampleMethod();\ndouble xValue1 = xValue;\nExpectations.expect(xValue1).toEqual(\"New string\");\n",
                                        "Example.exampleMethod(-0.987f);\nClass.method();\nString x = Example.m(true);\nExpectations.expect(x).toNotEqual(\"String\");\n",
                                        "int x = -100;\nfloat y = 0.5f;\nint test = Class.method(false, \"New string\", -3.4);\n",
                                        "double xValue2 = xValue;\nint z = Class.method(\"Hello and goodbye\", 1.5, false);\nExpectations.expect(x).toEqual(z);\n"};

        for(int i = 0; i < rules.length; i++) {
            System.out.println(rules[i]);
            Assert.assertEquals(expectedCodeBlocks[i], Parser.parseRule(rules[i]));
        }
    }

    @Test
    public void testCodeGenerationOfRuleBlock() {
        loadCLass("tw.jruletest.testexamples.testprograms.Class");
        loadCLass("tw.jruletest.testexamples.testprograms.Example");

        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                "Call method Example.exampleMethod then store value of xValue in xValue1 and expect xValue1 to equal `New string`",
                "call Example.exampleMethod: -0.987f, call Class.method then store value of Example.m with: true in x and expect x to not equal `String`",
                "  Store -100 in x, store 0.5f in y, store value of Class.method with arguments: false and `New string`, -3.4 in test",
                "Get value of xValue, store Class.method with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};

        String expectedCode = "int methodValue = Class.method(1, \"Hello\");\nint xValue = value;\nExpectations.expect(xValue).toEqual(1);\n"
                            + "Example.exampleMethod();\ndouble xValue1 = xValue;\nExpectations.expect(xValue1).toEqual(\"New string\");\n"
                            + "Example.exampleMethod(-0.987f);\nClass.method();\nString x = Example.m(true);\nExpectations.expect(x).toNotEqual(\"String\");\n"
                            + "int x = -100;\nfloat y = 0.5f;\nint test = Class.method(false, \"New string\", -3.4);\n"
                            + "double xValue2 = xValue;\nint z = Class.method(\"Hello and goodbye\", 1.5, false);\nExpectations.expect(x).toEqual(z);\n";

        Assert.assertEquals(expectedCode, Parser.parseRules(rules));
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
