package tw.jruletest.generators;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tw.jruletest.files.FileFinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTestSuiteGenerator {

    private Map<String, String> codeBlocks = new HashMap<>();

    @Before
    public void setup() {
        codeBlocks.put("testMethod1", "Example1 Code 1;\nExample1 Code 2;");
        codeBlocks.put("testMethod2", "Example2 Code 1;\nExample2 Code 2;");
        codeBlocks.put("testMethod3", "Example3 Code 1;\nExample3 Code 2;");
        codeBlocks.put("testMethod4", "Example4 Code 1;\nExample4 Code 2;");
        codeBlocks.put("testMethod5", "Example5 Code 1;\nExample5 Code 2;");
    }

    @Test
    public void testTestSuiteConstruction() {
        String generatedCode = TestSuiteGenerator.constructTestSuite(codeBlocks, "Test", "example");

        Assert.assertTrue(generatedCode.contains("package generated.example;"));
        Assert.assertTrue(generatedCode.contains("public class Test"));

        for(String methodName: codeBlocks.keySet()) {
            String methodCode = "\tpublic void " + methodName + "() {";
            String[] lines = codeBlocks.get(methodName).split("\n");
            for(String line: lines) {
                methodCode += "\n\t\t" + line;
            }
            methodCode += "\n\t}";
            Assert.assertTrue(generatedCode.contains(methodCode));
        }
    }

    @Test
    public void testWritingToFileWithGeneratedCode() {
        String code = "Line1\nLine2\nLine3";
        String filepath = "generated\\testsuite\\TestSuite.java";
        TestSuiteGenerator.writeSuiteToFile(code, filepath);

        try {
            List<String> lines = Files.readAllLines(Paths.get(System.getProperty("user.dir") + "\\src\\test\\java\\" + filepath));
            String[] expectedLines = code.split("\n");
            for(int i = 0; i < expectedLines.length; i++) {
                Assert.assertEquals(expectedLines[i], lines.get(i));
            }
        } catch(IOException e ){
            Assert.fail("Didn't find the required file.");
        }
    }

    @Test
    public void testWritingToFileWithCodeList() {
        TestSuiteGenerator.writeSuiteToFile(codeBlocks, "testsuite.TestSuite");
        FileFinder.collectFiles(System.getProperty("user.dir") + "/src/test/java/generated");
        File file = FileFinder.getFiles("").get(0);

        Assert.assertNotNull(file);
        Assert.assertEquals(file.getPath(), System.getProperty("user.dir") + "\\src\\test\\java\\generated\\testsuite\\TestSuite.java");
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/generated/testsuite/TestSuite.java"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/generated/testsuite"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/generated"));
        } catch(IOException e) {
            System.out.println("Couldn't delete generated files.");
        }
    }
}
