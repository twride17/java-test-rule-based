package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.test.TestClass;

import java.io.File;
import java.util.List;

/**
 * Class that extracts rules from the required files and passes the rules to the Runner class.
 *
 * @author Toby Wride
 * */

public class RuleExtractor {

    /**
     * Obtains the rules defined in all Java files located in the 'test' directory
     * */

    public static void extractRules() {
        extractRules(FileFinder.getFiles(Runner.getRootPath() + "\\test\\java"));
    }

    /**
     * Obtains the rules defined in each of the files provided and passes them to the Runner class
     *
     * @param testFiles: list of files from which to extract the rules
     * */

    public static void extractRules(List<File> testFiles) {
        for(File testFile: testFiles) {
            extractRules(testFile);
        }
    }


    /**
     * Extracts the rules from the provided file
     *
     * @param testFile: file from which to extract the rules
     * */
    public static void extractRules(File testFile) {
        try {
            TestClass testClassFile = new TestClass(FileFinder.getClassName(testFile));
            testClassFile.read();
            Runner.addTestClass(testClassFile.getClassName(), testClassFile.getRules());
        } catch(ClassNotFoundException e) {
            System.out.println("Couldn't find class");
        }
    }
}
