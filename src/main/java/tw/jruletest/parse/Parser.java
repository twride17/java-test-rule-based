package tw.jruletest.parse;

import com.sun.corba.se.impl.orbutil.ObjectUtility;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.rules.*;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.rulenodes.ExpectationNode;

import java.util.*;


public class Parser {

    /**
     * @author Toby Wride
     *
     * Parses the rules from a test case
     */

    private static final String[] KEYWORDS = {"call", "get", "store", "expect"};
    private static final String[] POSSIBLE_CONNECTIVES = {"and", "then"};
    private static final HashMap<String, Rule> KEYWORD_HANDLERS = mapKeywordsToHandlers();

    private static LinkedList<TreeNode> rules = new LinkedList<>();

    public static String parseRules(String[] rules) {
        String generatedCode = "";
        for(String rule: rules) {
            generatedCode += parseRule(rule);
        }
        return generatedCode;
    }

    public static String parseRule(String rule) {
        String[] subRules = rule.split("\\. ");
        String codeBlock = "";
        for(String subRule: subRules) {
            try {
                generateTrees(subRule);
                for (TreeNode ruleNode : rules) {
                    codeBlock += ruleNode.generateCode() + "\n";
                }
            } catch(UnparsableRuleException e) {
                e.printError();
            }
            rules = new LinkedList<>();
        }
        return codeBlock;
    }

    private static void generateTrees(String rule) throws UnparsableRuleException {
        //System.out.println(rule);
        //System.out.println(rule.length());

        ArrayList<String> subRules = new ArrayList<>();
        String startingCommand = rule.split(" ")[0];
        int currentEndIndex = getEndOfSubRule(rule, startingCommand);
        //System.out.println(currentEndIndex);

        subRules.add(rule.substring(0, currentEndIndex).trim());
        String remainingRule = rule.substring(currentEndIndex).trim();

        do {
            try {
                //System.out.println(remainingRule);
                int connectiveIndex = -1;
                String foundConnective = null;
                if (remainingRule.charAt(0) == ',') {
                    connectiveIndex = 2;
                } else {
                    int connectiveListIndex = 0;

                    do {
                        if(remainingRule.split(" ")[0].equals(POSSIBLE_CONNECTIVES[connectiveListIndex])) {
                            foundConnective = POSSIBLE_CONNECTIVES[connectiveListIndex];
                        } else {
                            connectiveListIndex++;
                        }
                    } while (foundConnective == null && connectiveListIndex < POSSIBLE_CONNECTIVES.length);
                }

                if(foundConnective != null) {
                    connectiveIndex = foundConnective.length() + 1;
                    //currentEndIndex = connectiveIndex;
                }

                remainingRule = remainingRule.substring(connectiveIndex);
                String nextWord = remainingRule.split(" ")[0];
                if((new ArrayList<>(Arrays.asList(KEYWORDS))).contains(nextWord)) {
                    currentEndIndex = getEndOfSubRule(remainingRule, nextWord);
                    subRules.add(remainingRule.substring(0, currentEndIndex).trim());
                } else {
                    currentEndIndex = getEndOfSubRule(remainingRule, startingCommand);
                    subRules.add(startingCommand + " " + remainingRule.substring(0, currentEndIndex).trim());
                }
                //System.out.println(currentEndIndex);
                remainingRule = remainingRule.substring(currentEndIndex).trim();

                //System.out.println(remainingRule);
            } catch (StringIndexOutOfBoundsException e) {
                // Invalid rule structure exception???
                if(currentEndIndex < rule.length()) {
                    throw new UnparsableRuleException(rule);
                }
            }
        } while(!remainingRule.isEmpty());

        System.out.println("OUTPUT:");
        for(String subRule: subRules) {
            System.out.println(subRule);
        }

        // Generate the trees and add into list
    }

    private static int getEndOfSubRule(String rule, String startCommand) throws UnparsableRuleException {
        switch(startCommand.toLowerCase()) {
            case "expect":
                return ExpectationNode.validateRule(rule);
            default:
                throw new UnparsableRuleException(rule);
        }
    }

//    public static String parseRule(String rule) {
//        String codeBlock = "";
//        ArrayList<String> ruleSegments = getRuleSegments(rule);
//        // Currently sequential commands
//        // TODO deal with different control flows
//        try {
//            for (String segment : ruleSegments) {
//                String keyword = segment.split(" ")[0];
//                String remains = segment.substring(segment.indexOf(" "));
//                codeBlock += KEYWORD_HANDLERS.get(keyword).decodeRule(remains) + ";\n";
//            }
//        } catch(UnparsableRuleException e) {
//            e.printError();
//        }
//        return codeBlock;
//    }

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
                case "Expect":
                    handler = new ExpectationRule();
                    break;
                case "Store":
                    handler = new StoreValueRule();
            }
            keywordHandlers.put(keyword, handler);
        }
        return keywordHandlers;
    }

    private static void testRule(String rule) {
        try {
            generateTrees(rule);
        } catch(UnparsableRuleException e) {
            System.out.println("Detected unparsable rule:");
            e.printError();
        }
        System.out.println("##############");
    }

    public static void main(String[] args) {
        testRule("Expect value of x to equal 0");
        testRule("Expect value of x to equal 0 and expect value of y to not equal 1");
        testRule("Expect value of x and y to equal 0");
        testRule("Expect value of x to equal 0, value of y to equal 2 and value of t to equal 4");
        testRule("Expect value of x, y and z to not equal 0");
    }
}