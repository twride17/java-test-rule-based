package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaClassLoader extends ClassLoader {

    private static JavaClassLoader loader = new JavaClassLoader(Runner.class.getClassLoader());

    private String currentDirectory = "\\main\\java\\";
    private String rootPackage = "";
    private String filePath;

    public JavaClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

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

    public void changeDirectory(String directory) {
        currentDirectory = directory;
    }

    // Static methods
    public static void createLoader() {
        loader = new JavaClassLoader(Runner.class.getClassLoader());
    }

    public static void setLoaderRootPackage(String packageName) {
        loader.setRootPackage(packageName);
    }

    public static void changeLoaderDirectory(String directory) {
        loader.changeDirectory(directory);
    }

    public static JavaClassLoader getLoader() {
        return loader;
    }

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

    public static ArrayList<String> loadClasses(List<File> sourceClasses, List<File> testClasses) throws CompilationFailureException {
        sourceClasses.addAll(testClasses);
        return loadClasses(sourceClasses);
    }

    public static ArrayList<String> loadClasses(String directory) throws CompilationFailureException {
        System.out.println(directory);
        return loadClasses(FileFinder.getFiles(directory));
    }
}
