package main.java.tw.jruletest.parse;

import main.java.tw.jruletest.parse.rules.GetValueRule;
import main.java.tw.jruletest.parse.rules.MethodCallRule;
import main.java.tw.jruletest.parse.rules.Rule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

public class Parser {

    /**
     * @author Toby Wride
     *
     * Parses the rules from a test case
     */

    private static Rule ruleHandler;

    private static final String[] KEYWORDS = {"Call", "Get"};

    public static String parseRule(String rule) {
        String codeBlock = "";
        ArrayList<String> ruleSegments = getRuleSegments(rule);
        // Currently sequential commands
        // TODO deal with different control flows
        for(String segment: ruleSegments) {
            switch(segment.split(" ")[0]) {
                case "Call":
                    ruleHandler = new MethodCallRule();
                    break;
                case "Get":
                    ruleHandler = new GetValueRule();
                    break;
            }
            codeBlock += ruleHandler.decodeRule(segment.substring(segment.indexOf(" ")+1)) + "\n";
        }
        return codeBlock;
    }

    private static ArrayList<String> getRuleSegments(String rule) {
        ArrayList<String> subRules = new ArrayList<>();
        TreeMap<Integer, String> keywordLocations = new TreeMap<>();
        for(String keyword: KEYWORDS) {
            keywordLocations.put(rule.indexOf(keyword), keyword);
        }

        Integer[] indices = keywordLocations.keySet().toArray(new Integer[0]);
        for(int i = 0; i < indices.length-1; i++) {
            int location = indices[i];
            if(location >= 0) {
                subRules.add(rule.substring(location, indices[i+1]).trim());
            }
        }
        subRules.add(rule.substring(indices[indices.length-1]).trim());
        return subRules;
    }
}
