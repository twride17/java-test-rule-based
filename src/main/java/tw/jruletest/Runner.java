package tw.jruletest;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.analyzers.RuleExtractor;
import tw.jruletest.files.FileFinder;
import tw.jruletest.generators.TestSuiteGenerator;
import tw.jruletest.loaders.TestClassLoader;
import tw.jruletest.parse.Parser;
import tw.jruletest.translation.VariableStore;

import java.io.*;
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
    private static TestClassLoader loader;

    private static String currentMethod = "";

    private static Map<String, Map<String, String>> ruleSets = new HashMap<>();

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
        path += "\\src";

        createTestClassLoader();

        FileFinder.collectFiles(path);
        List<File> files = FileFinder.getFiles(path + "\\test\\java");
        for(File file: files) {
            runCommand("javac -cp src " + file.getPath().substring(file.getPath().indexOf("src")));
        }
        RuleExtractor.extractRules(files);

        JavaClassAnalyzer.compileSourceFiles();

        for(String className: ruleSets.keySet()) {
            Map<String, String> rules = ruleSets.get(className);
            System.out.println("Writing tests for " + className);
            for(String methodName: rules.keySet()) {
                currentMethod = methodName;
                rules.replace(methodName, Parser.parseRules(rules.get(methodName).split("\n")));
            }
            TestSuiteGenerator.writeSuiteToFile(rules, className);
            VariableStore.reset();
            currentMethod = "";
            ImportCollector.resetImports();
        }

        TestExecutor.executeTests();
    }

    public static String getCurrentMethod() {
        return currentMethod;
    }

    public static void runCommand(String command) {
        try {
            //System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            //displayOutput(command + " stdout:", process.getInputStream());
            //displayOutput(command + " stderr:", process.getErrorStream());
            process.waitFor();
            //System.out.println(command + " exitValue() " + process.exitValue());
        } catch(Exception e) {
            System.out.println("Couldn't run process: " + command);
        }
    }

    private static void displayOutput(String command, InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        while(line != null) {
            System.out.println(line);
            line = reader.readLine();
        }
    }

    public static String getPath() {
        return path;
    }

    public static void addTestClass(String className, Map<String, String> rules) {
        ruleSets.put(className, rules);
    }

    public static TestClassLoader getLoader() {
        return loader;
    }

    public static void createTestClassLoader() {
        createTestClassLoader(Runner.class.getClassLoader());
    }

    public static void createTestClassLoader(ClassLoader parent) {
        loader = new TestClassLoader(parent);
    }

    public static Map<String, Map<String, String>> getRuleSets() {
        return ruleSets;
    }
}