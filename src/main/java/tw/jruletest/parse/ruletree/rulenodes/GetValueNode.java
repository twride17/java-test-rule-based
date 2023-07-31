package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

public class GetValueNode implements TreeNode {

    private TreeNode callTree;

    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        int currentEnd;
        String[] segments = ruleContent.split(" ");
        if(segments[0].equalsIgnoreCase("get")) {
            if((segments[1].equals("value") || segments[1].equals("result")) && segments[2].equals("of")) {
                currentEnd = ("get of ").length() + segments[1].length() + 1;
            } else if(!segments[1].equals("of") && !segments[2].equals("of")) {
                currentEnd = ("get").length();
            } else {
                throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
            }
        } else if((segments[0].equals("value") || segments[0].equals("result")) && segments[1].equals("of")) {
            currentEnd = ("of ").length() + segments[0].length() + 1;
        } else if(!segments[1].equals("of") && !segments[2].equals("of")) {
            currentEnd = 0;
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
        }

        String valueCall = ruleContent.substring(currentEnd).trim();
        int possibleMethodIndex = (new MethodNode()).validateRule(valueCall);
        if(possibleMethodIndex == -1) {
            throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
        } else {
            return possibleMethodIndex + currentEnd;
        }
    }

    public static void testValid(String rule) {
        try {
            GetValueNode n = new GetValueNode();
            System.out.println(rule);
            n.validateRule(rule);
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
        testValid("x and store");
        testValid("Get of x");
    }
}
