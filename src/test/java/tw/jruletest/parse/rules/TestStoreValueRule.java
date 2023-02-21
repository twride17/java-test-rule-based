package tw.jruletest.parse.rules;

import org.junit.*;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.files.FileFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestStoreValueRule {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    @Test
    public void testStoreFieldValue() {
        try {
            String rule = "value of Class.field in field";
            Assert.assertEquals("int field = Class.field", (new StoreValueRule()).decodeRule(rule));
        } catch(UnparsableRuleException e) {
            Assert.fail("Should have found field in Class");
        }
    }

    @Test
    public void testStoreMethodValue() {
        try {
            String rule = "value of Class.method in field";
            Assert.assertEquals("int field = Class.method()", (new StoreValueRule()).decodeRule(rule));
        } catch(UnparsableRuleException e) {
            Assert.fail("Should have found method in Class");
        }
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file");
        }
    }
}
