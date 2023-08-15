package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import tw.jruletest.files.TestClassFile;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestClassAnalyzer {

    private String className;
    private String filePath;

    private TestClassFile testClassFile;

    public TestClassAnalyzer(File file) {
        filePath = file.getPath();
        int classNameIndex = filePath.lastIndexOf(".");
        int packageNameIndex = filePath.indexOf("test\\java\\")+10;
        className = filePath.substring(packageNameIndex, classNameIndex).replaceAll("\\\\", ".");
        testClassFile = new TestClassFile(className);
    }

    public TestClassFile readTestClass() {
        try {
            Class<?> cls = Class.forName(className, false, JavaClassLoader.getLoader());
            Object classInstance = cls.newInstance();

            Method[] methods = orderMethods(cls.getDeclaredMethods());
            Field[] fields = orderFields(cls.getDeclaredFields());

            if((fields.length == 1) && (methods.length > 1)) {
                readFieldAfterMethod(classInstance, fields[0], methods);
            }
            else {
                readFromMethods(classInstance, methods);
                readFields(classInstance, fields);
            }
        }
        catch (ClassNotFoundException ex) {
            System.out.println("Couldn't find class: " + className);
        } catch (InstantiationException | IllegalAccessException e) {
            System.out.println("Couldn't instantiate " + className);
        }
        return testClassFile;
    }

    private void readFieldAfterMethod(Object instance, Field field, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
            readFromField(instance, field);
        }
    }

    private void readFields(Object instance, Field[] fields) {
        for(Field field: fields) {
            readFromField(instance, field);
        }
    }

    private void readFromMethods(Object instance, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
        }
    }

    private void readFromMethod(Object instance, Method method) {
        String result = callMethod(instance, method);
        if(result != null) {
            testClassFile.addRule(method.getName(), result);
        }
    }

    private void readFromField(Object instance, Field field) {
        try {
            if(field.getType().isArray()) {
                readFromArrayField(instance, field);
            } else {
                testClassFile.addRule(field.getName(), (String) field.get(instance));
            }
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access field: " + field.getName());
        }
    }

    private void readFromArrayField(Object instance, Field field) {
        try {
            Object[] set = (Object[])field.get(instance);
            String ruleSet = "";
            for(Object rule: set) {
                ruleSet += rule + "\n";
            }
            testClassFile.addRule(field.getName(), ruleSet);
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access the field " + field.getName());
        }
    }

    private String callMethod(Object instance, Method method) {
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

    private Method[] orderMethods(Method[] methods) {
        Method[] orderedMethods = new Method[methods.length];
        int methodsFound = 0;
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))));
            while(methodsFound < orderedMethods.length) {
                String[] terms = javaReader.readLine().split(" ");
                for(String term: terms) {
                    if(!term.isEmpty()) {
                        for(Method method: methods) {
                            if(matches(method.getName(), term)){
                                orderedMethods[methodsFound] = method;
                                methodsFound ++;
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

    private Field[] orderFields(Field[] fields) {
        Field[] orderedFields = new Field[fields.length];
        int fieldsFound = 0;
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))));
            while(fieldsFound < orderedFields.length) {
                String[] terms = javaReader.readLine().split(" ");
                for(String term: terms) {
                    if(!term.isEmpty()) {
                        for(Field field: fields) {
                            if(matches(field.getName(), term)) {
                                orderedFields[fieldsFound] = field;
                                fieldsFound++;
                            }
                        }
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(NullPointerException e) {}
        return orderedFields;
    }

    private boolean matches(String name, String code) {
        if(name.contains(".")) {
            name = name.substring(name.lastIndexOf("."));
        }

        if(code.contains("(")) {
            return name.equals(code.substring(0, code.indexOf("(")));
        }
        else if(code.contains(";")){
            return name.equals(code.substring(0, code.indexOf(";")));
        }
        else {
            return name.equals(code);
        }
    }
}
