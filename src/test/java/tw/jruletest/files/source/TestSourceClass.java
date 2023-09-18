package tw.jruletest.files.source;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.SourceClassLoader;
import tw.jruletest.virtualmachine.TestClassLoader;

import java.io.IOException;
import java.lang.reflect.Member;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestSourceClass {

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        SourceClassLoader.loadClasses("main\\java\\tw\\jruletest\\testing\\examples\\");
    }

    @Test
    public void testGetValidMethod() {
        try {
            SourceClass sourceClass = new SourceClass("tw.jruletest.testing.examples.ExampleClass");
            SourceMember result = sourceClass.getMember("testString");
            Assert.assertEquals("testString", result.getName());
            Assert.assertEquals("java.lang.String", result.getType().getTypeName());
            Assert.assertEquals("tw.jruletest.testing.examples.ExampleClass", result.getFullClassName());
        } catch(ClassNotFoundException e) {
            Assert.fail("Class not found");
        } catch (AmbiguousMemberException e) {
            Assert.fail("Cannot identify member");
        } catch (UnidentifiedCallException e) {
            Assert.fail("Member does not exist");
        }
    }

    @Test
    public void testGetValidField() {
        try {
            SourceClass sourceClass = new SourceClass("tw.jruletest.testing.examples.ExampleClass");
            SourceMember result = sourceClass.getMember("intValue");
            Assert.assertEquals("intValue", result.getName());
            Assert.assertEquals("int", result.getType().getTypeName());
            Assert.assertEquals("tw.jruletest.testing.examples.ExampleClass", result.getFullClassName());
        } catch(ClassNotFoundException e) {
            Assert.fail("Class not found");
        } catch (AmbiguousMemberException e) {
            Assert.fail("Cannot identify member");
        } catch (UnidentifiedCallException e) {
            Assert.fail("Member does not exist");
        }
    }

    @Test
    public void testGetAmbiguousMember() {
        try {
            SourceClass sourceClass = new SourceClass("tw.jruletest.testing.examples.Example2");
            sourceClass.getMember("dummy1");
            Assert.fail("Expected Ambiguous Call Exception");
        } catch(ClassNotFoundException e) {
            Assert.fail("Class not found");
        } catch (AmbiguousMemberException e) { }
        catch (UnidentifiedCallException e) {
            Assert.fail("Member does not exist");
        }
    }

    @Test
    public void testGetNonExistentMember() {
        try {
            SourceClass sourceClass = new SourceClass("tw.jruletest.testing.examples.ExampleClass");
            sourceClass.getMember("dummy");
            Assert.fail("Expected Unidentified Call Exception");
        } catch(ClassNotFoundException e) {
            Assert.fail("Class not found");
        } catch (AmbiguousMemberException e) {
            Assert.fail("Cannot identify member");
        } catch (UnidentifiedCallException e) { }
    }

    @After
    public void teardown() {
        JavaClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/ExampleClass.class"));
        } catch (IOException e) {
            System.out.println("Class file does not exist.");
        }
    }
}
