package main.java.tw.jruletest.parse;

import main.java.tw.jruletest.parse.rules.GetValueRule;
import main.java.tw.jruletest.parse.rules.MethodCallRule;
import main.java.tw.jruletest.parse.rules.Rule;

import java.util.*;


public class Parser {

    /**
     * @author Toby Wride
     *
     * Parses the rules from a test case
     */

    private static Rule ruleHandler;

    private static final String[] KEYWORDS = {"Call", "Get"};
    private static final HashMap<String, Rule> KEYWORD_HANDLERS = mapKeywordsToHandlers();

    public static String parseRule(String rule) {
        String codeBlock = "";
        ArrayList<String> ruleSegments = getRuleSegments(rule);
        // Currently sequential commands
        // TODO deal with different control flows
        for(String segment: ruleSegments) {
            String keyword = segment.split(" ")[0];
            String remains = segment.substring(segment.indexOf(" ")+1);
            codeBlock += KEYWORD_HANDLERS.get(keyword).decodeRule(remains) + "\n";
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

    private static HashMap<String, Rule> mapKeywordsToHandlers() {
        HashMap<String, Rule> keywordHandlers = new HashMap<>();
        for(String keyword: KEYWORDS) {
            Rule handler = null;
            switch (keyword) {
                case "Get":
                    handler = new GetValueRule();
                    break;
                case "Call":
                    handler = new MethodCallRule();
                    break;
            }
            keywordHandlers.put(keyword, handler);
        }
        return keywordHandlers;
    }
}
