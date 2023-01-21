package tw.jruletest.compilers;

import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;

import java.util.ArrayList;

public class ClassCompiler {

    public static void compileClasses(ArrayList<String> packages) {
        String command = "javac -cp " + System.getProperty("java.class.path");
        for(String packageName: packages) {
            command += " " + packageName + "\\*.java";
        }
        Runner.runCommand(command);
    }

    public static void compileJavaClasses() {
        compileClasses(new ArrayList<>(FileFinder.getDistinctDirectoryNames("src\\main\\java")));
    }

    public static void compileGeneratedClasses() {
        ArrayList<String> files = new ArrayList<>(FileFinder.getDistinctDirectoryNames("src\\main\\java\\"));
        files.addAll(FileFinder.getDistinctDirectoryNames("src\\test\\java\\generated\\"));
        compileClasses(files);
    }
}
