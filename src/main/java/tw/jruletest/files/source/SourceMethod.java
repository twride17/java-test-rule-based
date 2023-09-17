package tw.jruletest.files.source;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class SourceMethod extends SourceMember {

    private Type[] parameters;

    public SourceMethod(Method method, String className) {
        super(method, className);
        type = method.getReturnType();
        parameters = method.getParameterTypes();
    }
}
