package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.files.FileFinder;

import java.util.ArrayList;
import java.util.List;

public class JavaClassCompiler {

    public static void compileClasses(List<String> packages) {
        String command = "javac -cp \"" + System.getProperty("java.class.path") + "\"";
        for(String packageName: packages) {
            command += " " + packageName + "\\*.java";
        }
        Runner.runCommand(command);
    }

    public static void compileClasses(String directory) {
        compileClasses(FileFinder.getDistinctDirectoryNames(directory));
    }
}
