package tw.jruletest.analyzers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestImportCollector {

    @Before
    public void setup() {
        ImportCollector.addImport("Test Import");
    }

    @Test
    public void testAddNewImport() {
        ImportCollector.addImport("New Import");
        Assert.assertTrue(ImportCollector.getImports().contains("New Import"));
        Assert.assertEquals(2, ImportCollector.getImports().size());
    }

    @Test
    public void testAddExistingImport() {
        ImportCollector.addImport("Test Import");
        Assert.assertEquals(1, ImportCollector.getImports().size());
    }

    @Test
    public void testResetImports() {
        ImportCollector.resetImports();
        Assert.assertTrue(ImportCollector.getImports().isEmpty());
    }

    @After
    public void teardown() {
        ImportCollector.resetImports();
    }
}
