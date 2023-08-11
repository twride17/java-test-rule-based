package tw.jruletest.exceptions;

public class UnknownFieldException extends Exception {

    private String fieldName;

    public UnknownFieldException(String fieldName) {
        this.fieldName = fieldName;
    }

    public void printError() {
        System.out.println("Unknown field: " + fieldName);
    }
}
