package tw.jruletest.files.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Utility class used to represent a method defined by a class and provide methods capable of calling and reading the represented method's return value
 *
 * @author Toby Wride
 * */

public class TestMethod extends TestMember {

    /**
     * Instance field storing a reference to the original method being represented by this object
     * */

    private Method method;

    /**
     * TestMethod class constructor inherits from TestMember class
     *
     * @param method the defined method being represented by this object
     * */

    public TestMethod(Method method) {
        super(method);
        this.method = method;
    }

    /**
     * Reads the value returned by the method represented by this object
     *
     * @param instance an instantiation of the class defining this method
     *
     * @return the value returned by calling the method
     * */

    public String readValue(Object instance) {
        return callMethod(instance);
    }

    /**
     * Calls the method represented by this object
     *
     * @param instance an instantiation of the class defining this method
     *
     * @return the value returned by calling the method. null is returned if the method cannot be called
     * */

    public String callMethod(Object instance) {
        try {
            return (String)method.invoke(instance);
        } catch(IllegalArgumentException e) {
            System.out.println("Wrong arguments for " + method.getName());
            for(Type type: method.getParameterTypes()) {
                System.out.println(type.getTypeName());
            }
        } catch (IllegalAccessException e) {
            System.out.println("Couldn't access the method");
        } catch (InvocationTargetException e) {
            System.out.println("Couldn't invoke method");
        }
        return null;
    }

    /**
     * Static helper method used to re-order the methods stored in the provided list so that they are in order in which they were defined in the provided class
     *
     * @param className the name of the defining class
     * @param methods array of original methods to be re-ordered
     *
     * @return list of TestMethod objects representing the re-ordered methods
     * */

    public static ArrayList<TestMethod> orderMethods(String className, Method[] methods) {
        ArrayList<TestMethod> orderedMethods = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "\\src\\test\\java\\" + className.replaceAll("\\.", "\\\\") + ".java";
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))));
            while(orderedMethods.size() < methods.length) {
                String[] terms = javaReader.readLine().split(" ");
                for(String term: terms) {
                    if(!term.isEmpty()) {
                        for(Method method: methods) {
                            if(TestMember.nameMatches(method.getName(), term)){
                                orderedMethods.add(new TestMethod(method));
                            }
                        }
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(NullPointerException e) {}
        return orderedMethods;
    }
}
