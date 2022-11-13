package main.java.tw.jruletest.parse.rules;

public class MethodCallRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        // TODO Find class file
        // TODO Check if static method
        // TODO If static, call Class.method()
        // TODO If not call object.method()
        // TODO Call with arguments defined
        rule = rule.replace(" method ", "");
        String[] ruleTerms = rule.split(" ");
        // Expect method identifier at start of term list
        // (ie: Call Class.method = 'Class.method'
        // Assuming static method
        String code = ruleTerms[0] + "(";
        // TODO use reflection to find number of arguments
        // Avoid using rule in case written incorrectly
        if(ruleTerms.length != 1) {
            code += new MethodArgumentRule().decodeRule(rule.substring(rule.indexOf("with")));
        }
        return code + ");";
    }
}
