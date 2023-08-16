package tw.jruletest;

import tw.jruletest.expectations.UnsatisfiedExpectationError;
import tw.jruletest.files.FileFinder;
import tw.jruletest.logging.TestLogger;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TestExecutor {

    public static void executeTests() {
        JavaClassLoader.loadGeneratedTestClasses();

        List<File> generatedTestFiles = FileFinder.getFiles("generated");
        for(File generatedFile: generatedTestFiles) {
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

        TestLogger.writeToLogfile();
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

    public static void main(String[] args) {
        FileFinder.collectFiles(Runner.getRootPath());

        JavaClassLoader.createLoader();

        String firstClass = FileFinder.getClassNames(FileFinder.getFiles(Runner.getRootPath() + "\\main\\java")).get(0);
        JavaClassLoader.setLoaderRootPackage(firstClass.substring(0, firstClass.indexOf('.')));

        JavaClassLoader.loadSourceClasses();
        JavaClassLoader.loadGeneratedTestClasses();
    }
}
