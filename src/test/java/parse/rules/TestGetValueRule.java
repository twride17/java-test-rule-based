package test.java.parse.rules;

import main.java.tw.jruletest.parse.rules.GetValueRule;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestGetValueRule {

    @BeforeClass
    public static void compileTestPrograms() {
        try {
            Runtime.getRuntime().exec("javac -cp src/test/java/testprograms/Class.java");
        } catch(IOException e) {
            System.out.println("Couldn't compile the test programs.");
        }
    }

    @Test
    public void testGetValueWithIntegerType() {
        String code = new GetValueRule().decodeRule("value of Class.field");
        Assert.assertEquals("int value = Class.field;", code);
    }

    @Test
    public void testGetValueOfIntegerReturnedValue() {
        String code = new GetValueRule().decodeRule("value of Class.method");
        Assert.assertEquals("int value = Class.method();", code);
    }

    @AfterClass
    public static void removeClassFiles() {
        try {
            System.out.println(Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/testprograms/Class.class")));
        } catch(IOException e) {
            System.out.println("Couldn't remove the generated class files.");
        }
    }
}
