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
        return callTree.generateCode();
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
            } else if(nextSegment.equals("get") || nextSegment.equals("Get")) {
                throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
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
}
