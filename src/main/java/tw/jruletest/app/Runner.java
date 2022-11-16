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

public class Runner {

    // Run with java -jar {JAR-FILE}
    // Use framework classes to create own runner

    private static String path;

    private static ArrayList<String> ruleSets = new ArrayList<>();

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

    private static void readTestClass(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Object classInstance = cls.newInstance();

            Method[] methods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();

            if((fields.length == 1) && (methods.length > 1)) {
                readFieldAfterMethod(classInstance, fields[0], methods);
            }
            else {
                readFromMethods(classInstance, methods);
                readFields(classInstance, fields);
            }
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Couldn't find class: " + className);
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Couldn't instantiate " + className);
        }
    }

    private static void readFieldAfterMethod(Object instance, Field field, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
            readFromField(instance, field);
        }
    }

    private static void readFields(Object instance, Field[] fields) {
        for(Field field: fields) {
            readFromField(instance, field);
        }
    }

    private static void readFromMethods(Object instance, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
        }
    }

    private static void readFromMethod(Object instance, Method method) {
        String result = callMethod(instance, method);
        if(result != null) {
            ruleSets.add(result);
        }
    }

    private static void readFromField(Object instance, Field field) {
        try {
            if(field.getType().isArray()) {
                readFromArrayField(instance, field);
            } else {
                ruleSets.add((String) field.get(instance));
            }
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access field: " + field.getName());
        }
    }

    private static void readFromArrayField(Object instance, Field field) {
        try {
            Object[] set = (Object[])field.get(instance);
            String ruleSet = "";
            for(Object rule: set) {
                ruleSet += rule + " ";
            }
            ruleSets.add(ruleSet);
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access the field " + field.getName());
        }
    }

    private static String callMethod(Object instance, Method method) {
        try {
            return (String)method.invoke(instance);
        } catch(IllegalArgumentException e) {
            System.out.println("Wrong arguments for " + method.getName());
            for(Type type: method.getParameterTypes()) {
                System.out.println(type.getTypeName());
            }
        } catch (IllegalAccessException e) {
            System.out.println("Couldn't access the method");
        } catch (InvocationTargetException e) {
            System.out.println("Couldn't invoke method");
        }
        return null;
    }

    public static List<File> searchFiles(File topLevelFile, List<File> files) {
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

    public static void runCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        displayOutput(command + " stdout:", process.getInputStream());
        displayOutput(command + " stderr:", process.getErrorStream());
        process.waitFor();
        System.out.println(command + " exitValue() " + process.exitValue());
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