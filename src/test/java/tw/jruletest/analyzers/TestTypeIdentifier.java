package tw.jruletest.analyzers;

import org.junit.*;

public class TestTypeIdentifier {

    @Test
    public void testClassType() {
        Assert.assertEquals("String", TypeIdentifier.getType(String.class));
    }

    @Test
    public void testPrimitiveType() {
        Assert.assertEquals("int", TypeIdentifier.getType(int.class));
        Assert.assertEquals("float", TypeIdentifier.getType(float.class));
        Assert.assertEquals("double", TypeIdentifier.getType(double.class));
        Assert.assertEquals("boolean", TypeIdentifier.getType(boolean.class));
    }
}
