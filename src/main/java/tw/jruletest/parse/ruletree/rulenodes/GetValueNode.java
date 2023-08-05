package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetValueNode implements TreeNode {

    private TreeNode callTree;

    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Pattern regex = Pattern.compile("(((G|g)et\\s)?(value|result)\\sof\\s)?(.+)");
        Matcher matcher = regex.matcher(ruleContent);

        int currentEnd = 0;
        String nextSegment = ruleContent;
        if(matcher.find()) {
            if(nextSegment.toLowerCase().startsWith("get ")) {
                nextSegment = nextSegment.substring(4);
                currentEnd += 4;
            }

            if(nextSegment.startsWith("of ") || (nextSegment.equals("of"))) {
                throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
            } else if(nextSegment.startsWith("value ") || nextSegment.startsWith("result ")) {
                currentEnd += nextSegment.indexOf(' ') + 1;
                nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
                if(!nextSegment.startsWith("of ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
                } else {
                    currentEnd += nextSegment.indexOf(' ') + 1;
                    nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
                }
            } else if(nextSegment.equals("value") || nextSegment.equals("result")) {
                throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
            }
        }

        callTree = new MethodNode();
        try {
            return callTree.validateRule(nextSegment) + currentEnd;
        } catch(InvalidRuleStructureException e) {
            try {
                callTree = new VariableNode();
                return currentEnd + callTree.validateRule(nextSegment);
            } catch(InvalidRuleStructureException e2) {
                throw new InvalidRuleStructureException(nextSegment, "Get Value Node");
            }
        }
    }

    public static void testValid(String rule) {
        try {
            GetValueNode n = new GetValueNode();
            System.out.println(rule);
            System.out.println(n.validateRule(rule));
            System.out.println(rule.length());
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        testValid("Get value of x");
        testValid("value of x");
        testValid("Get value of Class.method with: 2 and -75.2, 63");
        testValid("Get value of Class.method with arguments: `This is great, lol` and `Hello`, 63");
        testValid("Get value of Class.method: 2 and -75.2, 63");
        testValid("Get value of Class.method with: 2 and -75.2, Lol`");
        testValid("Get value of Class.method with: `New string and new test`, value3 and 63 and store in z");
        testValid("Get value of Class.method with: `New string and new test`, value3 and `Hello` and store in z");
        testValid("Get result of Class.method and store in y");
        testValid("Get result of Class.method: and store in y");
        testValid("x and store");
        testValid("Get of x");
        testValid("Get of");
        testValid("Get value");
        testValid("Get result");
        testValid("Get result of 1");
        testValid("Get result of 'Hello'");
        testValid("Get result of xxx");
        testValid("Get value of");
    }
}
