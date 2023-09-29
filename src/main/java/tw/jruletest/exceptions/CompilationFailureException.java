package tw.jruletest.exceptions;

import tw.jruletest.logging.CompilationLogger;

public class CompilationFailureException extends Exception {

    public String getError() {
        return "Compilation failed!\n" + CompilationLogger.getNumberFailedClassCompilations() + " classes failed to compile.\n"
                + "See Compilation.log file, located in logfiles directory, for details";
    }
}
