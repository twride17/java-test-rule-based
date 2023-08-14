package tw.jruletest.loaders;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestClassLoader extends ClassLoader {

    private String sourceDirectory = "main";
    private String topPackage;
    private String filePath;

    public TestClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void setTopPackage(String topPackage) {
        this.topPackage = topPackage;
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {
        setFilePath(System.getProperty("user.dir") + "\\src\\" + sourceDirectory + "\\java\\" + name.replaceAll("\\.", "\\\\") + ".class");
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
        if (name.startsWith(topPackage)) {
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

    public void changeDirectory() {
        if(sourceDirectory.equals("main")) {
            sourceDirectory = "test";
        } else {
            sourceDirectory = "main";
        }
    }
}
