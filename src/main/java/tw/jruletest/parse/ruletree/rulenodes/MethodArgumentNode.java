package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Parser;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.ArrayList;

public class MethodArgumentNode implements TreeNode {

    // TODO Change to nodes when updated
    private ArrayList<TreeNode> arguments = new ArrayList<>();

    public MethodArgumentNode() {}

    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        boolean valid = false;
        String remainingRule = ruleContent;
        int currentEnd = 0;

        do {
            if(remainingRule.isEmpty()) {
                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
            } else if(remainingRule.equals("and") || remainingRule.equals(",") || remainingRule.equals("and ") || remainingRule.equals(", ")) {
                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
            }

            TreeNode argumentNode = Argument.getArgumentNode(remainingRule);
            int argumentEndIndex = argumentNode.generateCode().length();
            currentEnd += argumentEndIndex;
            arguments.add(argumentNode);

            if(argumentEndIndex == remainingRule.length()) {
                valid = true;
            } else {
                remainingRule = remainingRule.substring(argumentEndIndex);

                String connective;
                if(remainingRule.startsWith(" and")) {
                    connective = " and";
                } else if(remainingRule.startsWith(",")) {
                    connective = ",";
                } else if(remainingRule.startsWith(" in") || remainingRule.startsWith(" then")) {
                    return currentEnd;
                } else {
                    throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                }

                if(remainingRule.length() == connective.length()) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                }

                remainingRule = remainingRule.substring(connective.length());

                try {
                    if(remainingRule.charAt(0) == ' ') {
                        int nextSpaceIndex = remainingRule.substring(1).indexOf(' ');
                        String nextWord;
                        if(nextSpaceIndex == -1) {
                            nextWord = remainingRule.substring(1);
                        } else {
                            nextWord = remainingRule.substring(1, nextSpaceIndex + 1);
                        }

                        if(Parser.KEYWORDS.contains(nextWord)) {
                            valid = true;
                        } else {
                            currentEnd += connective.length() + 1;
                            remainingRule = remainingRule.substring(1);
                        }
                    } else {
                        throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                    }
                } catch(IndexOutOfBoundsException e) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                }
            }
        } while(!valid);

        return currentEnd;
    }

    private int findConnective(String rule, String connective) throws InvalidRuleStructureException {
        int index = rule.indexOf(connective);
        if(index != -1) {
            index += connective.length();
            if(index == rule.length() || rule.charAt(index) != ' ') {
                throw new InvalidRuleStructureException(rule, "Method Argument Node");
            } else {
                return index - connective.length();
            }
        } else {
            return index;
        }
    }

    private int countQuotes(String segment) {
        int numQuotes = 0;
        for(Character character: segment.toCharArray()) {
            if(character == '`') {
                numQuotes ++;
            }
        }
        return numQuotes;
    }

    public void showArguments() {
        String argumentList = "Arguments:";
        for(TreeNode node: arguments) {
            argumentList += " " + node.generateCode();
        }
        System.out.println(argumentList);
    }

    public static void main(String[] args) {
        // All work properly
        testValid("value1, 10.5 and value3");
        testValid("1v, value2, value3");
        testValid("value1 and store dummy");
        testValid("value1, value2,");
        testValid("value1, value2, 4");
        testValid("value1, value2 value3");
        testValid("value1, value2 value3 value4");
        testValid("value1 and -65.78 and value3");
        testValid("and value1 and -65.78 and value3");
        testValid("value1 and value2 and");
        testValid("value1, value2 and and");
        testValid("v, and !");
        testValid("`This is a string`");
        testValid("`This is a string and hello's a good word, probably`, 32 and x");
        testValid("`Hello world` and 6, value1");
        testValid("value1, value2 and `hello");
        testValid("value1, value2 and `hello`");
        testValid("value1, value2 and hello`");
        testValid("-0.612f, value2 and `hello`");
        testValid("value1 and `hello it's me` and -90");
    }

    public static void testValid(String rule) {
        try {
            MethodArgumentNode n = new MethodArgumentNode();
            System.out.println(rule);
            System.out.println(n.validateRule(rule));
            System.out.println(rule.length());
            n.showArguments();
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }
}
