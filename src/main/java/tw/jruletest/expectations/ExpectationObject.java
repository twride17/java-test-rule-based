package tw.jruletest.expectations;

public class ExpectationObject<T> {

    private final T firstValue;

    public ExpectationObject(T firstValue) {
        this.firstValue = firstValue;
    }

    public void toEqual(T secondValue) {
        if(!firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is not equal to " + secondValue + " when expected to be");
        } else {
            System.out.println("Expectation Satisfied: " + firstValue + " = " + secondValue);
        }
    }

    public void toNotEqual(T secondValue) {
        if(firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is equal to " + secondValue + " when expected to not be");
        }
    }

    public void toBeTrue() {
        testTruth(true);
    }

    public void toBeFalse() {
        testTruth(false);
    }

    private void testTruth(boolean trueRequired) {
        try {
            if ((Boolean) firstValue != trueRequired) {
                Expectations.failed("Unsatisfied Expectation: " + trueRequired + " value has not been obtained");
            }
        } catch(ClassCastException e) {
            Expectations.failed("Type mismatch: expected value is not of Boolean type");
        }
    }
}
