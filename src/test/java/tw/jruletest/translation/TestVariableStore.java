package tw.jruletest.translation;

import org.junit.*;

public class TestVariableStore {

    @Before
    public void setup() {
        VariableStore.addVariable("test", "variable");
    }

    @Test
    public void testAddVariableToExistingMethod() {
        VariableStore.addVariable("test", "otherVariable");
        Assert.assertTrue(VariableStore.getVars("test").contains("otherVariable"));
    }

    @Test
    public void testAddVariableToNewMethod() {
        VariableStore.addVariable("test1", "variable");
        Assert.assertEquals(1, VariableStore.getVars("test1").size());
        Assert.assertTrue(VariableStore.getVars("test1").contains("variable"));
    }

    @Test
    public void testGetVariableNameWithNoDuplicates() {
        Assert.assertEquals("variable", VariableStore.getNextUnusedVariableName("newTest", "variable"));
    }

    @Test
    public void testGetVariableNameWithDuplicates() {
        Assert.assertEquals("variable2", VariableStore.getNextUnusedVariableName("test", "variable"));
    }

    @After
    public void teardown() {
        VariableStore.reset();
    }
}
