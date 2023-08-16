package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.Runner;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.FileFinder;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class TestFieldNode {

    private FieldNode node;

    /* Testing rule validation for Method node */

    @Before
    public void setup() {
        Runner.setRootPath(System.getProperty("user.dir") + "\\src");
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
        JavaClassLoader.createLoader();
        JavaClassLoader.setLoaderRootPackage("tw");
        JavaClassLoader.loadClasses("programs");
    }

    @Test
    public void testValidField() {
        String rule = "Test.x";
        node = new FieldNode();
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch (InvalidRuleStructureException e) {
            Assert.fail("'" + rule + "': failed");
        }
    }

    @Test
    public void testInvalidField() {
        String rule = "Test.other";
        node = new FieldNode();
        try {
            node.validateRule(rule);
            Assert.fail("'" + rule + "': passed");
        } catch (InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidFieldPlusExtraRule() {
        String[] rules = {"Test.x, something", "Test.example and other"};
        node = new FieldNode();
        int[] indices = {6, 12};
        for(int i = 0; i < rules.length; i++) {
            try {
                Assert.assertEquals(indices[i], node.validateRule(rules[i]));
            } catch (InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    /* Testing code generation for Method node */
    @Test
    public void testCodeGeneration() {
        node = new FieldNode();
        String[] rules = {"Test.x, something", "Test.example and other", "Test.example"};
        String[] expectedCode = {"Test.x", "Test.example", "Test.example"};

        for(int i = 0; i < rules.length; i++) {
            try {
                node.validateRule(rules[i]);
                Assert.assertEquals(expectedCode[i], node.generateCode());
            } catch(InvalidRuleStructureException e) {
                Assert.fail("'" + rules[i] + "': failed");
            }
        }
    }

    @After
    public void teardown() {
        try {
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Example.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Test.class"));
            Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/src/main/java/tw/jruletest/testing/programs/Class.class"));
        } catch(IOException e) {
            System.out.println("Couldn't delete file.");
        }
    }
}
