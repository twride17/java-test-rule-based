package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.virtualmachine.JavaClassLoader;
import tw.jruletest.virtualmachine.SourceClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class TestJavaClassAnalyzer {

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        SourceClassLoader.loadClasses("examples");
    }

    @Test
    public void testSourceIdentification() {
        try {
            SourceClass cls = JavaClassAnalyzer.identifySourceClass("ExampleClass");
            Assert.assertEquals("ExampleClass", cls.getClassName());
        } catch(UnidentifiedCallException | AmbiguousMemberException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testAmbiguousSource() {
        try {
            JavaClassAnalyzer.identifySourceClass("Example");
            Assert.fail("Passed when should fail");
        } catch(AmbiguousMemberException e) { }
        catch(UnidentifiedCallException e) {
            Assert.fail("Failed for wrong reason");
        }
    }

    @Test
    public void testUnidentifiedSource() {
        try {
            JavaClassAnalyzer.identifySourceClass("Example3");
            Assert.fail("Passed when should fail");
        } catch(AmbiguousMemberException e) {
            Assert.fail("Failed for wrong reason");
        }  catch(UnidentifiedCallException e) { }
    }

    @After
    public void teardown() {
        JavaClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/examples/ExampleClass.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
