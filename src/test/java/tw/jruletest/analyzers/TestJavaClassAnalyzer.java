package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.classanalysis.AmbiguousClassException;
import tw.jruletest.exceptions.CompilationFailureException;
import tw.jruletest.exceptions.classanalysis.UnknownClassException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestJavaClassAnalyzer {

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        try {
            ArrayList<String> classes = JavaClassLoader.loadClasses("sourceclasses");
            for(String name: classes) {
                SourceClassAnalyzer.addSourceClass(new SourceClass(name));
            }
        } catch(CompilationFailureException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSourceIdentification() {
        try {
            SourceClass cls = SourceClassAnalyzer.identifySourceClass("ExampleClass");
            Assert.assertEquals("ExampleClass", cls.getClassName());
        } catch(AmbiguousClassException | UnknownClassException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testAmbiguousSource() {
        try {
            SourceClassAnalyzer.identifySourceClass("SubExample");
            Assert.fail("Passed when should fail");
        } catch(AmbiguousClassException e) { }
        catch(UnknownClassException e) {
            Assert.fail("Failed for wrong reason");
        }
    }

    @Test
    public void testUnidentifiedSource() {
        try {
            SourceClassAnalyzer.identifySourceClass("Example3");
            Assert.fail("Passed when should fail");
        } catch(AmbiguousClassException e) {
            Assert.fail("Failed for wrong reason");
        }  catch(UnknownClassException e) { }
    }

    @After
    public void teardown() {
        SourceClassAnalyzer.resetSourceClasses();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/examples/sourceclasses/ExampleClass.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
