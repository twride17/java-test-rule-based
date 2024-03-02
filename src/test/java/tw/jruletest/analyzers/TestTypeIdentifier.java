package tw.jruletest.analyzers;

import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTypeIdentifier {

    @Test
    public void testGetTypeOfObject() {
        Assert.assertEquals("String", TypeIdentifier.getType(String.class));
    }

    @Test
    public void testGetTypeOfPrimitives() {
        Assert.assertEquals("int", TypeIdentifier.getType(int.class));
        Assert.assertEquals("float", TypeIdentifier.getType(float.class));
        Assert.assertEquals("double", TypeIdentifier.getType(double.class));
        Assert.assertEquals("boolean", TypeIdentifier.getType(boolean.class));
    }

    @Test
    public void testTypesAreCompatibleValidPrimitiveCombinations() {
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(boolean.class, boolean.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(int.class, int.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(double.class, double.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(double.class, int.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(double.class, float.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(float.class, float.class));
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(float.class, int.class));
    }

    @Test
    public void testTypesAreCompatibleValidClassCombinations() {
        Assert.assertTrue(TypeIdentifier.typesAreCompatible(String.class, String.class));
    }

    @Test
    public void testTypesAreCompatibleInvalidCombinations() {
        List<Class<?>[]> incompatibleTypes = new ArrayList<>(Arrays.asList(
                new Class<?>[] {double.class, float.class, boolean.class},
                new Class<?>[] {boolean.class},
                new Class<?>[] {double.class, boolean.class},
                new Class<?>[] {int.class, double.class, float.class}
        ));
        Class<?>[] parameterTypes = new Class<?>[] {int.class, double.class, float.class, boolean.class};
        for(int i = 0; i < parameterTypes.length; i++) {
            for(Class<?> type: incompatibleTypes.get(i)) {
                System.out.println(parameterTypes[i] + "," + type);
                Assert.assertFalse(TypeIdentifier.typesAreCompatible(parameterTypes[i], type));
            }
        }
    }
}
