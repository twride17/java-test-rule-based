package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;

public class JavaClassAnalyzer {

    public static HashMap<String, SourceClass> sourceClasses = new HashMap<>();

    public static SourceClass identifySourceClass(String cls) throws AmbiguousMemberException, UnidentifiedCallException {
        SourceClass source = null;
        for(String sourceName: sourceClasses.keySet()) {
            String className = "";
            if(sourceName.indexOf('.') == -1) {
                className = sourceName;
            } else {
                className = sourceName.substring(sourceName.lastIndexOf('.') + 1);
            }

            if(className.equals(cls)) {
                if(source == null) {
                    source = sourceClasses.get(sourceName);
                } else {
                    throw new AmbiguousMemberException(cls);
                }
            }
        }

        if(source == null) {
            throw new UnidentifiedCallException(cls);
        } else {
            return source;
        }
    }

    public static void addSourceClass(SourceClass cls) {
        sourceClasses.put(cls.getFullClassName(), cls);
    }

    public static void resetSourceClasses() {
        sourceClasses = new HashMap<>();
    }
}
