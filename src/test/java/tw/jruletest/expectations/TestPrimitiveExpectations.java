package tw.jruletest.expectations;

import org.junit.*;

public class TestPrimitiveExpectations {

    @Test
    public void testIntegerEqualExpectationSatisfied() {
        try {
            Expectations.expect(20).toEqual(20);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied exception");
        }
    }

    @Test
    public void testIntegerEqualExpectationNotSatisfied() {
        try {
            Expectations.expect(13).toEqual(21);
            Assert.fail("Should not have obtained an unsatisfied exception");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: 13 is not equal to 21 when expected to be", e.getErrorMessage());
        }
    }

    @Test
    public void testIntegerNotEqualExpectationSatisfied() {
        try {
            Expectations.expect(13).toNotEqual(21);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied exception");
        }
    }

    @Test
    public void testIntegerNotEqualExpectationNotSatisfied() {
        try {
            Expectations.expect(20).toNotEqual(20);
            Assert.fail("Should not have obtained an unsatisfied exception");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: 20 is equal to 20 when expected to not be", e.getErrorMessage());
        }
    }
}
