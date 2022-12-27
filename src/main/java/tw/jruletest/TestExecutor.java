package tw.jruletest;

import tw.jruletest.expectations.UnsatisfiedExpectationError;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestExecutor {

    public static void executeTests() {
        Runner.runCommand("javac -cp " + System.getProperty("java.class.path") +
                " src/main/java/app/Example.java src/test/java/generated/app/ExampleClass.java");

        try {
            Runner.getLoader().loadClass("app.Example");
        }
        catch (LinkageError e) {}
        catch(ClassNotFoundException e) {
            System.out.println("Could not find required class.");
        }
        Runner.getLoader().setTopPackage("generated");
        Runner.getLoader().setFilePath(System.getProperty("user.dir") + "\\src\\test\\java\\generated\\app\\ExampleClass.class");

        Class<?> foundClass = null;
        try {
            foundClass = Runner.getLoader().loadClass("generated.app.ExampleClass");
        } catch(ClassNotFoundException e) {
            System.out.println("Couldn't find the test class.");
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
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "\\src\\test\\java\\generated\\app\\ExampleClass.class"));
        } catch (IOException e) {
            System.out.println("Couldn't delete file");
        }
    }

    private static void executeTest(Object instance, Method testMethod) throws Throwable {
        try {
            testMethod.invoke(instance);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if(cause instanceof UnsatisfiedExpectationError) {
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
