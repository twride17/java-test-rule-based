package tw.jruletest.exceptions.classanalysis;

/**
 * Exception to be thrown when a specific class's member cannot be found
 *
 * @author Toby Wride
 * */

public class UnknownClassException extends ClassAnalysisException {

    /**
     * Instance field for the name of the member that could not be idnetified
     */

    private String cls;

    /**
     * Exception constructor
     *
     * @param cls the name of the field or method that could not be identified
     */

    public UnknownClassException(String cls) {
        this.cls = cls;
    }

    /**
     * Returns an error message stating that the required field or method does not exist
     *
     * @return a message stating the member does not exist
     */

    public String getUnidentifiedCall() {
        return "No such class exists: " + cls;
    }

    /**
     * Returns an error message stating that the field or method is not known
     */

    public String getErrorMessage() {
        return "Unknown class: " + cls;
    }
}
