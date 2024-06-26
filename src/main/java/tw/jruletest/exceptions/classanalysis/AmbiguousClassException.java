package tw.jruletest.exceptions.classanalysis;

/**
 * Exception class for if a field or method cannot be distinguished when defined multiple times
 *
 * @author Toby Wride
 * */

public class AmbiguousClassException extends ClassAnalysisException {

    /**
     * Instance field for the name of the problem field or method
     * */

    private String fieldName;

    /**
     * Constructor for exception
     *
     * @param fieldName name of the problematic field
     * */

    public AmbiguousClassException(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Prints out the error showing the field that cannot be distinguished
     * */

    public String getErrorMessage() {
        return "Indistinguishable field: " + fieldName;
    }
}
