package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMember;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodNode implements TreeNode {

    private SourceMethod method;

    private String methodName;
    private MethodArgumentNode arguments = null;

    @Override
    public String generateCode() {
        String code = methodName + "(";
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
            methodName = methodCall;
        }

        // Test method call exists
        SourceMember method;
        try {
            method = JavaClassAnalyzer.identifySourceClass(methodCall.split("\\.")[0]).getMember(methodCall.split("\\.")[1]);
            if(method instanceof SourceMethod) {
                this.method = (SourceMethod) method;
            } else {
                throw new InvalidRuleStructureException(methodCall, "Method Node");
            }
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
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

    public Type getType() {
        return method.getType();
    }

    public String getMethodName() {
        return method.getName();
    }

    public SourceMethod getMethod() {
        return method;
    }
}
