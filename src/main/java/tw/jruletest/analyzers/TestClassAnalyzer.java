package main.java.tw.jruletest.analyzers;

import main.java.tw.jruletest.app.Runner;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class TestClassAnalyzer {

    public static void extractRules(List<File> testFiles) {
        for(File file: testFiles) {
            readTestClass("test.java." + file.getName().substring(0, file.getName().indexOf(".")));
        }
    }

    private static void readTestClass(String className) {
        try {
            Class<?> cls = Class.forName(className);
            Object classInstance = cls.newInstance();

            Method[] methods = cls.getDeclaredMethods();
            Field[] fields = cls.getDeclaredFields();

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
    }

    private static void readFieldAfterMethod(Object instance, Field field, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
            readFromField(instance, field);
        }
    }

    private static void readFields(Object instance, Field[] fields) {
        for(Field field: fields) {
            readFromField(instance, field);
        }
    }

    private static void readFromMethods(Object instance, Method[] methods) {
        for(Method method: methods) {
            readFromMethod(instance, method);
        }
    }

    private static void readFromMethod(Object instance, Method method) {
        String result = callMethod(instance, method);
        if(result != null) {
            Runner.addRuleSet(result);
        }
    }

    private static void readFromField(Object instance, Field field) {
        try {
            if(field.getType().isArray()) {
                readFromArrayField(instance, field);
            } else {
                Runner.addRuleSet((String) field.get(instance));
            }
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access field: " + field.getName());
        }
    }

    private static void readFromArrayField(Object instance, Field field) {
        try {
            Object[] set = (Object[])field.get(instance);
            String ruleSet = "";
            for(Object rule: set) {
                ruleSet += rule + " ";
            }
            Runner.addRuleSet(ruleSet);
        } catch(IllegalAccessException e) {
            System.out.println("Cannot access the field " + field.getName());
        }
    }

    private static String callMethod(Object instance, Method method) {
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
}
