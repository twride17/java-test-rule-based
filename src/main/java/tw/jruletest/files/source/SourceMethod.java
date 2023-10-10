package tw.jruletest.files.source;

import tw.jruletest.analyzers.TypeIdentifier;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

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

    public boolean hasRequiredParameters(ArrayList<String> parameterNames) {
        if(parameterNames.size() != parameters.length) {
            return false;
        } else {
            for(int i = 0; i < parameters.length; i++) {
                if(!parameterNames.get(i).equals(TypeIdentifier.getType(parameters[i]))) {
                    return false;
                }
            }
            return true;
        }
    }
}
