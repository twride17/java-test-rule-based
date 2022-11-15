package main.java.tw.jruletest.parse.rules;

import java.util.ArrayList;
import java.util.Arrays;

public class MethodArgumentRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        rule = rule.replace("with", "").replace("arguments", "").replace(":", "").trim();
        return rule.replace(" and", ",");
    }
}
