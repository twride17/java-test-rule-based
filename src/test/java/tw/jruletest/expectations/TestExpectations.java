package tw.jruletest.expectations;

import org.junit.*;

import tw.jruletest.testing.programs.Example;

public class TestExpectations {

    @Test
    public void testIntegerExpectationSatisfied() {
        try {
            Example.exampleMethod(20);
            Expectations.expect(Example.example).toEqual(20);
        } catch(UnsatisfiedExpectationError e) {
            Assert.fail();
        }
    }

    @Test
    public void testIntegerExpectationNotSatisfied() {
        try {
            Example.exampleMethod(20);
            Expectations.expect(Example.example).toEqual(21);
            Assert.fail();
        } catch(UnsatisfiedExpectationError e) {}
    }
}
