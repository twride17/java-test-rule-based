package tw.jruletest.parse.parser;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.ParserFailureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.parse.Parser;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestParserCodeGeneration {

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        VariableStore.addVariable(Runner.getCurrentMethod(), "value", int.class);
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        try {
            ArrayList<String> classes = JavaClassLoader.loadClasses("sourceclasses");
            for(String name: classes) {
                SourceClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {}
    }

    @Test
    public void testCodeGenerationOfSingleRules() {
        String[] rules = {"Get value of Example.concat: `Hello` and `1`, store 10 * 3 + 4 in xValue then expect xValue to equal 1",
                            "Call method Class.example then store value of xValue in xValue1 and expect xValue1 to equal `New string`",
                            "call Example.m3: -0.987f, call Class.method then store value of Class.isTrue with: xValue is equal to 1 in x and expect x to not equal `String`",
                            "  Store -100 in x, store 0.5f in y, store value of Class.method2 with arguments: `New string`, false and -3 / 0.1f in test",
                            "Get value of xValue, store Class.method2 with: `Hello and goodbye`, not true and 3 * -0.5f in z then expect x to equal z  "};

        String[] expectedCodeBlocks = {"String concatValue = Example.concat(\"Hello\", \"1\");\nint xValue = (10 * (3 + 4));\nExpectations.expect(xValue).toEqual(1);\n",
                                    "Class.example();\nint xValue1 = xValue;\nExpectations.expect(xValue1).toEqual(\"New string\");\n",
                                    "Example.m3(-0.987f);\nClass.method();\nboolean x = Class.isTrue((xValue == 1));\nExpectations.expect(x).toNotEqual(\"String\");\n",
                                    "x = -100;\nfloat y = 0.5f;\nint test = Class.method2(\"New string\", false, (-3 / 0.1f));\n",
                                    "int xValue2 = xValue;\nfloat z = (Class.method2(\"Hello and goodbye\", !true, 3) * -0.5f);\nExpectations.expect(x).toEqual(z);\n"};

        for(int i = 0; i < rules.length; i++) {
            System.out.println(rules[i]);
            try {
                Assert.assertEquals(expectedCodeBlocks[i], Parser.parseRule(rules[i]));
            } catch(ParserFailureException e) {
                Assert.fail("Parser failed with error:\n" + e.getErrors());
            }

        }
    }

    @Test
    public void testCodeGenerationOfRuleBlock() {
        String[] rules = {"Get value of Example.concat: `Hello` and `1`, store 10 * 3 + 4 in xValue then expect xValue to equal 1",
                            "Call method Class.example then store value of xValue in xValue1 and expect xValue1 to equal `New string`",
                            "call Example.m3: -0.987f, call Class.method then store value of Class.isTrue with: xValue is equal to 1 in x and expect x to not equal `String`",
                            "  Store -100 in x, store 0.5f in y, store value of Class.method2 with arguments: `New string`, false and -3 / 0.1f in test",
                            "Get value of xValue, store Class.method2 with: `Hello and goodbye`, not true and 3 * -0.5f in z then expect x to equal z  "};

        String expectedCode = "String concatValue = Example.concat(\"Hello\", \"1\");\nint xValue = (10 * (3 + 4));\nExpectations.expect(xValue).toEqual(1);\n"
                            + "Class.example();\nint xValue1 = xValue;\nExpectations.expect(xValue1).toEqual(\"New string\");\n"
                            + "Example.m3(-0.987f);\nClass.method();\nboolean x = Class.isTrue((xValue == 1));\nExpectations.expect(x).toNotEqual(\"String\");\n"
                            + "x = -100;\nfloat y = 0.5f;\nint test = Class.method2(\"New string\", false, (-3 / 0.1f));\n"
                            + "int xValue2 = xValue;\nfloat z = (Class.method2(\"Hello and goodbye\", !true, 3) * -0.5f);\nExpectations.expect(x).toEqual(z);\n";

        try {
            Assert.assertEquals(expectedCode, Parser.parseRules(rules));
        } catch(ParserFailureException e) {
            Assert.fail("Parser failed with error(s):\n" + e.getErrors());
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
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/ExampleClass.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
