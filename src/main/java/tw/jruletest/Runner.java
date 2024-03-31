package tw.jruletest;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.RuleExtractor;
import tw.jruletest.analyzers.TestClassAnalyzer;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.parsing.ParserFailureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.generators.TestSuiteGenerator;
import tw.jruletest.loggers.CompilationLogger;
import tw.jruletest.parse.Parser;
import tw.jruletest.variables.VariableStore;
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
 * Runner acts as the main controller of the testing framework. From here, the test classes are analyzed and new test suites are generated.
 * The file structure is also cleaned on each run through by removing any existing generated tests and removing all .class files.
 *
 * @author Toby Wride
 */

public class Runner {

    private static String path;

    private static String currentMethod = "";

    private static Map<String, Map<String, String>> ruleSets = new HashMap<>();

    /**
     * Main entrypoint into the system. Rules are extracted, code is generated and tests are created using this method as well as calling the TestExecutor to execute the tests.
     * Clean up of generated tests and .class files is carried out from this method.
     *
     * @param args {@code <path to root folder>}: the path to the folder containing the 'src' folder. Using no arguments
     *             assumes JAR is placed in same folder as the 'src' directory.
     */

    public static void main(String[] args) {
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
                    try {
                        rules.replace(methodName, Parser.parseRules(rules.get(methodName).split("\n")));
                    } catch(ParserFailureException e) {
                        rules.replace(methodName, "Expectations.failed(\"Parser failed to parse rules:\n" + e.getErrorMessage() + "\")");
                    }
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

    /**
     * Gets the method from which rules are currently being extracted
     *
     * @return the name of the method
     * */

    public static String getCurrentMethod() {
        return currentMethod;
    }

    /**
     * Executes the required command (usually javac commands)
     *
     * @param command the full command to execute
     *
     * @throws CompilationFailureException thrown if the command failed
     * */

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

    /**
     * Removes all .class files from the project structure
     * */

    public static void clearClassFiles() {
        List<File> files = FileFinder.getFiles("src");
        for(File file: files) {
            deleteFile(file.getPath().replace(".java", ".class"));
        }
    }

    /**
     * Removes all .log files from the project structure
     * */

    public static void removeExistingLogFiles() {
        deleteDirectory(new File(path + "\\test\\java\\logfiles"));
    }

    /**
     * Removes all generated test files from the project structure
     * */

    public static void removeExistingGeneratedTests() {
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

    /**
     * Deletes the file indicated by the provided file name
     *
     * @param filename the name of the file to delete
     * */

    public static void deleteFile(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException e) {}
    }

    /**
     * Adds a new test class and its extracted rules to the stored set of rules
     *
     * @param className the name of the class from which the rules were extarcted
     * @param rules a map for the methods in the class and the rules extracted from them
     * */

    public static void addTestClass(String className, Map<String, String> rules) {
        ruleSets.put(className, rules);
    }

    /**
     * Gets all the rules for all the classes
     *
     * @return a map of all the rules extracted from all the classes
     * */

    public static Map<String, Map<String, String>> getRuleSets() {
        return ruleSets;
    }

    /**
     * Gets the path to the root of the project
     *
     * @return the current root path
     * */

    public static String getRootPath() {
        return path;
    }

    /**
     * Updates the path to the root og the project
     *
     * @param newPath the new root path
     * */

    public static void setRootPath(String newPath) {
        path = newPath;
    }
}