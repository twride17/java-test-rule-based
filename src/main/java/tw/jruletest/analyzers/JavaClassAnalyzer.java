package tw.jruletest.analyzers;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.FileFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class JavaClassAnalyzer {

    /**
     * @author Toby Wride
     *
     * Performs reflection on the source java classes to simplify the process of decoding rules
     * */

    private static Class<?> getRequiredClass(String className) throws ClassNotFoundException {
        try {
            String filePath = FileFinder.findFile("\\" + className + ".java", "").getPath();
            String currentClass = filePath.substring(filePath.indexOf("src"), filePath.indexOf("."));
            Runner.runCommand("javac -cp src " + currentClass + ".java");

            currentClass = currentClass.substring(14).replaceAll("\\\\", ".");
            Runner.getLoader().setFilePath(filePath.replace(".java", ".class"));
            Runner.getLoader().setTopPackage(currentClass.substring(0, currentClass.indexOf(".")));
            try {
                Runner.getLoader().loadClass(currentClass);
            } catch (LinkageError e) {}
            return Class.forName(currentClass, true, Runner.getLoader());
        } catch(NullPointerException e) {
            throw new ClassNotFoundException();
        }
    }

    public static CallType getCallType(String segment) throws UnidentifiedCallException {
        if(isField(segment)) {
            return CallType.FIELD;
        } else if (isMethodCall(segment)) {
            return CallType.METHOD;
        } else {
            throw new UnidentifiedCallException(segment);
        }
    }

    private static boolean isField(String segment) {
        String[] parts = segment.split("\\.");
        try {
            Class<?> cls = getRequiredClass(parts[0]);
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if ((field.getModifiers() == Modifier.PUBLIC + Modifier.STATIC) && field.getName().equals(parts[1])) {
                    return true;
                }
            }
            return false;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isMethodCall(String segment) {
        String[] parts = segment.split("\\.");
        try {
            Class<?> cls = getRequiredClass(parts[0]);
            Method[] methods = cls.getDeclaredMethods();
            for(Method method: methods) {
                if((method.getModifiers() == Modifier.PUBLIC + Modifier.STATIC) && method.getName().equals(parts[1])) {
                    return true;
                }
            }
            return false;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }

    public static Type getReturnType(String name, boolean methodRequired) {
        try {
            String className = name.split("\\.")[0];
            String callName = name.split("\\.")[1];
            if(methodRequired) {
                return getMethodType(className, callName);
            } else {
                return getFieldType(className, callName);
            }
        } catch(ClassNotFoundException e) {
            System.out.println("Couldn't find the class for: " + name);
        }
        return null;
    }

    private static Type getMethodType(String className, String callName) throws ClassNotFoundException {
        Method[] methods = getRequiredClass(className).getDeclaredMethods();
        for(Method method: methods) {
            if(method.getName().equals(callName)) {
                return method.getReturnType();
            }
        }
        return null;
    }

    private static Type getFieldType(String className, String callName) throws ClassNotFoundException {
        Field[] fields = getRequiredClass(className).getDeclaredFields();
        for(Field field: fields) {
            if(field.getName().equals(callName)) {
                return field.getType();
            }
        }
        return null;
    }
}
