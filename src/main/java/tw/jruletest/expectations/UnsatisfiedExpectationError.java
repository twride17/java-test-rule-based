package tw.jruletest.expectations;

/**
 * Exception to be thrown when an expectation has not been satisfied
 *
 * @author Toby Wride
 * */

public class UnsatisfiedExpectationError extends AssertionError {

    /**
     * Instance field for the error message to be shown
     * */

    private String errorMessage = "";

    /**
     * Exception constructor
     *
     * @param message the message to show the user when a test fails
     * */

    public UnsatisfiedExpectationError(String message) {
        errorMessage = message;
    }

    /**
     * Returns the exception's stored error message
     *
     * @return the message to be shown when a test fails
     * */

    public String getErrorMessage() {
        return errorMessage;
    }
}
