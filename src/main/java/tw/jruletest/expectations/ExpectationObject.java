package tw.jruletest.expectations;

public class ExpectationObject<T> {

    private T firstValue;

    public ExpectationObject(T expected) {
        firstValue = expected;
    }

    public void toEqual(T secondValue) throws UnsatisfiedExpectationError {
        if(!firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is not equal to " + secondValue + " when expected to be");
        }
    }

    public void toNotEqual(T secondValue) throws UnsatisfiedExpectationError {
        if(firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is equal to " + secondValue + " when expected to not be");
        }
    }

    public void toBeTrue() throws UnsatisfiedExpectationError {
        if(firstValue instanceof Boolean) {
            testTruth(true);
        } else {
            Expectations.failed("Type mismatch: expected value is not of Boolean type");
        }
    }

    public void toBeFalse() throws UnsatisfiedExpectationError {
        if(firstValue instanceof Boolean) {
            testTruth(false);
        } else {
            Expectations.failed("Type mismatch: expected value is not of Boolean type");
        }
    }

    private void testTruth(boolean trueRequired) {
        if((Boolean) firstValue != trueRequired) {
            Expectations.failed("Unsatisfied Expectation: " + trueRequired + " value has not been obtained");
        }
    }
}
