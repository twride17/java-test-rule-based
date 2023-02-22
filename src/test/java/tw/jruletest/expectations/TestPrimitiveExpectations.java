package tw.jruletest.expectations;

import org.junit.*;

public class TestPrimitiveExpectations {

    @Test
    public void testIntegerEqualExpectationSatisfied() {
        try {
            Expectations.expect(20).toEqual(20);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied expectation error");
        }
    }

    @Test
    public void testIntegerEqualExpectationNotSatisfied() {
        try {
            Expectations.expect(13).toEqual(21);
            Assert.fail("Should have obtained an unsatisfied expectation error");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: 13 is not equal to 21 when expected to be", e.getErrorMessage());
        }
    }

    @Test
    public void testIntegerNotEqualExpectationSatisfied() {
        try {
            Expectations.expect(13).toNotEqual(21);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied expectation error");
        }
    }

    @Test
    public void testIntegerNotEqualExpectationNotSatisfied() {
        try {
            Expectations.expect(20).toNotEqual(20);
            Assert.fail("Should have obtained an unsatisfied expectation error");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: 20 is equal to 20 when expected to not be", e.getErrorMessage());
        }
    }

    @Test
    public void testTrueExpectationSatisfied() {
        try {
            Expectations.expect(true).toBeTrue();
        } catch (UnsatisfiedExpectationError e) {
            Assert.fail("Should not have obtained an unsatisfied expectation error");
        }
    }

    @Test
    public void testTrueExpectationNotSatisfied() {
        try {
            Expectations.expect(false).toBeTrue();
            Assert.fail("Should have obtained an unsatisfied expectation error");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: true value has not been obtained", e.getErrorMessage());
        }
    }

    @Test
    public void testFalseExpectationSatisfied() {
        try {
            Expectations.expect(false).toBeFalse();
        } catch (UnsatisfiedExpectationError e) {
            Assert.fail("Should have obtained an unsatisfied expectation error");
        }
    }

    @Test
    public void testFalseExpectationNotSatisfied() {
        try {
            Expectations.expect(true).toBeFalse();
            Assert.fail("Should have obtained an unsatisfied expectation error");
        } catch (UnsatisfiedExpectationError e) {
            Assert.assertEquals("Unsatisfied Expectation: false value has not been obtained", e.getErrorMessage());
        }
    }

    @Test
    public void testTrueExpectationTypeMismatch() {
        try {
            Expectations.expect(1).toBeTrue();
            Assert.fail("Expected type mismatch");
        } catch(UnsatisfiedExpectationError e) {
            Assert.assertEquals("Type mismatch: expected value is not of Boolean type", e.getErrorMessage());
        }
    }

    @Test
    public void testFalseExpectationTypeMismatch() {
        try {
            Expectations.expect(1).toBeFalse();
            Assert.fail("Expected type mismatch");
        } catch(UnsatisfiedExpectationError e) {
            Assert.assertEquals("Type mismatch: expected value is not of Boolean type", e.getErrorMessage());
        }
    }
}
