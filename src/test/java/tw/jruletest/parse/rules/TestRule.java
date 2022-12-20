package tw.jruletest.parse.rules;

import org.junit.Assert;
import org.junit.Test;
import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.files.FileFinder;

import java.util.ArrayList;

public class TestRule {

    @Test
    public void testAddingImportStatement() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java\\tw\\jruletest");
        Rule.createImportStatement("Example");
        Rule.createImportStatement("Test");

        ArrayList<String> imports = ImportCollector.getImports();
        Assert.assertTrue(imports.contains("import tw.jruletest.testprograms.Example;"));
        Assert.assertTrue(imports.contains("import tw.jruletest.testprograms.Test;"));
    }
}
