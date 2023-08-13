package tw.jruletest.translation;

import org.junit.*;

public class TestVariableStore {

    @Before
    public void setup() {
        VariableStore.addVariable("test", "variable", int.class);
    }

    @Test
    public void testAddVariableToExistingMethod() {
        VariableStore.addVariable("test", "otherVariable", int.class);
        Assert.assertTrue(VariableStore.getVarNames("test").contains("otherVariable"));
    }

    @Test
    public void testAddVariableToNewMethod() {
        VariableStore.addVariable("test1", "variable", int.class);
        Assert.assertEquals(1, VariableStore.getVarNames("test1").size());
        Assert.assertTrue(VariableStore.getVarNames("test1").contains("variable"));
    }

    @Test
    public void testGetVariableNameWithNoDuplicates() {
        Assert.assertEquals("variable", VariableStore.getNextUnusedVariableName("newTest", "variable", int.class));
    }

    @Test
    public void testGetVariableNameWithDuplicates() {
        Assert.assertEquals("variable2", VariableStore.getNextUnusedVariableName("test", "variable", int.class));
    }

    @After
    public void teardown() {
        VariableStore.reset();
    }
}
