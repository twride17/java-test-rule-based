package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.analyzers.TestClassAnalyzer;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.files.test.TestClass;

import java.util.ArrayList;

public class TestClassLoader {

    public static void loadClasses() {
        loadClasses("\\test\\");
    }

    public static void loadClasses(String requiredDirectory) {
        ArrayList<String> classes = JavaClassLoader.loadClasses(requiredDirectory);
        for(String cls: classes) {
            try {
                TestClassAnalyzer.addTestClass(new TestClass(cls));
            } catch(ClassNotFoundException e) {
                System.out.println("Failed to find: " + cls);
            }
        }
    }

    public static void loadGeneratedTestClasses() {
        System.out.println("Loading generated");
        JavaClassLoader.loadClasses(FileFinder.getFiles("\\main\\"), FileFinder.getFiles("\\generated\\"));
    }
}
