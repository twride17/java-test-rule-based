package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.UnparsableRuleException;

import java.util.ArrayList;
import java.util.Arrays;

public class GetValueRule implements Rule {

    @Override
    public String decodeRule(String rule) throws UnparsableRuleException {
        ArrayList<String> terms = new ArrayList<>(Arrays.asList(rule.trim().split(" ")));
        terms.remove("value");
        terms.remove("of");
        // Expect class name and field next
        // TODO Find field type, add as identifier
        String classCall = terms.get(0);
        Rule.createImportStatement(classCall.substring(0, classCall.indexOf(".")));
        if(JavaClassAnalyzer.isField(classCall)) {
            return "int value = " + classCall + ";";
        }
        else if (JavaClassAnalyzer.isMethodCall(terms.get(0))) {
            return "int value = " + constructMethodCall(rule, classCall);
        }
        else {
            throw new UnparsableRuleException(rule);
        }
    }

    public String constructMethodCall(String rule, String classCall) {
        return (new MethodCallRule()).decodeRule(rule.substring(rule.indexOf(classCall)));
    }
}
