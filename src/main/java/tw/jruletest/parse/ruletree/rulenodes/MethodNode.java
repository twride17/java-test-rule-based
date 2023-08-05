package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodNode implements TreeNode {

    private MethodArgumentNode arguments = null;

    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        int methodCallStart = 0;

        if(ruleContent.toLowerCase().startsWith("call ")) {
            methodCallStart += 5;
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
            methodCall = methodCall.substring(0, nextSpaceIndex);
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]+)\\.([a-z][A-Z0-9a-z]+)").matcher(methodCall);
        if(!matcher.matches()) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        }

        if(colonIndex == -1) {
            int currentEnd = methodCallStart + methodCall.length();
            if(currentEnd != ruleContent.length()) {
                String remainingRule = ruleContent.substring(currentEnd);
                if(!remainingRule.startsWith(" and ") && !remainingRule.startsWith(" in ") && !remainingRule.startsWith(", ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }
            }
            return methodCallStart + methodCall.length();
        } else {
            String middleWords = ruleContent.substring(methodCallStart + methodCall.length(), colonIndex);
            if(!middleWords.isEmpty()) {
                if (!(middleWords.equals(" with arguments") || middleWords.equals(" with"))) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }
            }

            if(colonIndex == ruleContent.length()-1) {
                throw new InvalidRuleStructureException(ruleContent, "Method Node");
            }

            arguments = new MethodArgumentNode();
            return arguments.validateRule(ruleContent.substring(colonIndex+2)) + colonIndex + 2;
        }
    }

    public static void testValid(String rule) {
        try {
            MethodNode n = new MethodNode();
            System.out.println(rule);
            System.out.println(n.validateRule(rule));
            System.out.println(rule.length());
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        testValid("Call Example.method with arguments: value1, 10.5 and value3");
        testValid("Call Example.method with: 1, 2");
        testValid("Call Example.method: 10.5");
        testValid("Example.method");
        testValid("method Example.method with: value1, -100, value3 and store");
        testValid("call method Example.method with: value1, value2, value3 and store");
        testValid("call method Example.method with: value1, value2, value3, and store");
        testValid("call method Example.method with: `This and this`, value2, value3 and store");
        testValid("call method Example.method with: value1, value2, `Hello, it's me` and get value of");
        testValid("call method Example.method with: value1 and `Lol, value3");
        testValid("call method method with: value1 and `Lol`, value3");
        testValid("call method Example.method with value1 and `Lol`, value3");
        testValid("call method Example.method with arguments value1 and `Lol`, value3");
        testValid("call Example.method:");
    }
}
