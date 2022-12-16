package tw.jruletest.loaders;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestClassLoader extends ClassLoader {

    public TestClassLoader(ClassLoader parent) {
        super(parent);
    }

    private Class<?> getClass(String name) throws ClassNotFoundException {
        String file = name.replace('.', File.separatorChar) + ".class";
        try {
            // This loads the byte code data from the file
            byte[] b = loadClassFileData(file);
            // defineClass is inherited from the ClassLoader class, it converts byte array into a Class.
            Class<?> c = defineClass(name, b, 0, b.length);
            resolveClass(c);
            return c;
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("tw")) {
            return getClass(name);
        }
        return super.loadClass(name);
    }

    private byte[] loadClassFileData(String name) throws IOException {
        InputStream stream = Files.newInputStream(Paths.get("src/test/java/" + name));
        int size = stream.available();
        byte buff[] = new byte[size];
        DataInputStream in = new DataInputStream(stream);
        in.readFully(buff);
        in.close();
        return buff;
    }
}
