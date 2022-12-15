package main.java.tw.jruletest.generators;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Toby Wride
 *
 * Writes the generated code into test suites as Java files
 *
 * */

public class TestSuiteGenerator {

    /**
     * Constructs the test suite and writes it to a Java file in a default package called 'generated'
     *
     * @param filename: the name of the file to write the test suite into
     * */

    public static void writeSuiteToFile(String code, String filename) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/java/" + filename));
            writer.write(code);
            writer.close();
        } catch(IOException e) {
            System.out.println("Couldn't create a file called: " + filename);
        }
    }

    /**
     * Constructs the test suite and writes it to a Java file in a specified package
     *
     * @param filename: the name of the file to write the suite to
     * @param packageName: the name of the package to write the file into
     * */

    public static void writeSuiteToFile(String code, String filename, String packageName) {
        writeSuiteToFile(code, packageName.replaceAll("\\.", "/") + "/" + filename);
    }

    /**
     * Constructs the test suite and writes it to a Java file in a specified package
     *
     * @param codeBlocks: list of blocks of code for all the test cases in the test suite
     * @param filename: the name of the file to write the suite to
     * */

    public static void writeSuiteToFile(List<String> codeBlocks, String filename) {
        writeSuiteToFile(constructTestSuite(codeBlocks), filename);
    }

    /**
     * Constructs the test suite and writes it to a Java file in a default package and filename
     *
     * @param codeBlocks: a list of the code for each test case in the suite
     * @return constructed test suite as a single string
     * */

    public static String constructTestSuite(List<String> codeBlocks) {
        String code = "package example;\n\npublic class TestSuite {";
        for(String codeBlock: codeBlocks) {
            code += "\n\n" + TestCaseGenerator.writeTestCase(codeBlock);
        }
        return code + "\n}";
    }
}
