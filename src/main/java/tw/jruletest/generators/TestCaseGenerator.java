package tw.jruletest.generators;

/**
 * @author Toby Wride
 *
 * Generates the code for a single test case method
 * */

public class TestCaseGenerator {

    /**
     * Constructs the test case code for a given block of code and wraps it in a default method
     *
     * @param codeBlock: the commands to be included in the test case code
     * @return generated code that has been constructed
     * */

    public static String writeTestCase(String codeBlock) {
        return writeTestCase(codeBlock, "example");
    }

    /**
     * Constructs the test case code for a given block of code and wraps it in a method with the name given
     *
     * @param codeBlock: the commands to be included in the test case code
     * @param methodName: the name of the method to wrap the constructed code in
     * @return generated code that has been constructed
     * */

    public static String writeTestCase(String codeBlock, String methodName) {
        String code = "\tpublic void " + methodName + "() {";
        for(String block: codeBlock.split("\n")) {
            code += "\n\t\t" + block;
        }
        return code + "\n\t}";
    }
}
