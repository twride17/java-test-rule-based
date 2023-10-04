package tw.jruletest.files.source;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Class which represents a method from a compiled and loaded source class
 *
 * @author Toby Wride
 * */

public class SourceMethod extends SourceMember {

    /**
     * Array of type java.lang.reflect.Type storing the parameters defined in the method's signature
     * */

    private Type[] parameters;

    /**
     * SourceField constructor extends from the SourceMember class
     *
     * @param method the class's declared field
     * @param className the name of the class which declared the field
     * */

    public SourceMethod(Method method, String className) {
        super(method, className);
        type = method.getReturnType();
        parameters = method.getParameterTypes();
    }
}
