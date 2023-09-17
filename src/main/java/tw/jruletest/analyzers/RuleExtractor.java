package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.test.TestClass;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RuleExtractor {

    /**
     * Obtains the rules defined in all files from test directory
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
