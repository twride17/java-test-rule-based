package main.java.tw.jruletest.parse.rules;

import java.util.ArrayList;
import java.util.Arrays;

public class MethodArgumentRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        rule = rule.replace("with arguments", "").replace(":", "");
        ArrayList<String> terms = new ArrayList<>(Arrays.asList(rule.split(" ")));

        boolean andRemaining;
        do {
            andRemaining = terms.remove("and");
        } while(andRemaining);

        String argList = terms.get(0).replace(",", "");
        for(int i = 1; i < terms.size(); i++) {
            argList += ", " + terms.get(i);
        }
        return argList;
    }
}
