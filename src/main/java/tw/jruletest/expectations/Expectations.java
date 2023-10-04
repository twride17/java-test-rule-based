package tw.jruletest.expectations;

/**
 * Class designed to create the appropriate ExpectationObject and record the result
 *
 * @author Toby Wride
 * */

public class Expectations {

    /**
     * Creates expectation object for specified value
     *
     * @param value the value to store in the object
     * @param <T> generic type to allow for all primitive types and objects
     *
     * @return an ExpectationObject representing the given expected (or first) value
     * */

    public static <T> ExpectationObject<T> expect(T value) {
        return new ExpectationObject<T>(value);
    }

    /**
     * Generic failure method for if an expectation has not been satisfied
     * */

    public static void failed() {
        failed("Expectation is not satisfied.");
    }

    /**
     * Failure method capable of outputting any failure message required.
     *
     * @param message the error message to be shown
     *
     * @throws UnsatisfiedExpectationError always thrown with whatever is stored in 'message'
     * */

    public static void failed(String message) {
        throw new UnsatisfiedExpectationError(message);
    }
}
