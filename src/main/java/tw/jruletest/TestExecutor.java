package tw.jruletest;

import tw.jruletest.expectations.UnsatisfiedExpectationError;
import tw.jruletest.files.FileFinder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestExecutor {

    public static void executeTests() {
        FileFinder.collectFiles(Runner.getPath());
        String command = "javac -cp " + System.getProperty("java.class.path");

        List<String> javaFileNames = FileFinder.getDistinctDirectoryNames("src\\main\\java");
        for(String javaFileName: javaFileNames) {
            command += " " + javaFileName + "\\*.java";
        }

        List<String> testFileNames = FileFinder.getDistinctDirectoryNames("src\\test\\java\\generated\\");
        for(String testFileName: testFileNames) {
            command += " " + testFileName + "\\*.java";
        }

        Runner.runCommand(command);

        Runner.getLoader().setTopPackage("generated");
        List<File> generatedTestFiles = FileFinder.getFiles("generated");
        for(File generatedFile: generatedTestFiles) {
            executeGeneratedTestClass(generatedFile.getPath().replace(".java", ".class"));
        }
    }

    private static void executeGeneratedTestClass(String filename) {
        Runner.getLoader().setFilePath(filename);
        String className = FileFinder.getClassName(filename, "src\\test\\java\\");
        System.out.println("Executing tests in " + className);
        Class<?> foundClass = null;
        try {
            foundClass = Runner.getLoader().loadClass(className);
        } catch(ClassNotFoundException e) {
            System.out.println("Couldn't find the test class:" + className);
        } catch(LinkageError e) {}

        Object instance = new Object();
        try {
            instance = foundClass.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            System.out.println("Couldn't instantiate or access class");
        }

        Method[] testMethods = foundClass.getDeclaredMethods();
        for (Method test : testMethods) {
            try {
                executeTest(instance, test);
            } catch(Throwable e) {
                ((UnsatisfiedExpectationError)e).explainError();
            }
        }

        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("Couldn't delete file");
        }
    }

    // TODO Proper logging of test passes and failures
    private static void executeTest(Object instance, Method testMethod) throws Throwable {
        try {
            testMethod.invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnsatisfiedExpectationError) {
                throw cause;
            } else {
                System.out.println("Failed to call method: " + testMethod.getName());
            }
        }
    }

    public static void main(String[] args) {
        executeTests();
    }
}
