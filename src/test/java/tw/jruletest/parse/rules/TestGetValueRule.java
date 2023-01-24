package tw.jruletest.parse.rules;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.parse.rules.GetValueRule;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestGetValueRule {

    @Before
    public void setup() {
        Runner.runCommand("javac -cp src src\\test\\java\\tw\\jruletest\\testprograms\\Class.java");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    @Test
    public void testGetValueWithIntegerType() {
        try {
            String code = new GetValueRule().decodeRule("value of Class.field");
            Assert.assertEquals("int value = Class.field;", code);
        } catch(UnparsableRuleException e) {
            Assert.fail();
        }
    }

    @Test
    public void testGetValueOfIntegerReturnedValue() {
        try {
            String code = new GetValueRule().decodeRule("value of Class.method");
            Assert.assertEquals("int value = Class.method();", code);
        } catch(UnparsableRuleException e) {
            Assert.fail();
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
