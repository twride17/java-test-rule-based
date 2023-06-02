package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreValueNode implements TreeNode {

    @Override
    public String generateCode() {
        return null;
    }

    public static int validateRule(String ruleContent) throws InvalidRuleStructureException {
        int endIndex = -1;
        //System.out.println(ruleContent);
        Matcher matcher = Pattern.compile("^(((S|s)tore\\s)?(.+))\\s(in)\\s([a-z][a-zA-Z0-9]+)").matcher(ruleContent);
        if(matcher.find()) {
            //System.out.println(matcher.group());
            String requiredSegment;
            if(ruleContent.toLowerCase().startsWith("store")) {
                endIndex = ("store ").length();
                requiredSegment = ruleContent.substring(endIndex);
            } else {
                endIndex = 0;
                requiredSegment = ruleContent;
            }

            int getValueIndex = GetValueNode.validateRule(requiredSegment);
            if(getValueIndex == -1) {
                // Invalid rule structure
                //System.out.println("Invalid rule");
                throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
            } else {
                System.out.println("Valid rule");
                if(matcher.end() == ruleContent.length()) {
                    //System.out.println("Rule found: " + ruleContent);
                    return ruleContent.length();
                } else {
                    int callIndex = endIndex + getValueIndex + (" in ").length();
                    int end = callIndex + ruleContent.substring(callIndex).indexOf(" ");
                    //System.out.println("Rule found: " + ruleContent.substring(0, end));
                    return end;
                }
            }
        } else {
            //System.out.println("Invalid rule structure detected");
            throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
        }
    }

//    public static void main(String[] args) {
//        validateRule("store value of Class.method in 1");
//        System.out.println();
//        validateRule("Store value of Class.method in variable");
//        System.out.println();
//        validateRule("Store value of Class.method in value1");
//        System.out.println();
//        validateRule("Store value of Class.method in Class.method2");
//        System.out.println();
//        validateRule("store result of Class.method: 1,2 and 3 in test");
//        System.out.println();
//        validateRule("result of Class.method with: x, y, z in test2");
//        System.out.println();
//        validateRule("store Class.method with: 0 in test");
//        System.out.println();
//        validateRule("Class.method with arguments: xValue, -10 in test");
//        System.out.println();
//        validateRule("store result of Class.method in test and expect test to equal 0");
//        System.out.println();
//        validateRule("store -100 in test");
//        System.out.println();
//        validateRule("store value of Example.x in test");
//        System.out.println();
//        validateRule("store value of xValue in test");
//    }
}
