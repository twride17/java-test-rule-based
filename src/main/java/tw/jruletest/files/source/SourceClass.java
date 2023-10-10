package tw.jruletest.files.source;

import tw.jruletest.exceptions.AmbiguousClassException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.virtualmachine.JavaClassLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;

/**
 * Utility class to store details of a source class and provide methods of analysis
 *
 * @author Toby Wride
 * */

public class SourceClass {

    /**
     * Instance field for the name of the class being represented by this SourceClass object
     * */

    private String className;

    /**
     * List of SourceMethod objects representing the methods defined in the class represented by this object
     * */

    private ArrayList<SourceMethod> methods = new ArrayList<>();

    /**
     * List of SourceField objects representing the fields defined in the class represented by this object
     * */

    private ArrayList<SourceField> fields = new ArrayList<>();

    /**
     * SourceClass constructor uses the represented class's name to find the class, collects the fields and methods and stores them in their respective objects.
     *
     * @param className the name of the class to be represented by this object
     *
     * @throws ClassNotFoundException when the class with the provided class name either doesn't exist or hss not been loaded into the classpath.
     * */

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

    /**
     * Finds the method or field defined by the required method or field memberName.
     *
     * @param memberName the name of the member to find
     *
     * @return the member found with the provided name
     *
     * @throws UnidentifiedCallException thrown when theis object does not have a field or method defined by the provided member name
     * @throws AmbiguousClassException thrown when there is more than one occurrence of members with the provided name
     * */

    public SourceMember getMember(String memberName) throws UnidentifiedCallException, AmbiguousClassException {
        SourceMethod requiredMethod = findMethod(memberName, new ArrayList<>());
        SourceField requiredField = findField(memberName);

        if(requiredField == null) {
            if(requiredMethod == null) {
                throw new UnidentifiedCallException(memberName);
            }
            else {
                return requiredMethod;
            }
        } else {
            if(requiredMethod == null) {
                return requiredField;
            }
            else {
                throw new AmbiguousClassException(memberName);
            }
        }
    }

    public SourceMethod findMethod(String call, ArrayList<String> parameterTypes) {
        for(SourceMethod method: methods) {
            if(method.getName().equals(call) && method.hasRequiredParameters(parameterTypes)) {
                return method;
            }
        }
        return null;
    }

    public SourceField findField(String call) {
        for(SourceField field: fields) {
            if(field.getName().equals(call)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns the base class name for the represented class
     *
     * @return the name of the class represented by this object
     * */

    public String getClassName() {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    /**
     * Returns the class name for the represented class, including the packages
     *
     * @return the full name of the class represented by this object
     * */

    public String getFullClassName() {
        return className;
    }
}
