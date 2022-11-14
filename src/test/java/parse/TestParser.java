package test.java.parse;

import main.java.tw.jruletest.parse.Parser;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParser {

    @BeforeClass
    public static void compileTestPrograms() {
        try {
            Runtime.getRuntime().exec("javac -cp src/test/java/testprograms/Example.java");
        } catch(IOException e) {
            System.out.println("Couldn't compile the test programs.");
        }
    }

    @Test
    public void testParseMethodCallCommand() {
        String code = Parser.parseRule("Call Example.methodName with 1234");
        Assert.assertEquals("Example.methodName(1234);\n", code);
    }

    @Test
    public void testParseGetValueCommand() {
        String code = Parser.parseRule("Get value of Example.example");
        Assert.assertEquals("int value = Example.example;\n", code);
    }

    @Test
    public void testParseMethodAndValueCommands() {
        String code = Parser.parseRule("Call Example.methodName with 1234 Get value of Example.example");
        Assert.assertEquals("Example.methodName(1234);\nint value = Example.example;\n", code);
    }

    @AfterClass
    public static void removeClassFiles() {
        try {
            System.out.println(Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/testprograms/Example.class")));
        } catch(IOException e) {
            System.out.println("Couldn't remove the generated class files.");
        }
    }
}
