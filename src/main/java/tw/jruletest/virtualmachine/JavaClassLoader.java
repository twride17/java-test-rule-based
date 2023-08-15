package tw.jruletest.virtualmachine;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JavaClassLoader extends ClassLoader {

    private static JavaClassLoader loader;

    private String currentDirectory = "\\main\\java\\";
    private String rootPackage = "";
    private String filePath;

    public JavaClassLoader(ClassLoader parent) {
        super(parent);
    }

    private void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {
        setFilePath(Runner.getRootPath() + currentDirectory + name.replaceAll("\\.", "\\\\") + ".class");
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
        if (name.startsWith(rootPackage)) {
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

    public static void loadSourceClasses() {
        changeLoaderDirectory("\\main\\java\\");
        loadClasses();
    }

    public static void loadTestClasses() {
        changeLoaderDirectory("\\test\\java\\");
        loadClasses();
    }

    public static void loadGeneratedTestClasses() {
        changeLoaderDirectory("\\test\\java\\generated\\");
        loadClasses();
    }

    public static void loadClasses(List<String> classes) {
        JavaClassCompiler.compileClasses("src" + loader.currentDirectory);
        for(String className: classes) {
            try {
                loader.loadClass(className);
                JavaClassAnalyzer.addSourceClass(new SourceClass(className));
            } catch (ClassNotFoundException e) {
                System.out.println("Could not find " + className);
            } catch (LinkageError e) {
                System.out.println("Linkage error detected for: " + className);
            }
        }
    }

    public static void loadClasses() {
        loadClasses(FileFinder.getClassNames(FileFinder.getFiles(Runner.getRootPath() + loader.currentDirectory), loader.currentDirectory));
    }
}
