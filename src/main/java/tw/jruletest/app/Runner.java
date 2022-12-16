package tw.jruletest.app;

import tw.jruletest.analyzers.RuleExtractor;
import tw.jruletest.generators.TestSuiteGenerator;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.*;

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

    public static Map<String, Map<String, String>> ruleSets = new HashMap<>();

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
        //path += "\\src\\test\\java\\examples";
        path += "\\src\\test\\java\\tw\\jruletest\\examples";

        try {
            runCommand("javac -cp src/test/java " + path + "/*.java");
            //runCommand("java -cp src/test/java tw/jruletest/examples/TestClass1");
        } catch(Exception e){}

        List<File> classFiles = searchFiles(new File(path), new ArrayList<>());
        RuleExtractor.extractRules(classFiles.get(0));
//        RuleExtractor.extractRules(classFiles);
//
//        for(String className: ruleSets.keySet()) {
//            Map<String, String> rules = ruleSets.get(className);
//            TestSuiteGenerator.writeSuiteToFile(rules, className);
//        }
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

    public static void addTestClass(String className, Map<String, String> rules) {
        ruleSets.put(className, rules);
    }

    public static List<File> searchFiles(File topLevelFile, List<File> files) {
        File[] fileList = topLevelFile.listFiles();
        for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                files.add(file);
            }
            else if (file.isDirectory()) {
                searchFiles(file, files);
            }
        }
        return files;
    }
}