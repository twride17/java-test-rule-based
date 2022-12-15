package main.java.tw.jruletest.app;

import main.java.tw.jruletest.analyzers.TestClassAnalyzer;
import main.java.tw.jruletest.generators.TestSuiteGenerator;
import main.java.tw.jruletest.parse.Parser;

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
        path += "\\src\\test\\java\\examples";
        // System.out.println(path);

        List<File> classFiles = searchFiles(new File(path), new ArrayList<>());
        TestClassAnalyzer.extractRules(classFiles);

        int x = 1;
        for(String ruleSet: ruleSets) {
            List<String> codeBlocks = new ArrayList<>();
            String code = "";
            for(String rule: ruleSet.split("\n")) {
                System.out.println(rule);
                code += Parser.parseRule(rule);
            }
            System.out.println();
            codeBlocks.add(code);
            TestSuiteGenerator.writeSuiteToFile(codeBlocks, "Test" + x + ".txt");
            x++;
        }
    }

    public static void addRuleSet(String set) {
        ruleSets.add(set);
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

    public static ArrayList<String> getRuleSets() {
        return ruleSets;
    }
}