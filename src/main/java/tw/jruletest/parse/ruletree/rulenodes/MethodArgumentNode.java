package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Parser;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.ArrayList;

public class MethodArgumentNode implements TreeNode {

    private ArrayList<TreeNode> arguments = new ArrayList<>();

    @Override
    public String generateCode() {
        String code = arguments.get(0).generateCode();
        for(int i = 1; i < arguments.size(); i++) {
            code += ", " + arguments.get(i).generateCode();
        }
        return code;
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

                        if(Parser.KEYWORDS.contains(nextWord.toLowerCase())) {
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
}
