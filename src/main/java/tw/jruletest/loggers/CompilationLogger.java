package tw.jruletest.loggers;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.CompilationFailureException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * CompilationLogger stores the errors that have resulted from the compilation of Java files./
 * All errors are written to a log file located within a default directory
 *
 * @author Toby Wride
 * */

public class CompilationLogger {

    private static final String LOG_FILENAME = "logfiles\\Compilation.log";

    private static HashMap<String, String> compilationResults = new HashMap<>();

    /**
     * Adds a new compilation result to the map using the class name as the key and the result as the value.
     *
     * @param className the name of the class that failed to compile
     * @param result the reasons for the compilation failure
     * */

    public static void addResult(String className, String result) {
        compilationResults.put(className, result);
    }

    /**
     * Writes the results to the log file
     *
     * @throws CompilationFailureException thrown when at least one class has failed to compile. This prevents propagation of errors and reduces confusion when debugging rule extraction and validation problems.
     * */

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

    /**
     * Gets the total number of classes that failed compilation
     *
     * @return number of classes that failed to compile
     * */

    public static int getNumberFailedClassCompilations() {
        return compilationResults.keySet().size();
    }

    /**
     * Resets the map so that errors found in earlier compilations do not cause unexpected failures in later compilation attempts.
     * */

    public static void reset() {
        compilationResults = new HashMap<>();
    }
}
