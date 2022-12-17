package tw.jruletest.analyzers;

import tw.jruletest.app.Runner;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class JavaClassAnalyzer {

    /**
     * @author Toby Wride
     *
     * Performs reflection on the source java classes to simplify the process of decoding rules
     * */

    private static Class<?> getRequiredClass(String className) throws ClassNotFoundException {
        String currentClass = "";
        List<File> files = Runner.searchFiles(new File(System.getProperty("user.dir") + "\\src\\test\\java"), new ArrayList<>());
        for(File file: files) {
            String path = file.getPath();
            if(path.contains(className+".java") && !path.contains("generated")) {
                //System.out.println(path);
                currentClass = path.substring(path.indexOf("src\\test\\java"), path.lastIndexOf("\\")+1) + className;
                Runner.runCommand("javac -cp src " + currentClass + ".java");
            }
        }

        currentClass = currentClass.substring(14).replaceAll("\\\\", ".");
        //System.out.println(currentClass);
        try {
            Runner.getLoader().loadClass(currentClass);
        } catch(LinkageError e) {}
        return Class.forName(currentClass, true, Runner.getLoader());
    }

    public static boolean isField(String segment) {
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
            System.out.println("Could not find class: " + parts[0]);
            return false;
        }

    }

    public static boolean isMethodCall(String segment) {
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
            System.out.println("Could not find class: " + parts[0]);
            return false;
        }
    }
}
