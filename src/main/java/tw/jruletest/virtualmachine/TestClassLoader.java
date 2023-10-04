package tw.jruletest.virtualmachine;

import tw.jruletest.analyzers.TestClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.test.TestClass;

import java.util.ArrayList;

/**
 * Utility class designed to load specifically test classes
 *
 * @author Toby Wride
 * */

public class TestClassLoader {

    /**
     * Loads all test classes using the 'test' directory as the default root folder.
     *
     * @throws CompilationFailureException thrown if the test classes failed to compile
     * */

    public static void loadClasses() throws CompilationFailureException {
        loadClasses("\\test\\");
    }

    /**
     * Loads all test classes from a specified root directory
     *
     * @param requiredDirectory the directory from which to compile the test classes
     *
     * @throws CompilationFailureException thrown if the test classes failed to compile
     * */

    public static void loadClasses(String requiredDirectory) throws CompilationFailureException {
        ArrayList<String> classes = JavaClassLoader.loadClasses(requiredDirectory);
        for(String cls: classes) {
            try {
                TestClassAnalyzer.addTestClass(new TestClass(cls));
            } catch(ClassNotFoundException e) { }
        }
    }

    /**
     * Loads all generated test classes using the 'generated' package as the default root folder
     *
     * @throws CompilationFailureException thrown if the generated test classes failed to compile
     * */

    public static void loadGeneratedTestClasses() throws CompilationFailureException {
        JavaClassLoader.loadClasses(FileFinder.getFiles("\\main\\"), FileFinder.getFiles("\\generated\\"));
    }
}
