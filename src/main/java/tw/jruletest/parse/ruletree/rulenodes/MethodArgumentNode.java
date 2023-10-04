package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Parser;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.ArrayList;

/**
 * Rule node that deals with the arguments for method calls.
 *
 * @author Toby Wride
 * */

public class MethodArgumentNode implements TreeNode {

    private ArrayList<TreeNode> arguments = new ArrayList<>();

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves concatenating the results of code generation from all the arguments and separating them with commas
     *
     * @return the generated code segment for structuring a method's arguments
     * */

    @Override
    public String generateCode() {
        String code = arguments.get(0).generateCode();
        for(int i = 1; i < arguments.size(); i++) {
            code += ", " + arguments.get(i).generateCode();
        }
        return code;
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that each valid argument is followed by an appropriate connective until the end of the rule is found
     * or another connective  or keyword is found.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException if the connective between each argument is invalid or a connective is found when no extra rule exists.
     * */

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
