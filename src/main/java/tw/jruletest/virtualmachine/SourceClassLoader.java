package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.files.source.SourceClass;

import java.util.ArrayList;

public class SourceClassLoader {

    public static void loadClasses() {
        loadClasses("\\main\\");
    }

    public static void loadClasses(String requiredDirectory) {
        ArrayList<String> classes = JavaClassLoader.loadClasses(requiredDirectory);
        for(String cls: classes) {
            try {
                JavaClassAnalyzer.addSourceClass(new SourceClass(cls));
            } catch(ClassNotFoundException e) { }
        }
    }
}
