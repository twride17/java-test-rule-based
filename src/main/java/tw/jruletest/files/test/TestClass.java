package tw.jruletest.files.test;

import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestClass {

    private String className;

    private ArrayList<TestMethod> methods;
    private ArrayList<TestField> fields;

    private Object classInstance;
    private HashMap<String, String> rules = new HashMap<>();

    public TestClass(String className) throws ClassNotFoundException {
        try {
            Class<?> cls = Class.forName(className, false, JavaClassLoader.getLoader());
            classInstance = cls.newInstance();
            fields = TestField.orderFields(className, cls.getDeclaredFields());
            methods = TestMethod.orderMethods(className, cls.getDeclaredMethods());
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Couldn't instantiate " + className);
        }

        this.className = className;
    }

    public void read() {
        try {
            if ((fields.size() == 1) && (methods.size() > 1)) {
                readFieldAfterMethod(classInstance);
            } else {
                readFromMethods(classInstance);
                readFields(classInstance);
            }
        } catch(IllegalAccessException e) {
            System.out.println("Couldn't access");
            e.printStackTrace();
        }
    }

    public void readFields(Object instance) throws IllegalAccessException {
        for(TestField field: fields) {
            rules.put(field.getName(), field.readValue(instance));
        }
    }

    public void readFromMethods(Object instance) {
        for(TestMethod method: methods) {
            String result = method.readValue(instance);
            if(result != null) {
                rules.put(method.getName(), result);
            }
        }
    }

    public void readFieldAfterMethod(Object instance) throws IllegalAccessException {
        for(TestMethod method: methods) {
            method.readValue(instance);
            String newFieldName = fields.get(0).getName() + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
            rules.put(newFieldName, fields.get(0).readValue(instance));
        }
    }

    public String getClassName() {
        return className;
    }

    public String getFullClassName() {
        return className;
    }

    public HashMap<String, String> getRules() {
        return rules;
    }
}
