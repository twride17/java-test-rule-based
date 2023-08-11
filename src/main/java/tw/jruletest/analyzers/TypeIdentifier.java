package tw.jruletest.analyzers;

import java.lang.reflect.Type;

public class TypeIdentifier {

    public static String getType(Type type) {
        String typeName = type.getTypeName();
        int endPackageIndex = typeName.lastIndexOf(".");
        if(endPackageIndex == -1) {
            return typeName;
        } else {
            return typeName.substring(endPackageIndex + 1);
        }
    }
}
