package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaClassLoader extends the built-in ClassLoader class.
 *
 * @author Toby Wride
 * */

public class JavaClassLoader extends ClassLoader {

    private static JavaClassLoader loader = new JavaClassLoader(Runner.class.getClassLoader());
    private String rootPackage = "";
    private String filePath;

    /**
     * Constructor extends ClassLoader using a parent loader as argument.
     *
     * @param parent the ClassLoader parent
     * */

    public JavaClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Updates the current root package
     *
     * @param rootPackage the name of the new root package
     * */

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    /**
     * Updates the current file path
     *
     * @param path the new file path
     * */

    public void setFilePath(String path) {
        filePath = path;
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {
        try {
            // This loads the byte code data from the file
            byte[] b = loadClassFileData();
            // defineClass is inherited from the ClassLoader class, it converts byte array into a Class.
            Class<?> c = defineClass(name, b, 0, b.length);
            resolveClass(c);
            return c;
        } catch (IOException e) {
            System.out.println("Couldn't find: " + filePath);
            throw new ClassNotFoundException();
        }
    }

    /**
     * Overridden method from ClassLoader uses parent class loader if neither the first nor second packages match the root package.
     *
     * @param name the full name of the class
     *
     * @return the loaded class
     *
     * @throws ClassNotFoundException thrown if the class required could not be foumd
     * */

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith(rootPackage) || name.substring(name.indexOf('.')+1).startsWith(rootPackage)) {
            return getClass(name);
        }
        return super.loadClass(name);
    }

    private byte[] loadClassFileData() throws IOException {
        InputStream stream = Files.newInputStream(Paths.get(filePath));
        int size = stream.available();
        byte[] buff = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        in.readFully(buff);
        in.close();
        return buff;
    }

    /**
     * Static method to instantiate the re-instantiate the class loader
     * */

    public static void createLoader() {
        loader = new JavaClassLoader(Runner.class.getClassLoader());
    }

    /**
     * Static method to update the root package stored by the class loader
     *
     * @param packageName the name of the new root package
     * */

    public static void setLoaderRootPackage(String packageName) {
        loader.setRootPackage(packageName);
    }

    /**
     * Static method to get the loader object
     *
     * @return the loader object being used
     * */

    public static JavaClassLoader getLoader() {
        return loader;
    }

    /**
     * Loads the classes from the provided files and gets the names of all successfully loaded classes
     *
     * @param classes list of files with classes to load
     *
     * @return a list of the names of all the successfully loaded classes
     *
     * @throws CompilationFailureException thrown if the files did not compile
     * */

    public static ArrayList<String> loadClasses(List<File> classes) throws CompilationFailureException {
        ArrayList<String> classNames = new ArrayList<>();
        JavaClassCompiler.compileClasses(classes);
        for(File classFile: classes) {
            loader.setFilePath(classFile.getPath().replace(".java", ".class"));
            String className = FileFinder.getClassName(classFile);
            try {
                loader.loadClass(className);
                classNames.add(className);
                System.out.println("\n" + className + " loaded successfully");
            } catch (ClassNotFoundException e) {
                System.out.println("Could not find " + className);
            } catch (LinkageError e) {
                System.out.println("Linkage error detected for: " + className);
            }
        }
        return classNames;
    }

    /**
     * Loads the classes from both lists of provided files and gets the names of all successfully loaded classes
     *
     * @param sourceClasses list of source files with classes to load
     * @param testClasses list of test files with classes to load
     *
     * @return a list of the names of all the successfully loaded classes
     *
     * @throws CompilationFailureException thrown if the files did not compile
     * */

    public static ArrayList<String> loadClasses(List<File> sourceClasses, List<File> testClasses) throws CompilationFailureException {
        sourceClasses.addAll(testClasses);
        return loadClasses(sourceClasses);
    }

    /**
     * Loads all classes found within the specified directory and gets the names of all successfully loaded classes
     *
     * @param directory name of the directory containing the classes to be loaded
     *
     * @return a list of the names of all the successfully loaded classes
     *
     * @throws CompilationFailureException thrown if the files did not compile
     * */

    public static ArrayList<String> loadClasses(String directory) throws CompilationFailureException {
        return loadClasses(FileFinder.getFiles(directory));
    }
}
