package tw.jruletest;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.RuleExtractor;
import tw.jruletest.analyzers.TestClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.generators.TestSuiteGenerator;
import tw.jruletest.logging.CompilationLogger;
import tw.jruletest.parse.Parser;
import tw.jruletest.variables.VariableStore;
import tw.jruletest.virtualmachine.JavaClassCompiler;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.SourceClassLoader;
import tw.jruletest.virtualmachine.TestClassLoader;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static String currentMethod = "";

    private static Map<String, Map<String, String>> ruleSets = new HashMap<>();

    public static void main(String[] args) {
        // TODO Separate main function logic into separate classes/methods
        if(args.length == 0) {
            // assuming JAR in same place as source
            path = System.getProperty("user.dir");
        }
        else {
            // assuming JAR in different place as source
            path = args[0];
        }
        path += "\\src";

        removeExistingGeneratedTests();
        removeExistingLogFiles();

        FileFinder.collectFiles(path);

        try {
            String firstClass = FileFinder.getClassNames(FileFinder.getFiles(path + "\\main\\java")).get(0);
            JavaClassLoader.setLoaderRootPackage(firstClass.substring(0, firstClass.indexOf('.')));
            TestClassLoader.loadClasses();
            RuleExtractor.extractRules();

            SourceClassLoader.loadClasses();
            for (String className : ruleSets.keySet()) {
                Map<String, String> rules = ruleSets.get(className);
                for (String methodName : rules.keySet()) {
                    currentMethod = methodName;
                    rules.replace(methodName, Parser.parseRules(rules.get(methodName).split("\n")));
                }
                TestSuiteGenerator.writeSuiteToFile(rules, className);
                VariableStore.reset();
                currentMethod = "";
                ImportCollector.resetImports();
            }
            TestClassAnalyzer.resetTestClasses();

            TestExecutor.executeTests();
        } catch(CompilationFailureException e) {
            JOptionPane.showMessageDialog(null, e.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            CompilationLogger.reset();
            clearClassFiles();
            removeExistingGeneratedTests();
            System.exit(0);
        }
    }

    public static String getCurrentMethod() {
        return currentMethod;
    }

    public static void runCommand(String command) throws CompilationFailureException {
        try {
            Process process = Runtime.getRuntime().exec(command);
            recordCompilationOutput(process.getErrorStream());
            process.waitFor();
            CompilationLogger.writeToFile();
        } catch (IOException | InterruptedException e) {
            System.out.println("Couldn't run process:" + command);
            e.printStackTrace();
        }
    }

    private static void recordCompilationOutput(InputStream input) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String currentClass = "";
        String output = "";

        try {
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                Matcher matcher = Pattern.compile("^(((.+)\\\\)*([A-za-z0-9]+)(\\.java))").matcher(line);
                if(matcher.find()) {
                    if(!(currentClass.isEmpty() && output.isEmpty())) {
                        CompilationLogger.addResult(currentClass, output);
                    }
                    currentClass = FileFinder.getClassName(matcher.group());
                    String remainingLine = line.substring(matcher.end());
                    output = remainingLine.substring(remainingLine.indexOf(' ') + 1) + "\n";
                } else {
                    output += line + "\n";
                }
                line = reader.readLine();
            }
        } catch(IOException e) {
            currentClass = "Compilation Error Stream";
            output = "Couldn't open error stream.";
        }

        if(!(currentClass.isEmpty() && output.isEmpty())) {
            CompilationLogger.addResult(currentClass, output);
        }
    }

    public static void clearClassFiles() {
        List<File> files = FileFinder.getFiles("src");
        for(File file: files) {
            deleteFile(file.getPath().replace(".java", ".class"));
        }
    }

    private static void removeExistingLogFiles() {
        deleteDirectory(new File(path + "\\test\\java\\logfiles"));
    }

    private static void removeExistingGeneratedTests() {
        deleteDirectory(new File(path + "\\test\\java\\generated"));
    }

    private static void deleteDirectory(File root) {
        File[] rootContents = root.listFiles();
        if (rootContents != null) {
            for (File file : rootContents) {
                deleteDirectory(file);
            }
        }
        root.delete();
    }

    private static void deleteFile(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {}
    }

    public static void addTestClass(String className, Map<String, String> rules) {
        ruleSets.put(className, rules);
    }

    public static Map<String, Map<String, String>> getRuleSets() {
        return ruleSets;
    }

    public static String getRootPath() {
        return path;
    }

    public static void setRootPath(String newPath) {
        path = newPath;
    }
}