package tw.jruletest.expectations;

public class UnsatisfiedExpectationError extends AssertionError {

    String errorMessage = "";

    public UnsatisfiedExpectationError(String message) {
        errorMessage = message;
    }

    public void explainError() {
        System.out.println("Error: " + errorMessage);
    }
}
