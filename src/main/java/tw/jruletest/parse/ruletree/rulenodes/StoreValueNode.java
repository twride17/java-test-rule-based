package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreValueNode implements TreeNode {

    private TreeNode valueTree;

    // TODO Likely to be a variable node, when implemented
    private String variableTree;


    @Override
    public String generateCode() {
        return null;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^(((S|s)tore\\s)?(.+))\\s(in)\\s([a-z][a-zA-Z0-9]+)").matcher(ruleContent);
        if(matcher.find()) {
            String requiredSegment;
            int endIndex;
            if(ruleContent.toLowerCase().startsWith("store")) {
                endIndex = ("store ").length();
                requiredSegment = ruleContent.substring(endIndex);
            } else {
                endIndex = 0;
                requiredSegment = ruleContent;
            }

            requiredSegment = requiredSegment.substring(0, requiredSegment.indexOf(" in "));

            try {
                int getValueIndex = (new GetValueNode()).validateRule(requiredSegment);
                if(matcher.end() == ruleContent.length()) {
                    return ruleContent.length();
                } else {
                    int callIndex = endIndex + getValueIndex + (" in ").length();
                    return callIndex + ruleContent.substring(callIndex).indexOf(" ");
                }
            } catch(ArrayIndexOutOfBoundsException e) {
                Argument.getArgumentNode(requiredSegment);
                if(matcher.end() == ruleContent.length()) {
                    return ruleContent.length();
                } else {
                    return matcher.end() + 1;
                }
            }
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
        }
    }

    public static void testValid(String rule) {
        try {
            StoreValueNode n = new StoreValueNode();
            System.out.println(rule);
            n.validateRule(rule);
        } catch(InvalidRuleStructureException e) {
            e.printError();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        testValid("store value of Class.method in 1");
        testValid("Store value of Class.method in variable");
        testValid("Store value of Class.method in value1");
        testValid("Store value of Class.method in Class.method2");
        testValid("store result of Class.method: 1, 2 and 3 in test");
        testValid("result of Class.method with: x, y, z in test2");
        testValid("store Class.method with: 0 in test");
        testValid("store Class.method in test");
        testValid("Class.method with arguments: -10.5f in test");
        testValid("Class.method with arguments: `This is a string, it's cool` and 0.65 in test");
        testValid("Class.method with arguments: `This is a cool and short string` in test");
        testValid("Class.method with arguments: xValue, `-36.5f`, `hello in test");
        testValid("Class.method with arguments: xValue, `-36.5f`, `hello` in 10test");
        testValid("store result of Class.method in test and expect test to equal 0");
        testValid("store -100 in test");
        testValid("store value of Example.x in test");
        testValid("store value of xValue in test");
        testValid("store value of xValue in");
        testValid("store value of xValue test");
    }
}
