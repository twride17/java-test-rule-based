package tw.jruletest.logging;

import tw.jruletest.Runner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
        currentLog += testName + ": FAILED\n";
        currentLog += failureMessage + "\n";
    }

    public static void writeToLogfile() {
        try {
            BufferedWriter logWriter = new BufferedWriter(new FileWriter(Runner.getPath() + "\\test\\java\\" + LOG_FILENAME, true));
            logWriter.write(currentLog + "\n");
            logWriter.close();
            currentLog = "";
        } catch(IOException e) {}
    }
}
