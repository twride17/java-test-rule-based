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
        while(!valid) {
            String connective = "";
            if(remainingRule.isEmpty() || remainingRule.equals(",") || remainingRule.equals("and")) {
                throw new InvalidRuleStructureException(remainingRule, "Method Argument Node");
            }

            int commaIndex = findConnective(remainingRule, ",");
            int andIndex = findConnective(remainingRule, " and");

            int connectiveIndex = -1;
            if(commaIndex == -1 && andIndex != -1) {
                connectiveIndex = andIndex;
                connective = " and ";
            } else if(andIndex == -1) {
                if(commaIndex != -1) {
                    connectiveIndex = commaIndex;
                    connective = ", ";
                }
            } else {
                if(commaIndex < andIndex) {
                    connectiveIndex = commaIndex;
                    connective = ", ";
                } else {
                    connectiveIndex = andIndex;
                    connective = " and ";
                }
            }

            if(connectiveIndex == -1) {
                if(remainingRule.startsWith("`") && remainingRule.charAt(remainingRule.length() - 1) == '`') {
                    valid = true;
                } else if(remainingRule.indexOf(' ') == -1) {
                    valid = true;
                } else {
                    throw new InvalidRuleStructureException(remainingRule, "Method Argument Node");
                }
                arguments.add(Argument.getArgumentNode(remainingRule));
            } else if(connectiveIndex == 0) {
                throw new InvalidRuleStructureException(remainingRule, "Method Argument Node");
            } else {
                int leftQuoteNum = countQuotes(remainingRule.substring(0, connectiveIndex));
                int rightQuoteNum = countQuotes(remainingRule.substring(connectiveIndex));

                if(leftQuoteNum == 1) {
                    if(rightQuoteNum % 2 == 0) {
                        throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                    }
                    connectiveIndex += remainingRule.substring(connectiveIndex).indexOf('`') + 1;
                    arguments.add(Argument.getArgumentNode(remainingRule.substring(0, connectiveIndex)));
                    remainingRule = remainingRule.substring(connectiveIndex);
                    if(remainingRule.isEmpty()) {
                        valid = true;
                    } else {
                        commaIndex = findConnective(remainingRule, ",");
                        andIndex = findConnective(remainingRule, " and");

                        if(commaIndex == -1) {
                            if(andIndex == -1) {
                                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                            } else if(andIndex == 0) {
                                connectiveIndex = 5;
                            } else {
                                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                            }
                        } else if(andIndex == -1) {
                            if(commaIndex == 0) {
                                connectiveIndex = 2;
                            } else {
                                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                            }
                        } else {
                            if(commaIndex == 0) {
                                connectiveIndex = 2;
                            } else if(andIndex == 0) {
                                connectiveIndex = 5;
                            } else {
                                throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                            }
                        }

                        remainingRule = remainingRule.substring(connectiveIndex);
                    }
                } else if(leftQuoteNum <= 2){
                    arguments.add(Argument.getArgumentNode(remainingRule.substring(0, connectiveIndex)));
                    // TODO add argument object to list
                    connectiveIndex += connective.length();
                    remainingRule = remainingRule.substring(connectiveIndex);
                } else {
                    throw new InvalidRuleStructureException(ruleContent, "Method Argument Node");
                }

                int nextSpaceIndex = remainingRule.indexOf(' ');
                if(nextSpaceIndex != -1) {
                    if(Parser.KEYWORDS.contains(remainingRule.substring(0, nextSpaceIndex))) {
                        valid = true;
                    }
                }
            }
        }
        return ruleContent.length();
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
        testValid("-0.612f, value2 and `hello`");
        testValid("value1 and `hello it's me` and -90");
    }

    public static void testValid(String rule) {
        try {
            MethodArgumentNode n = new MethodArgumentNode();
            System.out.println(rule);
            n.validateRule(rule);
            n.showArguments();
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }
}
