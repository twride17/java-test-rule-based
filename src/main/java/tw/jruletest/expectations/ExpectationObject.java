package tw.jruletest.expectations;

/**
 * ExpectationObject stores the first value to be compared as an instance field of a generic type.
 * The second value to be compared (if first value is not a boolean) is passed to the required method and is thus compared to the stored first value
 *
 * @author Toby Wride
 * */

public class ExpectationObject<T> {

    /**
     * Generic instance field storing the first value
     * */

    private final T firstValue;

    /**
     * Constructor of ExpectationObject
     *
     * @param firstValue the first value to be used in the comparison
     * */

    public ExpectationObject(T firstValue) {
        this.firstValue = firstValue;
    }

    /**
     * Determines if the first value is equal to the second value
     * Currently, the assumption is that the first and second values are of either primitive or String types for equality checking
     *
     * @param secondValue the value to which the first value should be compared
     * */

    public void toEqual(T secondValue) {
        if(!firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is not equal to " + secondValue + " when expected to be");
        } else {
            System.out.println("Expectation Satisfied: " + firstValue + " = " + secondValue);
        }
    }

    /**
     * Determines if the first value is not equal to the second value
     * Currently, the assumption is that the first and second values are of either primitive or String types for equality checking
     *
     * @param secondValue the value to which the first value should be compared
     * */

    public void toNotEqual(T secondValue) {
        if(firstValue.equals(secondValue)) {
            Expectations.failed("Unsatisfied Expectation: " + firstValue + " is equal to " + secondValue + " when expected to not be");
        }
    }

    /**
     * Determines if the value stored in the object's field is true
     * */

    public void toBeTrue() {
        testTruth(true);
    }

    /**
     * Determines if the value stored in the object's field is false
     * */

    public void toBeFalse() {
        testTruth(false);
    }

    /**
     * Tests if the value stored is true or false depending on which is required
     *
     * @param trueRequired flag for if the value should be true or false
     * */

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
