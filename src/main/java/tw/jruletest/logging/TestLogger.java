package tw.jruletest.logging;

import tw.jruletest.Runner;

import java.io.*;

public class TestLogger {

    private static final String LOG_FILENAME = "generated\\TestResults.log";

    private static String currentLog = "";

    public static void setTestClassDetails(String classname) {
        currentLog += "Executing tests in: " + classname + "\n";
    }

    public static void errorEncountered(String message) {
        currentLog += message + "\n";
    }

    public static void passedTest(String testName) {
        currentLog += testName + ": PASSED\n";
    }

    public static void failedTest(String testName, String failureMessage) {
        currentLog += "Test method " + testName + ": FAILED, caused by:\n";
        currentLog += failureMessage + "\n";
    }

    public static void writeToLogfile() {
        try {
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(Runner.getRootPath() + "\\test\\java\\" + LOG_FILENAME, true));
            logWriter.write(currentLog + "\n");
            logWriter.close();
            currentLog = "";
        } catch(IOException e) {}
    }

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
