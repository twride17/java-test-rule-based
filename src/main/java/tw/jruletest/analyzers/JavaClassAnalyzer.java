package tw.jruletest.analyzers;

import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.source.SourceClass;

import java.util.HashMap;

/**
 * Class that stores instances of SourceClass for the source classes that have been loaded into the classpath.
 *
 * @author Toby Wride
 * */

public class JavaClassAnalyzer {

    /**
     * HashMap which maps the class name as a String to a SourceClass instance of the loaded class
     * */

    public static HashMap<String, SourceClass> sourceClasses = new HashMap<>();

    /**
     * Identifies the class required from the provided class name.
     *
     * @param cls name of the required class.
     * @return SourceClass instance of the class that was found
     *
     * @throws AmbiguousMemberException if there are multiple classes that match the provided class name.
     * @throws UnidentifiedCallException if no class exists with the provided class name
     * */

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

    /**
     * Adds a new entry to the map. Using the provided SourceClass's class name as the key and the SourceClass instance as the value.
     *
     * @param cls instance of SourceClass representing the loaded class.
     * */

    public static void addSourceClass(SourceClass cls) {
        sourceClasses.put(cls.getFullClassName(), cls);
    }

    /**
     * Resets the map storing the loaded classes.
     * */

    public static void resetSourceClasses() {
        sourceClasses = new HashMap<>();
    }
}
