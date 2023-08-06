//package tw.jruletest.parse.ruletree.rulenodes;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import tw.jruletest.Runner;
//import tw.jruletest.files.FileFinder;
//import tw.jruletest.translation.VariableStore;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class TestExpectationNode {
//
//    @Before
//    public void setup() {
//        VariableStore.addVariable("", "value");
//        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
//        Runner.createTestClassLoader();
//    }
//
//    @Test
//    public void testIntegerVariableValueNotEquals() {
//        String rule = "value to not equal 0";
//        Assert.assertEquals("Expectations.expect(value).toNotEqual(0)", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @Test
//    public void testIntegerVariableValueEquals() {
//        String rule = "value to equal 0";
//        Assert.assertEquals("Expectations.expect(value).toEqual(0)", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @Test
//    public void testIntegerMethodCallNotEquals() {
//        String rule = "value of Class.method to not equal 0";
//        Assert.assertEquals("Expectations.expect(Class.method()).toNotEqual(0)", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @Test
//    public void testIntegerMethodCallEquals() {
//        String rule = "value of Class.method to equal 0";
//        Assert.assertEquals("Expectations.expect(Class.method()).toEqual(0)", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @Test
//    public void testValueNotEqualsIntegerMethodCall() {
//        String rule = "0 to not equal value of Class.method";
//        Assert.assertEquals("Expectations.expect(0).toNotEqual(Class.method())", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @Test
//    public void testValueEqualsIntegerMethodCall() {
//        String rule = "0 to equal value of Class.method";
//        Assert.assertEquals("Expectations.expect(0).toEqual(Class.method())", (new ExpectationRule()).decodeRule(rule));
//    }
//
//    @After
//    public void teardown() {
//        VariableStore.reset();
//        try {
//            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Class.class"));
//        } catch(IOException e) {
//            System.out.println("Couldn't delete file");
//        }
//    }
//}
