package tw.jruletest.parse;

import tw.jruletest.files.FileFinder;
import tw.jruletest.parse.Parser;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestParser {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
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

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/testprograms/Example.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
