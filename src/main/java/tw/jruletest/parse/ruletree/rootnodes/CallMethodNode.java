package tw.jruletest.parse.ruletree.rootnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.valuenodes.MethodNode;

/**
 * Rule node that deals with calling source methods.
 * This node is designed to be the root node of a tree generated from rules that start with the keyword 'call'.
 *
 * @author Toby Wride
 * */

public class CallMethodNode extends RuleNode implements Rule {

    private MethodNode methodNode;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves code generation from the child node (likely a MethodNode object) and adding a semicolon at the end so compilation can succeed.
     *
     * @return a full line of code containing the generated method calling code.
     * */

    @Override
    public String generateCode() {
        return methodNode.generateCode() + ";";
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks if the first word is the keyword 'call' then validity is dependent on the validity of the rest of the rule.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not start with the 'call' keyword or if the rest of the rule could not be validated.
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        if(ruleContent.toLowerCase().startsWith("call ")) {
            methodNode = new MethodNode();
            methodNode.validateRule(ruleContent.substring(5));
            endIndex = 5 + methodNode.getEndIndex();
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Method Call Node");
        }
    }
}
