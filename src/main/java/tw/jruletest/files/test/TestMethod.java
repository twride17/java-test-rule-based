package tw.jruletest.files.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestMethod extends TestMember {

    private Method method;

    public TestMethod(Method method, String className) {
        super(method, className);
        this.method = method;
    }

    public String readValue(Object instance) {
        return callMethod(instance);
    }

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
                                orderedMethods.add(new TestMethod(method, className));
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
