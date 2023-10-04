package tw.jruletest.loggers;

import tw.jruletest.Runner;

import java.io.*;

/**
 * TestLogger records the results of all test suites and their associated test cases in a log file located within a default directory.
 *
 * @author Toby Wride
 * */

public class TestLogger {

    private static final String LOG_FILENAME = "logfiles\\TestResults.log";

    private static String currentLog = "";

    /**
     * Updates the class that is currently being executed
     *
     * @param className the name of the class that is currently being executed
     * */

    public static void setTestClassDetails(String className) {
        currentLog += "Executing tests in: " + className + "\n";
    }

    /**
     * Adds an entry to the log file stating that an error was encountered when executing the current class
     *
     * @param message the message to be recorded in the log file
     * */

    public static void errorEncountered(String message) {
        currentLog += message + "\n";
    }

    /**
     * Adds an entry to the log file stating that the current test has passed
     *
     * @param testName the name of the current test method
     * */

    public static void passedTest(String testName) {
        currentLog += testName + ": PASSED\n";
    }

    /**
     * Adds an entry to the log file stating that the current test has failed and for what reason
     *
     * @param testName the name of the current test method
     * @param failureMessage the reason for the test failure
     * */

    public static void failedTest(String testName, String failureMessage) {
        currentLog += "Test method " + testName + ": FAILED, caused by:\n";
        currentLog += failureMessage + "\n";
    }

    /**
     * Writes the results to the log file located within the default directory
     * */

    public static void writeToFile() {
        try {
            File logDirectory = new File(Runner.getRootPath() + "\\test\\java\\logfiles");
            if(!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            BufferedWriter logWriter = new BufferedWriter(new FileWriter(Runner.getRootPath() + "\\test\\java\\" + LOG_FILENAME, true));
            logWriter.write(currentLog + "\n");
            logWriter.close();
            currentLog = "";
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints out the current contents of the log file to the console
     * */

    public static void printLog() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Runner.getRootPath() + "\\test\\java\\" + LOG_FILENAME));
            String line;
            do {
                line = reader.readLine();
                if(line != null) {
                    System.out.println(line);
                }
            } while(line != null);
        } catch(IOException e) {
            System.out.println("Couldn't open log file.");
        }
    }
}
