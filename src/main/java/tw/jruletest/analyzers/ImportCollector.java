package tw.jruletest.analyzers;

import java.util.ArrayList;

/**
 * @author Toby Wride
 *
 * Class that stores the imports required in a list.
 * Should be used as temporary storag for use when writing test suites.
 * */

public class ImportCollector {

    /**
     * List of Strings storing the code for the required imports
     * */

    private static ArrayList<String> imports = new ArrayList<>();

    /**
     * Adds the required import code to the list of imports.
     * @param importCode The code to be added to the import list
     * */

    public static void addImport(String importCode) {
        if(!imports.contains(importCode)) {
            imports.add(importCode);
        }
    }

    /**
     * Returns the current import list
     *
     * @return a list of all the imports
     * */

    public static ArrayList<String> getImports() {
        return imports;
    }

    /**
     * Resets the list of imports
     * */

    public static void resetImports() {
        imports = new ArrayList<>();
    }
}
