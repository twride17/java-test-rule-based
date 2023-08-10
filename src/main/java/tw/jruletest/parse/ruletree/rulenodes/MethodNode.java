package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodNode implements TreeNode {

    private String method;
    private MethodArgumentNode arguments = null;

    @Override
    public String generateCode() {
        String code = method + "(";
        if(arguments != null) {
            code += arguments.generateCode();
        }
        return code + ")";
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        int methodCallStart = 0;
        if(ruleContent.charAt(0) == ' ') {
            throw new InvalidRuleStructureException(ruleContent, "Method Node");
        }

        if(ruleContent.substring(methodCallStart).startsWith("method ")) {
            methodCallStart += 7;
        }

        String methodCall;

        int colonIndex = ruleContent.indexOf(":");
        if(colonIndex == -1) {
            methodCall = ruleContent.substring(methodCallStart);
        } else {
            methodCall = ruleContent.substring(methodCallStart, colonIndex);
        }

        int nextSpaceIndex = methodCall.indexOf(' ');
        if(nextSpaceIndex != -1) {
            if(methodCall.charAt(nextSpaceIndex - 1) == ',') {
                methodCall = methodCall.substring(0, nextSpaceIndex-1);
            } else {
                methodCall = methodCall.substring(0, nextSpaceIndex);
            }
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(methodCall);
        if(!matcher.matches()) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        } else {
            method = methodCall;
        }

        if(colonIndex == -1) {
            int currentEnd = methodCallStart + methodCall.length();
            if(currentEnd != ruleContent.length()) {
                String remainingRule = ruleContent.substring(currentEnd);
                if(!remainingRule.startsWith(" and ") && !remainingRule.startsWith(" in ") && !remainingRule.startsWith(", ") && !remainingRule.startsWith(" then ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }
            }
            return methodCallStart + methodCall.length();
        } else {
            String middleWords = ruleContent.substring(methodCallStart + methodCall.length(), colonIndex);
            if(!middleWords.isEmpty()) {
                if(middleWords.trim().isEmpty()) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                } else {
                    String firstWord = middleWords.trim().split(" ")[0];
                    if (firstWord.equals(",") || firstWord.equals("and") || firstWord.equals("in") || firstWord.equals("then")) {
                        return methodCallStart + methodCall.length();
                    } else if (!(middleWords.equals(" with arguments") || middleWords.equals(" with"))) {
                        throw new InvalidRuleStructureException(ruleContent, "Method Node");
                    }
                }
            }

            if(colonIndex == ruleContent.length()-1) {
                throw new InvalidRuleStructureException(ruleContent, "Method Node");
            }

            arguments = new MethodArgumentNode();
            return arguments.validateRule(ruleContent.substring(colonIndex+2)) + colonIndex + 2;
        }
    }
}
