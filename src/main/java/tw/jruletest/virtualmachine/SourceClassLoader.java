package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.source.SourceClass;

import java.util.ArrayList;

public class SourceClassLoader {

    public static void loadClasses() throws CompilationFailureException {
        loadClasses("\\main\\");
    }

    public static void loadClasses(String requiredDirectory) throws CompilationFailureException {
        ArrayList<String> classes = JavaClassLoader.loadClasses(requiredDirectory);
        for(String cls: classes) {
            try {
                JavaClassAnalyzer.addSourceClass(new SourceClass(cls));
            } catch(ClassNotFoundException e) { }
        }
    }
}
