//package tw.jruletest.parse.ruletree.rulenodes;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import tw.jruletest.exceptions.UnparsableRuleException;
//import tw.jruletest.files.FileFinder;
//import tw.jruletest.parse.rules.GetValueRule;
//import tw.jruletest.translation.VariableStore;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//public class TestGetValueNode {
//
//    @Before
//    public void setup() {
//        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
//    }
//
//    @Test
//    public void testGetValueWithIntegerType() {
//        try {
//            String code = new GetValueRule().decodeRule("value of Class.field");
//            Assert.assertEquals("int fieldValue = Class.field", code);
//        } catch(UnparsableRuleException e) {
//            e.printError();
//            Assert.fail();
//        }
//    }
//
//    @Test
//    public void testGetValueOfIntegerReturnedValue() {
//        try {
//            String code = new GetValueRule().decodeRule("value of Class.method");
//            Assert.assertEquals("int methodValue = Class.method()", code);
//        } catch(UnparsableRuleException e) {
//            e.printError();
//            Assert.fail();
//        }
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
