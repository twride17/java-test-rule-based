package tw.jruletest.exceptions;

public class UnidentifiedCallException extends Exception {

    private String call;

    public UnidentifiedCallException(String call) {
        this.call = call;
    }

    public String getUnidentifiedCall() {
        return "No such field or method: " + call;
    }
}
