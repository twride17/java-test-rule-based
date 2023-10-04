package tw.jruletest.analyzers;

import tw.jruletest.files.test.TestClass;

import java.util.HashMap;

/**
 * Class that stores instances of TestClass for the test classes that have been loaded into the classpath.
 *
 * @author Toby Wride
 * */

public class TestClassAnalyzer {

    /**
     * HashMap which maps the class name as a String to a TestClass instance of the loaded class
     * */

    public static HashMap<String, TestClass> testClasses = new HashMap<>();

    /**
     * Adds a new entry to the map. Using the provided TestClass's class name as the key and the TestClass instance as the value.
     *
     * @param cls instance of TestClass representing the loaded class.
     * */

    public static void addTestClass(TestClass cls) {
        testClasses.put(cls.getClassName(), cls);
    }

    /**
     * Resets the map storing the loaded classes.
     * */

    public static void resetTestClasses() {
        testClasses = new HashMap<>();
    }
}
