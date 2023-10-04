package tw.jruletest;

import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.expectations.UnsatisfiedExpectationError;
import tw.jruletest.files.FileFinder;
import tw.jruletest.loggers.TestLogger;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.SourceClassLoader;
import tw.jruletest.virtualmachine.TestClassLoader;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * TestExecutor is designed to execute all the tests from all the generated test suites and record the outcomes of each test.
 * The execution can either be started by calling this from Runner or by simply using TestExecutor's main method.
 * In order to use TestExecutor on its own, all test suites must be located within a 'generated' directory.
 *
 * @author Toby Wride
 * */

public class TestExecutor {

    /**
     * Executes the test suites by loading the classes and executing each test case in each class.
     * The test results are logged in the appropriate log file.
     *
     * @throws CompilationFailureException thrown if the generated test suites do not compile successfully
     * */

    public static void executeTests() throws CompilationFailureException {
        FileFinder.collectFiles(Runner.getRootPath());
        TestClassLoader.loadGeneratedTestClasses();

        List<File> generatedTestFiles = FileFinder.getFiles("generated");
        for (File generatedFile : generatedTestFiles) {
            executeGeneratedTestClass(generatedFile.getPath().replace(".java", ".class"));
        }

        TestLogger.printLog();
        Runner.clearClassFiles();
    }

    private static void executeGeneratedTestClass(String filename) {
        String className = FileFinder.getClassName(filename);
        TestLogger.setTestClassDetails(className);
        try {
            Class<?> foundClass = Class.forName(className, false, JavaClassLoader.getLoader());
            Object instance = foundClass.newInstance();

            Method[] testMethods = foundClass.getDeclaredMethods();
            for (Method test : testMethods) {
                attemptTest( test, instance);
            }
        } catch(InstantiationException | IllegalAccessException e) {
            TestLogger.errorEncountered("Couldn't instantiate or access class: " + className);
            System.out.println("Couldn't instantiate or access class");
        } catch(ClassNotFoundException e) {
            TestLogger.errorEncountered("Couldn't find the test class:" + className);
            System.out.println("Couldn't find the test class:" + className);
        } catch(LinkageError e) {}

        TestLogger.writeToFile();
    }

    private static void attemptTest(Method test, Object instance) {
        try {
            executeTest(instance, test);
            TestLogger.passedTest(test.getName());
        } catch(Throwable e) {
            String error = ((UnsatisfiedExpectationError)e).getErrorMessage();
            TestLogger.failedTest(test.getName(), error);
        }
    }

    private static void executeTest(Object instance, Method testMethod) throws Throwable {
        try {
            testMethod.invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnsatisfiedExpectationError) {
                throw cause;
            } else {
                TestLogger.errorEncountered("Failed to call method: " + testMethod.getName());
                System.out.println("Failed to call method: " + testMethod.getName());
            }
        }
    }

    /**
     * Main entrypoint for executing tests. Assumes files are located in 'generated' package.
     * This method controls the execution of all available test classes.
     *
     * @param args this parameter has no use
     * */

    public static void main(String[] args) {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(Runner.getRootPath());

        String firstClass = FileFinder.getClassNames(FileFinder.getFiles(Runner.getRootPath() + "\\main\\java")).get(0);
        JavaClassLoader.setLoaderRootPackage(firstClass.substring(0, firstClass.indexOf('.')));

        try {
            SourceClassLoader.loadClasses();
            executeTests();
        } catch(CompilationFailureException e) {
            JOptionPane.showMessageDialog(null, e.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            Runner.clearClassFiles();
            System.exit(0);
        }
    }
}
