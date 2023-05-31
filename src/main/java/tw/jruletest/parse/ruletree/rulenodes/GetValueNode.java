package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.parse.ruletree.TreeNode;

public class GetValueNode implements TreeNode {

    //private static final String[] POSSIBLE_STARTERS = {"get", "value of ", "result of "};

    @Override
    public String generateCode() {
        return null;
    }

    public static int validateRule(String ruleContent) {
        System.out.println(ruleContent);
        int currentEnd;
        String[] segments = ruleContent.split(" ");
        if(segments[0].equalsIgnoreCase("get")) {
            if((segments[1].equals("value") || segments[1].equals("result")) && segments[2].equals("of")) {
                currentEnd = ("get of ").length() + segments[1].length() + 1;
            } else if(!segments[1].equals("of") && !segments[2].equals("of")) {
                currentEnd = ("get").length();
            } else {
                // Should be invalid rule structure
                System.out.println("Invalid rule");
                return -1;
            }
        } else if((segments[0].equals("value") || segments[0].equals("result")) && segments[1].equals("of")) {
            currentEnd = ("of ").length() + segments[0].length() + 1;
        } else if(!segments[1].equals("of") && !segments[2].equals("of")) {
            currentEnd = 0;
        } else {
            // Should be invalid rule structure
            System.out.println("Invalid rule");
            return -1;
        }

        String valueCall = ruleContent.substring(currentEnd).trim();
        System.out.println(valueCall);
        int possibleMethodIndex = currentEnd + MethodNode.validateRule(valueCall);
        if(possibleMethodIndex == -1) {
            // Should be invalid rule structure
            System.out.println("Invalid rule");
            return -1;
        } else {
            System.out.println("Rule collected: " + ruleContent.substring(0, possibleMethodIndex));
            return possibleMethodIndex;
        }
    }

    public static void main(String[] args) {
        validateRule("Get value of x");
        System.out.println();
        validateRule("value of x");
        System.out.println();
        validateRule("Get value of Class.method with: 2 and -75.2, 63");
        System.out.println();
        validateRule("Get result of Class.method and store in y");
        System.out.println();
        validateRule("x and store");
        System.out.println();
        validateRule("Get of x");
    }
}
