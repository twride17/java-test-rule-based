package tw.jruletest.analyzers;

import java.util.ArrayList;

public class ImportCollector {

    private static ArrayList<String> imports = new ArrayList<>();

    public static void addImport(String importCode) {
        if(!imports.contains(importCode)) {
            imports.add(importCode);
        }
    }

    public static ArrayList<String> getImports() {
        return imports;
    }

    public static void resetImports() {
        imports = new ArrayList<>();
    }
}
