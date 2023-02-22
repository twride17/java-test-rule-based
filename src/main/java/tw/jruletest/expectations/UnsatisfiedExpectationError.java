package tw.jruletest.expectations;

public class UnsatisfiedExpectationError extends AssertionError {

    private String errorMessage = "";

    public UnsatisfiedExpectationError(String message) {
        errorMessage = message;
    }

    public void explainError() {
        System.out.println("Error: " + getErrorMessage());
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
