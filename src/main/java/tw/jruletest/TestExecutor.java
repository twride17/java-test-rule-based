package tw.jruletest;

import tw.jruletest.expectations.UnsatisfiedExpectationError;
import tw.jruletest.files.FileFinder;
import tw.jruletest.logging.TestLogger;
import tw.jruletest.virtualmachine.JavaClassCompiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TestExecutor {

    public static void executeTests() {
        JavaClassCompiler.compileGeneratedClasses();

        Runner.getLoader().setTopPackage("generated");
        List<File> generatedTestFiles = FileFinder.getFiles("generated");
        for(File generatedFile: generatedTestFiles) {
            executeGeneratedTestClass(generatedFile.getPath().replace(".java", ".class"));
        }

        TestLogger.printLog();
        Runner.clearClassFiles();
    }

    private static void executeGeneratedTestClass(String filename) {
        Runner.getLoader().setFilePath(filename);
        String className = FileFinder.getClassName(filename, "src\\test\\java\\");
        TestLogger.setTestClassDetails(className);
        Class<?> foundClass = null;
        try {
            foundClass = Runner.getLoader().loadClass(className);
        } catch(ClassNotFoundException e) {
            TestLogger.errorEncountered("Couldn't find the test class:" + className);
            System.out.println("Couldn't find the test class:" + className);
        } catch(LinkageError e) {}

        Object instance = new Object();
        try {
            instance = foundClass.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            TestLogger.errorEncountered("Couldn't instantiate or access class: " + className);
            System.out.println("Couldn't instantiate or access class");
        }

        Method[] testMethods = foundClass.getDeclaredMethods();
        for (Method test : testMethods) {
            try {
                executeTest(instance, test);
                TestLogger.passedTest(test.getName());
            } catch(Throwable e) {
                String error = ((UnsatisfiedExpectationError)e).getErrorMessage();
                TestLogger.failedTest(test.getName(), error);
            }
        }

        TestLogger.writeToLogfile();
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

    public static void main(String[] args) {
        executeTests();
    }
}
