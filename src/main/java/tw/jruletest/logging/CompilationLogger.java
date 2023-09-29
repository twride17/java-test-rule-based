package tw.jruletest.logging;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CompilationLogger {

    private static final String LOG_FILENAME = "logfiles\\Compilation.log";

    private static HashMap<String, String> compilationResults = new HashMap<>();

    public static void addResult(String className, String result) {
        compilationResults.put(className, result);
    }

    public static void writeToFile() throws CompilationFailureException {
        try {
            File logDirectory = new File(Runner.getRootPath() + "\\test\\java\\logfiles");
            if(!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            BufferedWriter logWriter =  new BufferedWriter(new FileWriter(Runner.getRootPath() + "\\test\\java\\" + LOG_FILENAME));
            String currentLog = "";

            if(compilationResults.keySet().size() == 0) {
                logWriter.write("No compilation failures detected");
                logWriter.close();
            } else {
                for(String className: compilationResults.keySet()) {
                    currentLog += "Compilation of " + className + ":\n" + compilationResults.get(className) + "\n";
                }
                logWriter.write(currentLog);
                logWriter.close();
                throw new CompilationFailureException();
            }
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't open log file");
        }
    }

    public static int getNumberFailedClassCompilations() {
        return compilationResults.keySet().size();
    }

    public static void reset() {
        compilationResults = new HashMap<>();
    }
}
