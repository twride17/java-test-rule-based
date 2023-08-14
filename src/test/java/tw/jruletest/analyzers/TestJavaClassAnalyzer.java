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
        Runner.runCommand("javac -cp src src/test/java/tw/jruletest/testprograms/Example.java");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
        Runner.createTestClassLoader();
        Runner.runCommand("javac -cp src " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\subpackage1\\*.java " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\subpackage2\\*.java " +
                            System.getProperty("user.dir") + "\\src\\main\\java\\tw\\jruletest\\testexamples\\*.java");
        Runner.getLoader().setTopPackage("tw");
    }

    @Test
    public void testMethodCallType() {
        try {
            loadCLass("tw.jruletest.testexamples.ExampleClass");
            Assert.assertEquals("java.lang.String", JavaClassAnalyzer.getReturnType("ExampleClass.testString").getTypeName());
            Assert.assertEquals("int", JavaClassAnalyzer.getReturnType("ExampleClass.testInt").getTypeName());
            Assert.assertEquals("char", JavaClassAnalyzer.getReturnType("ExampleClass.testChar").getTypeName());
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testFieldType(){
        try {
            loadCLass("tw.jruletest.testexamples.ExampleClass");
            Assert.assertEquals("int", JavaClassAnalyzer.getReturnType("ExampleClass.intValue").getTypeName());
            Assert.assertEquals("float", JavaClassAnalyzer.getReturnType("ExampleClass.floatValue").getTypeName());
            Assert.assertEquals("double", JavaClassAnalyzer.getReturnType("ExampleClass.doubleValue").getTypeName());
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testAmbiguousSource() {
        try {
            loadCLass("tw.jruletest.testexamples.subpackage1.Example");
            loadCLass("tw.jruletest.testexamples.subpackage2.Example");
            JavaClassAnalyzer.getReturnType("Example.dummy").getTypeName();
            Assert.fail("Passed");
        } catch(AmbiguousMemberException e) { }
        catch(UnidentifiedCallException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testAmbiguousCall() {
        try {
            loadCLass("tw.jruletest.testexamples.Example2");
            JavaClassAnalyzer.getReturnType("Example2.dummy1").getTypeName();
            Assert.fail("Passed");
        } catch(AmbiguousMemberException e) { }
        catch(UnidentifiedCallException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testUnidentifiedSource() {
        try {
            JavaClassAnalyzer.getReturnType("Example2.dummy").getTypeName();
            Assert.fail("Passed");
        } catch(UnidentifiedCallException e) { }
        catch(AmbiguousMemberException e) {
            Assert.fail("Failed");
        }
    }

    @Test
    public void testUnidentifiedCall() {
        try {
            loadCLass("tw.jruletest.testexamples.subpackage1.Example2");
            JavaClassAnalyzer.getReturnType("Example2.dummy").getTypeName();
            Assert.fail("Passed");
        } catch(UnidentifiedCallException e) { }
        catch(AmbiguousMemberException e) {
            Assert.fail("Failed");
        }
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
