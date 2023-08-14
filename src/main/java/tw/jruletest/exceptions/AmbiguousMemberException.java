package tw.jruletest.exceptions;

public class AmbiguousMemberException extends Exception {

    private String fieldName;

    public AmbiguousMemberException(String fieldName) {
        this.fieldName = fieldName;
    }

    public void printError() {
        System.out.println("Unknown field: " + fieldName);
    }
}
