package tw.jruletest.generators;

import tw.jruletest.analyzers.ImportCollector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * @author Toby Wride
 *
 * Writes test suites containing generated code to Java files
 * */

public class TestSuiteGenerator {

    /**
     * Constructs the test suite and writes it to a Java file in a package starting with 'generated'
     *
     * @param code code to write to the specified file
     * @param filename the name of the file to write the test suite to
     * */

    public static void writeSuiteToFile(String code, String filename) {
        String[] directoryNames = filename.substring(0, filename.lastIndexOf("\\")).split("\\\\");
        String directoryPath = "src\\test\\java";
        for(String directoryName: directoryNames) {
            directoryPath += "\\" + directoryName;
            File currentDirectory = new File(directoryPath);
            if(!currentDirectory.exists()) {
                currentDirectory.mkdir();
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src\\test\\java\\" + filename));
            writer.write(code);
            writer.close();
        } catch(IOException e) {
            System.out.println("Couldn't create a file called: " + filename);
        }
    }

    /**
     * Constructs the test suite and writes it to a Java file in a specified package
     *
     * @param codeBlocks list of blocks of code mapped to their respective methods
     * @param className the full name of the class the code has been generated for. The filename is generated using this
     * */

    public static void writeSuiteToFile(Map<String, String> codeBlocks, String className) {
        String filename = "generated\\" + className.replaceAll("\\.", "\\\\") + ".java";
        int classNameIndex = className.lastIndexOf(".");
        writeSuiteToFile(constructTestSuite(codeBlocks, className.substring(classNameIndex+1), className.substring(0, classNameIndex)), filename);
    }

    /**
     * Constructs the test suite and writes it to a Java file of the same name, in a specified package
     *
     * @param codeBlocks a list of the code for each test case in the suite
     * @param className the full name of the class the code has been generated for. The filename is generated using this
     * @param packageName name of the package the test suite should belong to
     *
     * @return the constructed test suite as a String
     * */

    public static String constructTestSuite(Map<String, String> codeBlocks, String className, String packageName) {
        String testCode = "";
        for(String methodName: codeBlocks.keySet()) {
            testCode += "\n\n" + TestCaseGenerator.writeTestCase(codeBlocks.get(methodName), methodName);
        }

        String topCode = "package generated." + packageName + ";\n\n// Necessary Imports\n";
        for(String importStatement: ImportCollector.getImports()) {
            topCode += importStatement + "\n";
        }
        ImportCollector.resetImports();
        topCode += "\npublic class " + className + " {";

        return topCode + testCode + "\n}";
    }
}
