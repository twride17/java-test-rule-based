package main.java.tw.jruletest.app;

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Toby Wride
 *
 * Controls the flow of the system
 * Initiates parsing of test cases, compilation and execution of test suites
 */

public class TestRunner {

    // Run with java -jar {JAR-FILE}
    // Use framework classes to create own runner

    private static String path;

    public static void main(String[] args) {
        if(args.length == 0) {
            // assuming JAR in same place as source
            path = System.getProperty("user.dir");
        }
        else {
            // assuming JAR in different place as source
            System.out.println("args given");
            path = args[0];
        }
        path += "\\src";
        System.out.println(path);

        try {
            runCommand("javac -cp src " + path + "/test/java/*.java");
            runCommand("java -cp src test/java/Test");
            runCommand("java -cp src test/java/Test2");
        } catch(IOException | InterruptedException e) {
            System.out.println("Couldn't run.");
        } catch (Exception e) {
            System.out.println("Failed");
        }

        List<File> classFiles = searchFiles(new File(path), new ArrayList<>());
        for(File file: classFiles) {
            readTestClass("test.java." + file.getName().substring(0, file.getName().indexOf(".")));
        }
    }

    // Examine class:
    //   - look at fields
    //   - look at declared methods (only public):
    //     - look at return types (ie: void may mean use field)
    //   - identify if tests called sequentially or all at once (based on num of fields) ???

    private static void readTestClass(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Field[] fields = cls.getFields();

            if(fields.length == 0) {
                // No fields = returning functions
                readFromMethods(cls);
            }
            else if(fields.length == 1) {
                Class<?> type = fields[0].getType();
                if(type.isArray() && type == Class.forName("java.lang.String")) {
                    // Single String[] field = call methods then get array
                    // TODO Allow for collections
                    readFromArrayField(cls, fields[0]);
                }
                else if(type == Class.forName("java.lang.String")) {
                    // Single String field = get rule set after each method call
                    readFieldAfterEachMethodCall(cls, fields[0]);
                }
            }
            else {
                // Multiple String fields = run methods then get values for each
                readMultipleFields(cls, fields);
            }
        }
        catch (ClassNotFoundException ex) { }
    }

    private static void readMultipleFields(Class<?> cls, Field[] fields) {
        Method[] methods = cls.getMethods();
        for(Method method: methods) {
            invokeMethod(cls, method);
        }

        for(Field field: fields) {
            readFromField(cls, field);
        }
    }

    private static void readFromMethods(Class<?> cls) {
        Method[] methods = cls.getMethods();
        for(Method method: methods) {
            readFromMethod(cls, method);
        }
    }

    private static void readFromMethod(Class<?> cls, Method Method) {

    }

    private static void readFromField(Class<?> cls, Field field) {

    }

    private static void readFromArrayField(Class<?> cls, Field field) {

    }

    private static void readFieldAfterEachMethodCall(Class<?> cls, Field field) {
        Method[] methods = cls.getMethods();
        for(Method method: methods) {
            invokeMethod(cls, method);
            readFromField(cls, field);
        }
    }

    private static void invokeMethod(Class<?> currentClass, Method method) {
        System.out.println(method.getName());
        try {
            // Temporary, used for testing with main methods
            if (method.getParameterCount() == 1) {
                method.invoke(currentClass.newInstance(), new Object[]{new String[] {}});
            } else {
                method.invoke(currentClass.newInstance());
            }
        } catch(IllegalArgumentException e) {
            System.out.println("Wrong arguments for " + method.getName());
            for(Type type: method.getParameterTypes()) {
                System.out.println(type.getTypeName());
            }
        } catch (InstantiationException e) {
            System.out.println("Couldn't instantiate " + currentClass.getName());
        } catch (IllegalAccessException e) {
            System.out.println("Couldn't access the method");
        } catch (InvocationTargetException e) {
            System.out.println("Couldn't invoke method");
        }
        System.out.println("************");
    }

    private static List<File> searchFiles(File topLevelFile, List<File> files) {
        File[] fileList = topLevelFile.listFiles();
        for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".class")) {
                files.add(file);
            }
            else if (file.isDirectory()) {
                searchFiles(file, files);
            }
        }
        return files;
    }

    private static void runCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        //displayOutput(command + " stdout:", process.getInputStream());
        //displayOutput(command + " stderr:", process.getErrorStream());
        process.waitFor();
        //System.out.println(command + " exitValue() " + process.exitValue());
    }

    private static void displayOutput(String command, InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        while(line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
    }
}