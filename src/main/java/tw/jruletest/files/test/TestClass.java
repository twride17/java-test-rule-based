package tw.jruletest.files.test;

import tw.jruletest.virtualmachine.JavaClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class to store details of a test class and provide methods of analysis
 *
 * @author Toby Wride
 * */

public class TestClass {

    /**
     * Instance field for the name of the class being represented by this SourceClass object
     * */

    private String className;

    /**
     * List of TestMethod objects representing the methods defined in the test class represented by this object
     * */

    private ArrayList<TestMethod> methods;

    /**
     * List of TestField objects representing the fields defined in the test class represented by this object
     * */

    private ArrayList<TestField> fields;

    /**
     * Field for storing an instantiated instance of the appropriate class
     * */

    private Object classInstance;

    /**
     * HashMap object mapping a field or method name to the rules extracted from the associated field or method
     * */

    private HashMap<String, String> rules = new HashMap<>();

    /**
     * TestClass constructor uses the represented class's name to find the test class, collects the fields and methods and stores them in their respective objects.
     *
     * @param className the name of the test class to be represented by this object
     *
     * @throws ClassNotFoundException when the test class with the provided class name either doesn't exist or hss not been loaded into the classpath.
     * */

    public TestClass(String className) throws ClassNotFoundException {
        try {
            Class<?> cls = Class.forName(className, false, JavaClassLoader.getLoader());
            classInstance = cls.getDeclaredConstructor().newInstance();
            fields = TestField.orderFields(className, cls.getDeclaredFields());
            methods = TestMethod.orderMethods(className, cls.getDeclaredMethods());
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Couldn't instantiate or access " + className);
        } catch (InvocationTargetException e) {
            System.out.println("Couldn't invoke " + className);
        } catch (NoSuchMethodException e) {
            System.out.println("Couldn't find constructor for " + className);
        }

        this.className = className;
    }

    /**
     * Reads the test class to determine in what order to read the field and method return values
     * */

    public void read() {
        try {
            if ((fields.size() == 1) && (methods.size() > 1)) {
                readFieldAfterMethod();
            } else {
                readFromMethods();
                readFields();
            }
        } catch(IllegalAccessException e) {
            System.out.println("Couldn't access");
            e.printStackTrace();
        }
    }

    /**
     * For each field in the test field list, a new mapping is created with the field name as the key and the rules extracted as the value
     *
     * @throws IllegalAccessException thrown if the field doesn't have a public access modifier
     * */

    public void readFields() throws IllegalAccessException {
        for(TestField field: fields) {
            rules.put(field.getName(), field.readValue(classInstance));
        }
    }

    /**
     * For each method in the test method list, a new mapping is created with the method name as the key and the rules extracted as the value
     * */

    public void readFromMethods() {
        for(TestMethod method: methods) {
            String result = method.readValue(classInstance);
            if(result != null) {
                rules.put(method.getName(), result);
            }
        }
    }

    /**
     * Reads the value of the field after each method is invoked
     *
     * @throws IllegalAccessException thrown if the field doesn't have a public access modifier
     * */

    public void readFieldAfterMethod() throws IllegalAccessException {
        for(TestMethod method: methods) {
            method.readValue(classInstance);
            String newFieldName = fields.get(0).getName() + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
            rules.put(newFieldName, fields.get(0).readValue(classInstance));
        }
    }

    /**
     * Gets the stored class name
     *
     * @return the class name stored by this object
     * */

    public String getClassName() {
        return className;
    }

    /**
     * Gets the rules extracted for each method in the class
     *
     * @return the map object mapping methods to sets of extracted rules
     * */

    public HashMap<String, String> getRules() {
        return rules;
    }
}
