package tw.jruletest.parse.ruletree.innernodes.valuenodes;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with getting values from methods, fields, variables and arguments.
 * This node is designed to act as an intermediary between nodes which require values of methods, fields, variables and
 * constants, and method, field and argument nodes, in order to simplify selection of the appropriate node.
 *
 * @author Toby Wride
 * */

public class ValueNode extends ChildNode implements Rule {

    private ChildNode valueSourceNode;

    /**
     * Implementation of code generation from TreeNode interface.
     * Code generation completely relies on the generation of code from the chosen child node.
     *
     * @return the generated code segment for getting the value of a method, field, variable.
     * */

    @Override
    public String generateCode() {
        return ((Rule)valueSourceNode).generateCode();
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * First check is that the basic structure is valid for the start (or all) of the rule.
     * The next check is to determine if the rule starts with a valid combination of the keywords 'value', 'result' and 'of'.
     * Assuming these validations pass, the validity of the rule is now determined by the appropriate child node.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the basic structure does not start with 'value of', 'result of' or no keywords.
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        // REMOVE???
        Pattern regex = Pattern.compile("^((value|result)\\sof\\s)?(.+)");
        Matcher matcher = regex.matcher(ruleContent);

        int currentEnd = 0;
        String nextSegment = ruleContent;
//        if(matcher.find()) {
//            if(nextSegment.startsWith("of ") || (nextSegment.equals("of"))) {
//                throw new InvalidRuleStructureException(ruleContent, "Value Node");
//            } else if(nextSegment.startsWith("value ") || nextSegment.startsWith("result ")) {
//                currentEnd += nextSegment.indexOf(' ') + 1;
//                nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
//                if(!nextSegment.startsWith("of ")) {
//                    throw new InvalidRuleStructureException(ruleContent, "Value Node");
//                } else {
//                    currentEnd += nextSegment.indexOf(' ') + 1;
//                    nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
//                }
//            } else if(nextSegment.equals("value") || nextSegment.equals("result")) {
//                throw new InvalidRuleStructureException(ruleContent, "Value Node");
//            }
//        }

        if(nextSegment.startsWith("of ") || (nextSegment.equals("of"))) {
            throw new InvalidRuleStructureException("Value Node", "Keyword 'of' found at start of rule. If using phrase " +
                                                                    "'value/result of', all of the phrase is expected");
        } else if(nextSegment.startsWith("value ") || nextSegment.startsWith("result ")) {
            currentEnd += nextSegment.indexOf(' ') + 1;
            nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
            if(!nextSegment.startsWith("of ")) {
                throw new InvalidRuleStructureException("Value Node", "Keyword 'of' expected after 'value' or 'result' but not found. " +
                                                        "If using phrase 'value/result of', all of the phrase is expected");
            } else {
                currentEnd += nextSegment.indexOf(' ') + 1;
                nextSegment = nextSegment.substring(nextSegment.indexOf(' ') + 1);
            }
        } else if(nextSegment.equals("value") || nextSegment.equals("result")) {
            throw new InvalidRuleStructureException("Value Node", "Only the keyword 'value' or 'result' was found in the remaining rule");
        }

        try {
            valueSourceNode = RuleNode.getChildNode(nextSegment, RuleNode.VALUE_RETRIEVAL_NODE);
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Value Node", "Failed to find valid node for rule: '" + nextSegment + "':", e);
        }
        endIndex = currentEnd + valueSourceNode.getEndIndex();
    }

    /**
     * Gets the type from the selected value source node
     *
     * @return the type of the method, field, variable
     * */

    public Type getType() {
        return valueSourceNode.getType();
    }

    /**
     * Gets the name of the value's source from the selected value source node
     *
     * @return the name of the method, field, variable
     * */

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
