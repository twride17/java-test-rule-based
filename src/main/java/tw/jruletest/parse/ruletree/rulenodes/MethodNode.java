package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class MethodNode implements TreeNode {

    private MethodArgumentNode arguments = null;

    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        int currentEnd = ruleContent.indexOf(":");
        if(currentEnd != -1) {
            // Invalid rules will give invalid structure exception
            int argumentIndex = (new MethodArgumentNode()).validateRule(ruleContent.substring(currentEnd+1).trim());
            if(argumentIndex == -1) {
                throw new InvalidRuleStructureException(ruleContent, "Method Call Node");
            } else {
                return currentEnd + argumentIndex + 2;
            }
        } else {
            String[] ruleSegments = ruleContent.split(" ");
            try {
                if (ruleSegments[0].equalsIgnoreCase("call")) {
                    if (ruleSegments[1].equals("method")) {
                        currentEnd = ("call method").length() + 1;
                    } else {
                        currentEnd = ("call").length() + 1;
                    }
                } else if (ruleSegments[0].equals("method")) {
                    currentEnd = ("method").length() + 1;
                } else {
                    currentEnd = 0;
                }

                int nextSpaceIndex = ruleContent.substring(currentEnd).indexOf(" ");
                if(nextSpaceIndex == -1) {
                    return ruleContent.length();
                } else {
                    return nextSpaceIndex + currentEnd;
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                throw new InvalidRuleStructureException(ruleContent, "Method Call Node");
            }
        }
    }

    public static void testValid(String rule) {
        try {
            MethodNode n = new MethodNode();
            System.out.println(rule);
            n.validateRule(rule);
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
    }
}
