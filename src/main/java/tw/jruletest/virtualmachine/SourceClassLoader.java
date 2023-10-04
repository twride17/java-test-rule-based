package tw.jruletest.virtualmachine;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.source.SourceClass;

import java.util.ArrayList;

/**
 * Utility class designed to load specifically source classes
 *
 * @author Toby Wride
 * */

public class SourceClassLoader {

    /**
     * Loads all source classes using the 'main' directory as the default root folder.
     *
     * @throws CompilationFailureException thrown if the source classes failed to compile
     * */

    public static void loadClasses() throws CompilationFailureException {
        loadClasses("\\main\\");
    }

    /**
     * Loads all source classes from a specified root directory
     *
     * @param requiredDirectory the directory from which to compile the source classes
     *
     * @throws CompilationFailureException thrown if the source classes failed to compile
     * */

    public static void loadClasses(String requiredDirectory) throws CompilationFailureException {
        ArrayList<String> classes = JavaClassLoader.loadClasses(requiredDirectory);
        for(String cls: classes) {
            try {
                JavaClassAnalyzer.addSourceClass(new SourceClass(cls));
            } catch(ClassNotFoundException e) { }
        }
    }
}
