package main.java.tw.jruletest.analyzers;

import main.java.tw.jruletest.app.Runner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

public class TestClassAnalyzer {

    /**
     * Obtains the rules defined in each of the files provided and passes them to the Runner class
     *
     * @param testFiles: list of files from which to extract the rules
     * */

    public static void extractRules(List<File> testFiles) {
        for(File file: testFiles) {
            readTestClass("test.java.examples." + file.getName().substring(0, file.getName().indexOf(".")), file);
        }
    }

    private static void readTestClass(String className, File file) {
        try {
            Class<?> cls = Class.forName(className);
            Object classInstance = cls.newInstance();

            Method[] methods = orderMethods(cls.getDeclaredMethods(), file);
            Field[] fields = orderFields(cls.getDeclaredFields(), file);

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

    private static Method[] orderMethods(Method[] methods, File file) {
        Method[] orderedMethods = new Method[methods.length];
        int methodsFound = 0;
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
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

    private static Field[] orderFields(Field[] fields, File file) {
        Field[] orderedFields = new Field[fields.length];
        int fieldsFound = 0;
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
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

    private static boolean matches(String name, String code) {
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
