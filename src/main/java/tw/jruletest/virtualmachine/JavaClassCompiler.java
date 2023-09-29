package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.logging.CompilationLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaClassCompiler {

    private static final String COMMAND_START = "javac -cp \"" + System.getProperty("java.class.path") + "\" ";

    public static void compilePackages(List<String> packages) throws CompilationFailureException {
        String command = "javac -cp \"" + System.getProperty("java.class.path") + "\"";
        for(String packageName: packages) {
            command += " " + packageName + "\\*.java";
        }
        Runner.runCommand(command);
    }

    public static void compileClasses(List<File> files) throws CompilationFailureException {
        compilePackages(FileFinder.getDistinctPackageNames(files));
    }

    /* Possibly library methods */
    public static void compileClasses(String directory) throws CompilationFailureException {
        compileClasses(FileFinder.getFiles(directory));
    }

    public static void compileClasses() throws CompilationFailureException {
        compileClasses("src");
    }
}
