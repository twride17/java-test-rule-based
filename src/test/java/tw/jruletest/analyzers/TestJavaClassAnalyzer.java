package tw.jruletest.analyzers;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestJavaClassAnalyzer {

    private void loadCLass(String className) {
        try {
            Class<?> c = Runner.getLoader().loadClass(className);
            JavaClassAnalyzer.sourceFiles.put(className, new SourceClass(className, c));
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find " + className);
        } catch (LinkageError e) {
            System.out.println("Linkage error detected for: " + className);
        }
    }

    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
        Runner.createTestClassLoader();
        Runner.runCommand("javac -cp src " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\subpackage1\\*.java " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\subpackage2\\*.java " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\*.java");
        Runner.getLoader().setTopPackage("tw");
    }

    @Test
    public void testSourceIdentification() {
        try {
            loadCLass("tw.jruletest.testexamples.ExampleClass");
            SourceClass cls = JavaClassAnalyzer.identifySourceClass("ExampleClass");
            Assert.assertEquals("ExampleClass", cls.getClassName());
        } catch(UnidentifiedCallException | AmbiguousMemberException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testAmbiguousSource() {
        try {
            loadCLass("tw.jruletest.testexamples.subpackage1.Example");
            loadCLass("tw.jruletest.testexamples.subpackage2.Example");
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
            loadCLass("tw.jruletest.testexamples.subpackage1.Example");
            JavaClassAnalyzer.identifySourceClass("Example3");
            Assert.fail("Passed when should fail");
        } catch(AmbiguousMemberException e) {
            Assert.fail("Failed for wrong reason");
        }  catch(UnidentifiedCallException e) { }
    }

    @After
    public void teardown() {
        JavaClassAnalyzer.sourceFiles = new HashMap<>();
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/subpackage1/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/subpackage2/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/Example2.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testexamples/ExampleClass.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
