package main.java.tw.jruletest.analyzers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JavaClassAnalyzer {

    /**
     * @author Toby Wride
     *
     * Performs reflection on the source java classes to simplify the process of decoding rules
     * */

    private static Class<?> getRequiredClass(String classname) {
        try {
            return Class.forName("test.java.testprograms." + classname);
        } catch(ClassNotFoundException e) {
            System.out.println("Could not find class: " + classname);
            return null;
        }
    }

    public static String getReturnTypeCode() {
        return null;
    }

    public static Type getReturnType() {
        return null;
    }

    public static boolean isField(String segment) {
        String[] parts = segment.split("\\.");
        Class<?> cls = getRequiredClass(parts[0]);
        Field[] fields = cls.getDeclaredFields();
        for(Field field: fields) {
            boolean x = field.getModifiers() == Modifier.PUBLIC + Modifier.STATIC;
            boolean y = field.getName().equals(parts[1]);

            if((field.getModifiers() == Modifier.PUBLIC + Modifier.STATIC) && field.getName().equals(parts[1])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMethodCall(String segment) {
        String[] parts = segment.split("\\.");
        Class<?> cls = getRequiredClass(parts[0]);
        Method[] methods = cls.getDeclaredMethods();
        for(Method method: methods) {
            if((method.getModifiers() == Modifier.PUBLIC + Modifier.STATIC) && method.getName().equals(parts[1])) {
                return true;
            }
        }
        return false;
    }
}
