package tw.jruletest.analyzers;

import tw.jruletest.files.test.TestClass;

import java.util.HashMap;

public class TestClassAnalyzer {

    public static HashMap<String, TestClass> testClasses = new HashMap<>();

    public static void addTestClass(TestClass cls) {
        testClasses.put(cls.getFullClassName(), cls);
    }

    public static void resetTestClasses() {
        testClasses = new HashMap<>();
    }
}
