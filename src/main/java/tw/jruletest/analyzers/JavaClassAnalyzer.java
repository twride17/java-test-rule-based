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

    private static final String ROOT = "\\src\\main\\java\\";

    public static HashMap<String, SourceClass> sourceFiles = new HashMap<>();

    /**
     * @author Toby Wride
     *
     * Performs reflection on the source java classes to simplify the process of decoding rules
     * */

    public static void compileSourceFiles() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        String command = "javac -cp \"" + System.getProperty("java.class.path") + "\"";

        List<String> javaFolderNames = FileFinder.getDistinctDirectoryNames(ROOT);
        for(String javaFolderName: javaFolderNames) {
            command += " " + javaFolderName + "\\*.java";
        }

        Runner.runCommand(command);

        List<String> classNames = FileFinder.getClassNames(FileFinder.getFiles(System.getProperty("user.dir") + ROOT), ROOT);
        Runner.getLoader().setTopPackage("tw");
        for(String className: classNames) {
            try {
                Runner.getLoader().loadClass(className);
                sourceFiles.put(className, new SourceClass(className));
            } catch (ClassNotFoundException e) {
                System.out.println("Could not find " + className);
            } catch (LinkageError e) {
                System.out.println("Linkage error detected for: " + className);
            }
        }
    }

    public static Type getReturnType(String call) throws AmbiguousMemberException, UnidentifiedCallException {
        String className = call.split("\\.")[0];
        String memberName = call.split("\\.")[1];

        return identifySourceClass(className).findType(memberName);
    }

    public static SourceClass identifySourceClass(String cls) throws AmbiguousMemberException, UnidentifiedCallException {
        SourceClass source = null;
        for(String sourceName: sourceFiles.keySet()) {
            if(sourceName.endsWith(cls)) {
                if(source == null) {
                    source = sourceFiles.get(sourceName);
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
}
