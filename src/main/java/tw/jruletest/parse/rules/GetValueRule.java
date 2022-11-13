package main.java.tw.jruletest.parse.rules;

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

        // TODO Use reflection to find if method or variable
        if(!terms.get(0).contains(".")) {
            return "int value = " + terms.get(0) + ";";
        }
        else {
            return "int value = " + new MethodCallRule().decodeRule(rule.substring(rule.indexOf(terms.get(0))));
        }
    }
}
