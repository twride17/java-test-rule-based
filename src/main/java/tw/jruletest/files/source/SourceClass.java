package tw.jruletest.files.source;

import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SourceClass {

    private String className;
    private ArrayList<SourceMethod> methods = new ArrayList<>();
    private ArrayList<SourceField> fields = new ArrayList<>();

    public SourceClass(String className) throws ClassNotFoundException {
        Class<?> cls = Class.forName(className, false, JavaClassLoader.getLoader());
        Field[] classFields = cls.getDeclaredFields();
        Method[] classMethods = cls.getDeclaredMethods();

        this.className = className;

        for(Field field: classFields) {
            fields.add(new SourceField(field, className));
        }

        for(Method method: classMethods) {
            methods.add(new SourceMethod(method, className));
        }
    }

    public SourceMember getMember(String call) throws UnidentifiedCallException, AmbiguousMemberException {
        SourceMethod requiredMethod = findMethod(call);
        SourceField requiredField = findField(call);

        if(requiredField == null) {
            if(requiredMethod == null) {
                throw new UnidentifiedCallException(call);
            }
            else {
                return requiredMethod;
            }
        } else {
            if(requiredMethod == null) {
                return requiredField;
            }
            else {
                throw new AmbiguousMemberException(call);
            }
        }
    }

    private SourceMethod findMethod(String call) {
        for(SourceMethod method: methods) {
            if(method.getName().equals(call)) {
                return method;
            }
        }
        return null;
    }

    private SourceField findField(String call) {
        for(SourceField field: fields) {
            if(field.getName().equals(call)) {
                return field;
            }
        }
        return null;
    }

    public String getClassName() {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public String getFullClassName() {
        return className;
    }
}
