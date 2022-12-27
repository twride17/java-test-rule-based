package tw.jruletest.expectations;

public class Expectations {

    public static <T> ExpectationObject<T> expect(T value) {
        return new ExpectationObject<T>(value);
    }
}
