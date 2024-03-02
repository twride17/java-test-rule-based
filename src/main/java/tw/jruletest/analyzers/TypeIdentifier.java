package tw.jruletest.analyzers;

import java.lang.reflect.Type;

/**
 * Class designed to determine type name
 *
 * @author Toby Wride
 * */

public class TypeIdentifier {

    /**
     * Returns a String representation of the provided type
     *
     * @param type the type that translation is required for
     * @return a String representation of the provided type for use in code generation
     * */

    public static String getType(Type type) {
        String typeName = type.getTypeName();
        int endPackageIndex = typeName.lastIndexOf(".");
        if(endPackageIndex == -1) {
            return typeName;
        } else {
            return typeName.substring(endPackageIndex + 1);
        }
    }

    public static boolean typesAreCompatible(Type parameterType, Type argumentType) {
        String parameter = getType(parameterType);
        String argument = getType(argumentType);
        switch(parameter) {
            case "double":
                return argument.equals("int") || argument.equals("double") || argument.equals("float");
            case "float":
                return argument.equals("int") || argument.equals("float");
            default:
                return parameter.equals(argument);
        }
    }
}
