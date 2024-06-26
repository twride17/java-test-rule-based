package tw.jruletest.exceptions.classanalysis;

/**
 * Exception to be thrown when a specific class's member cannot be found
 *
 * @author Toby Wride
 * */

public class UnidentifiedCallException extends ClassAnalysisException {

    /**
     * Instance field for the name of the member that could not be idnetified
     * */

    private String call;

    /**
     * Exception constructor
     *
     * @param call the name of the field or method that could not be identified
     * */

    public UnidentifiedCallException(String call) {
        this.call = call;
    }

    /**
     * Returns an error message stating that the required field or method does not exist
     *
     * @return a message stating the member does not exist
     * */

    public String getErrorMessage() {
        return "No such field or method: " + call;
    }

    /**
     * Prints out an error message stating that the field or method is not known
     * */

    public void printError() {
        System.out.println("Unknown member: " + call);
    }
}
