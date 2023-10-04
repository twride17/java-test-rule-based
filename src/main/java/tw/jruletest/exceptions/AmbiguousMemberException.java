package tw.jruletest.exceptions;

/**
 * Exception class for if a field or method cannot be distinguished when defined multiple times
 *
 * @author Toby Wride
 * */

public class AmbiguousMemberException extends Exception {

    /**
     * Instance field for the name of the problem field or method
     * */

    private String fieldName;

    /**
     * Constructor for exception
     *
     * @param fieldName name of the problematic field
     * */

    public AmbiguousMemberException(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Prints out the error showing the field that cannot be distinguished
     * */

    public void printError() {
        System.out.println("Indistinguishable field: " + fieldName);
    }
}
