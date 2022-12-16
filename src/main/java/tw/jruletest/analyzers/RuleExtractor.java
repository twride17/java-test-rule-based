package tw.jruletest.analyzers;

import tw.jruletest.app.Runner;
import tw.jruletest.files.TestClassFile;

import java.io.File;
import java.util.List;

public class RuleExtractor {

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

    public static void extractRules(File testFile) {
        TestClassFile testClassFile = new TestClassAnalyzer(testFile).readTestClass();
        Runner.addTestClass(testClassFile.getClassName(), testClassFile.getExtractedRules());
    }
}
