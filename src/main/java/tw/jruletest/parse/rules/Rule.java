package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.files.FileFinder;

public interface Rule {

    /**
     * @author Toby Wride
     *
     * Defines the commands to be injected into test suite based on a given rule
     */

    String decodeRule(String rule) throws UnparsableRuleException;

    static void createImportStatement(String className) {
        String importFilename = FileFinder.findFile(className+".java").getPath();
        String packageName = importFilename.substring(importFilename.indexOf("src")+14, importFilename.indexOf("."));
        String importCode = "import " + packageName.replaceAll("\\\\", ".") + ";";
        ImportCollector.addImport(importCode);
    }
}
