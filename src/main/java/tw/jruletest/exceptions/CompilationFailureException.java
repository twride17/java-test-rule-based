package tw.jruletest.exceptions;

import tw.jruletest.loggers.CompilationLogger;

 /**
  * Exception for when compilation of source, test or generated test Java files has failed
  *
  * @author Toby Wride
  * */

public class CompilationFailureException extends Exception {

    /**
     * Returns the error message to show to user. Message content includes the number of classes that failed compilation and where to find the appropriate log file.
     *
     * @return String value of the error to be displayed
     * */

    public String getError() {
        return "Compilation failed!\n" + CompilationLogger.getNumberFailedClassCompilations() + " classes failed to compile.\n"
                + "See Compilation.log file, located in logfiles directory, for details";
    }
}
