package tw.jruletest.parse.ruletree.innernodes;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Parser;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;

import java.util.ArrayList;

/**
 * Rule node that deals with the arguments for method calls.
 *
 * @author Toby Wride
 * */

public class MethodArgumentNode extends RuleNode implements Rule {

    private ArrayList<ChildNode> arguments = new ArrayList<>();

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves concatenating the results of code generation from all the arguments and separating them with commas
     *
     * @return the generated code segment for structuring a method's arguments
     * */

    @Override
    public String generateCode() {
        String code = ((Rule)arguments.get(0)).generateCode();
        for(int i = 1; i < arguments.size(); i++) {
            code += ", " + ((Rule)arguments.get(i)).generateCode();
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

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        boolean valid = false;
        String remainingRule = ruleContent;
        int currentEnd = 0;

        do {
            if(remainingRule.isEmpty()) {
                throw new InvalidRuleStructureException("Method Argument Node", "Found empty rule after validation of arguments in '" +ruleContent + "'");
            } else if(remainingRule.equals("and") || remainingRule.equals(",") || remainingRule.equals("and ") || remainingRule.equals(", ")) {
                throw new InvalidRuleStructureException("Method Argument Node", "Remaining rule after argument validation of '"
                                                        + ruleContent.substring(0, currentEnd+1) + "' contains a connective when not expected to");
            }

            ChildNode argumentNode;
            try {
                argumentNode = RuleNode.getChildNode(remainingRule, RuleNode.CHILD_NODE);
            } catch(ChildNodeSelectionException e) {
                throw new InvalidRuleStructureException("Method Argument Node: ", "'" + remainingRule + "' failed because of: ", e);
            }
            int argumentEndIndex = argumentNode.getEndIndex();
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
                    endIndex = currentEnd;
                    return;
                } else {
                    throw new InvalidRuleStructureException("Method Argument Node", "No valid argument connective (',' or 'and') " +
                                                            "or valid rule connective ('in' or 'then') found after argument in: '" + remainingRule + "'");
                }

//                if(remainingRule.length() == connective.length()) {
//                    throw new InvalidRuleStructureException("Method Argument Node", "");
//                }

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
                        throw new InvalidRuleStructureException("Method Argument Node", "Expected a space after connective in: " + remainingRule + "'");
                    }
                } catch(IndexOutOfBoundsException e) {
                    throw new InvalidRuleStructureException("Method Argument Node", "Rule content after '" + ruleContent.substring(0 ,currentEnd)
                                                                                        + "' was empty when not expected to be");
                }
            }
        } while(!valid);

        endIndex = currentEnd;
    }

    public ArrayList<ChildNode> getArguments() {
        return arguments;
    }

    public void setArgumentSubset(int numToKeep) {
        int totalArguments = arguments.size();
        for(int i = 0; i < totalArguments - numToKeep; i++) {
            arguments.remove(arguments.size()-1);
        }
    }
}
