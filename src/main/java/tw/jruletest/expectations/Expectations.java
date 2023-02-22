package tw.jruletest.expectations;

public class Expectations {

    public static <T> ExpectationObject<T> expect(T value) {
        return new ExpectationObject<T>(value);
    }

    public static void failed() {
        failed("Expectation is not satisfied.");
    }

    public static void failed(String message) {
        throw new UnsatisfiedExpectationError(message);
    }
}
