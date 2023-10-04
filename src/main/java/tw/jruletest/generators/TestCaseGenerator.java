package tw.jruletest.generators;

/**
 * Generates the code for a single test case based on the tree generated during rule validation
 *
 * @author Toby Wride
 * */

public class TestCaseGenerator {

    /**
     * Constructs the test case code for a given block of code and wraps it in a method with the provided name
     *
     * @param codeBlock: the commands to be included in the test case code
     * @param methodName: the name of the method to wrap the constructed code in
     * @return the code generated for the method
     * */

    public static String writeTestCase(String codeBlock, String methodName) {
        String code = "\tpublic void " + methodName + "() {";
        for(String block: codeBlock.split("\n")) {
            code += "\n\t\t" + block;
        }
        return code + "\n\t}";
    }
}
