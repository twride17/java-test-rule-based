package tw.jruletest.parse.ruletree.rulenodes;

import com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane;
import tw.jruletest.parse.ruletree.TreeNode;

public class MethodNode implements TreeNode {

    @Override
    public String generateCode() {
        return null;
    }

    public static int validateRule(String ruleContent) {
        int currentEnd = ruleContent.indexOf(":");
        if(currentEnd != -1) {
            // Invalid rules will give invalid structure exception
            System.out.println("Detected arguments");
            int argumentIndex = MethodArgumentNode.validateRule(ruleContent.substring(currentEnd+1).trim());
            if(argumentIndex == -1) {
                System.out.println("Invalid arguments detected");
                return -1;
            } else {
                System.out.println("Resulting rule: " + ruleContent.substring(0, currentEnd + argumentIndex + 2));
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
                //System.out.println("Valid method structure: " + ruleContent.substring(0, nextSpaceIndex));
                if(nextSpaceIndex == -1) {
                    System.out.println("Uses full rule");
                    return ruleContent.length();
                } else {
                    System.out.println("Valid method structure: " + ruleContent.substring(0, nextSpaceIndex));
                    return nextSpaceIndex;
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid Structure");
            }
            return currentEnd;
        }
    }

    public static void main(String[] args) {
        validateRule("Call Example.method with arguments: value1, 10.5 and value3");
        System.out.println();
        validateRule("Call Example.method with: 1, 2");
        System.out.println();
        validateRule("Call Example.method: 10.5");
        System.out.println();
        validateRule("Example.method");
        System.out.println();
        validateRule("method Example.method with: value1, -100, value3 and store");
        System.out.println();
        validateRule("call method Example.method with: value1, value2, value3 and store");
        System.out.println();
        validateRule("call method Example.method with: value1, value2, value3, and store");
    }
}
