package tw.jruletest.expectations;

import org.junit.*;

public class TestStringExpectations {

    @Test
    public void testStringEqualExpectationSatisfied() {
        try {
            Expectations.expect("String").toEqual("String");
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied exception");
        }
    }

    @Test
    public void testStringEqualExpectationNotSatisfied() {
        try {
            Expectations.expect("String").toEqual("Wrong String");
            Assert.fail("Expected to get unsatisfied exception");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: String is not equal to Wrong String when expected to be", e.getErrorMessage());
        }
    }

    @Test
    public void testStringNotEqualExpectationSatisfied() {
        try {
            Expectations.expect("String").toNotEqual("Wrong String");
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied exception");
        }
    }

    @Test
    public void testStringNotEqualExpectationNotSatisfied() {
        try {
            Expectations.expect("String").toNotEqual("String");
            Assert.fail("Expected to get unsatisfied exception");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: String is equal to String when expected to not be", e.getErrorMessage());
        }
    }
}
