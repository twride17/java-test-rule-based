package main.java.tw.jruletest.app;

import java.io.*;
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
        System.out.println(path);

        try {
            runCommand("javac -cp src " + path + "/src/test/java/Test.java");
            runCommand("java -cp src test/java/Test");
        } catch(IOException | InterruptedException e) {
            System.out.println("Couldn't run.");
        } catch (Exception e) {
            System.out.println("Failed");
        }
    }

    private static List<File> searchFiles(File topLevelFile, List<File> files) {
        File[] fileList = topLevelFile.listFiles();
        for (File file : fileList) {
            if (file.isFile() /*&& file.getName().endsWith(".class")*/) {
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
        displayOutput(command + " stdout:", process.getInputStream());
        displayOutput(command + " stderr:", process.getErrorStream());
        process.waitFor();
        System.out.println(command + " exitValue() " + process.exitValue());
        System.out.println("*********************************");
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