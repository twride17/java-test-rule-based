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
        String code = "int value = " + terms.get(0) + ";";
        return code;
    }
}
