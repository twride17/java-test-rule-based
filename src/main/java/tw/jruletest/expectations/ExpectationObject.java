package tw.jruletest.expectations;

public class ExpectationObject<T> {

    private T expectedValue;

    public ExpectationObject(T expected) {
        expectedValue = expected;
    }

    public void toEqual(T actualValue) throws UnsatisfiedExpectationError {
        if(!expectedValue.equals(actualValue)) {
            toFail("Unsatisfied Expectation: " + expectedValue + " is not equal to " + actualValue);
        }
    }

    public void toBeTrue() throws UnsatisfiedExpectationError {

    }

    public void toBeFalse() throws UnsatisfiedExpectationError {

    }

    public void toFail() {
        toFail("Expectation is not satisfied.");
    }

    public void toFail(String message) {
        throw new UnsatisfiedExpectationError(message);
    }
}
