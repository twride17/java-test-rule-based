package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.JavaClassAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;

public class GetValueRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        ArrayList<String> terms = new ArrayList<>(Arrays.asList(rule.trim().split(" ")));
        terms.remove("value");
        terms.remove("of");
        // Expect class name and field next
        // TODO Find field type, add as identifier
        if(JavaClassAnalyzer.isField(terms.get(0))) {
            return "int value = " + terms.get(0) + ";";
        }
        else if (JavaClassAnalyzer.isMethodCall(terms.get(0))) {
            return "int value = " + new MethodCallRule().decodeRule(rule.substring(rule.indexOf(terms.get(0))));
        }
        else {
            return "Invalid rule: " + rule;
        }
    }
}
