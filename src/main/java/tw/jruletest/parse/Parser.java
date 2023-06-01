package tw.jruletest.parse;

import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.parse.rules.*;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.rulenodes.ExpectationNode;
import tw.jruletest.parse.ruletree.rulenodes.GetValueNode;
import tw.jruletest.parse.ruletree.rulenodes.MethodNode;
import tw.jruletest.parse.ruletree.rulenodes.StoreValueNode;

import java.util.*;


public class Parser {

    /**
     * @author Toby Wride
     *
     * Parses the rules from a test case
     */

    public static final ArrayList<String> KEYWORDS = new ArrayList<>(Arrays.asList("call", "get", "store", "expect"));
    private static final String[] POSSIBLE_CONNECTIVES = {"and", "then"};

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
                if(KEYWORDS.contains(nextWord)) {
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
            case "get":
                return GetValueNode.validateRule(rule);
            case "store":
                return StoreValueNode.validateRule(rule);
            case "call":
                return MethodNode.validateRule(rule);
            default:
                throw new UnparsableRuleException(rule);
        }
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
        testRule("Expect value of x, y and z to not equal 0");
        testRule("Get value of Class.method");
        testRule("get value of Class.method with arguments: 1, 2 and 3");
        testRule("Get result of Class.method: 12 and expect Class.x to equal 12");
        testRule("Expect Class.x to equal 12, get value of Class.method and expect Class.method2 to equal 10.6");
        testRule("Call Class.method and store value of Example.x in xValue");
        testRule("Store value of Class.method with arguments: 1 and 2, -90.23 in value and expect value to equal 0");
        testRule("Call Class.method and get value of Example.x and store value in value2");
    }
}