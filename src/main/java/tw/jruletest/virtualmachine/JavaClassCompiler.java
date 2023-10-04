package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;

import java.io.File;
import java.util.List;

/**
 * Provides methods with different types and combinations of parameters for compiling classes.
 *
 * @author Toby Wride
 * */

public class JavaClassCompiler {

    /**
     * Constant field storing the start of the java compilation command.
     * */

    public static final String COMMAND_START = "javac -cp \"" + System.getProperty("java.class.path") + "\"";

    /**
     * Uses a list of packages to construct the required Jav compilation command.
     * Compilation uses the wildcard (*.java) to allow for mass compilation.
     *
     * @param packages the list of names for the packages to be compiled.
     *
     * @throws CompilationFailureException thrown if the compilation failed
     * */

    public static void compilePackages(List<String> packages) throws CompilationFailureException {
        String command = COMMAND_START;
        for(String packageName: packages) {
            command += " " + packageName + "\\*.java";
        }
        Runner.runCommand(command);
    }

    /**
     * Uses a list of files to construct the required Jav compilation command.
     *
     * @param files the list of files to be compiled.
     *
     * @throws CompilationFailureException thrown if the compilation failed
     * */

    public static void compileClasses(List<File> files) throws CompilationFailureException {
        compilePackages(FileFinder.getDistinctPackageNames(files));
    }

    /**
     * Collects all Java files from a specified directory to be compiled.
     *
     * @param directory the name of the directory from which to get the files.
     *
     * @throws CompilationFailureException thrown if the compilation failed
     * */

    public static void compileClasses(String directory) throws CompilationFailureException {
        compileClasses(FileFinder.getFiles(directory));
    }

    /**
     * Compiles all classes from the entire project. Uses the 'src' directory as the default root folder to find the files from.
     *
     * @throws CompilationFailureException thrown if the compilation failed
     * */

    public static void compileClasses() throws CompilationFailureException {
        compileClasses("src");
    }
}
