package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class
ValueNode implements TreeNode {

    private TreeNode valueSourceNode;

    @Override
    public String generateCode() {
        return valueSourceNode.generateCode();
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Pattern regex = Pattern.compile("^((value|result)\\sof\\s)?(.+)");
        Matcher matcher = regex.matcher(ruleContent);

        int currentEnd = 0;
        String nextSegment = ruleContent;
        if(matcher.find()) {
            if(nextSegment.startsWith("of ") || (nextSegment.equals("of"))) {
                throw new InvalidRuleStructureException(ruleContent, "Value Node");
            } else if(nextSegment.startsWith("value ") || nextSegment.startsWith("result ")) {
                currentEnd += nextSegment.indexOf(' ') + 1;
                nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
                if(!nextSegment.startsWith("of ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Value Node");
                } else {
                    currentEnd += nextSegment.indexOf(' ') + 1;
                    nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
                }
            } else if(nextSegment.equals("value") || nextSegment.equals("result")) {
                throw new InvalidRuleStructureException(ruleContent, "Value Node");
            }
        }

        valueSourceNode = new MethodNode();
        try {
            return valueSourceNode.validateRule(nextSegment) + currentEnd;
        } catch(InvalidRuleStructureException e) {
            try {
                valueSourceNode = new FieldNode();
                return valueSourceNode.validateRule(nextSegment) + currentEnd;
            } catch(InvalidRuleStructureException e1) {
                try {
                    valueSourceNode = new VariableNode();
                    return currentEnd + valueSourceNode.validateRule(nextSegment);
                } catch(InvalidRuleStructureException e2) {
                    throw new InvalidRuleStructureException(nextSegment, "Value Node");
                }
            }
        }
    }

    public Type getType() {
        if(valueSourceNode instanceof MethodNode) {
            return ((MethodNode) valueSourceNode).getType();
        } else if(valueSourceNode instanceof FieldNode) {
            return ((FieldNode) valueSourceNode).getType();
        } else {
            return ((VariableNode) valueSourceNode).getType();
        }
    }

    public String getCallName() {
        if(valueSourceNode instanceof MethodNode) {
            return ((MethodNode) valueSourceNode).getMethodName();
        } else if(valueSourceNode instanceof FieldNode) {
            return ((FieldNode) valueSourceNode).getFieldName();
        } else {
            return ((VariableNode) valueSourceNode).getArgument();
        }
    }
}
