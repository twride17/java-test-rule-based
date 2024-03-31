package tw.jruletest.parse.ruletree.rootnodes;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;
import tw.jruletest.parse.ruletree.innernodes.valuenodes.VariableNode;
import tw.jruletest.variables.Variable;
import tw.jruletest.variables.VariableStore;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with storing the value of fields, variables or method calls in a specified variable.
 * This node is designed to be the root node of a tree generated from rules that start with the keyword 'store'.
 *
 * @author Toby Wride
 * */

public class StoreValueNode extends RootNode implements Rule {

    private ChildNode valueTree;
    private VariableNode variableTree;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves determining the required type and assigning the value to a variable with a specified name.
     *
     * @return a full line of code with an identifier, variable name and method of reading the required value.
     * */

    @Override
    public String generateCode() {
        String variableName = variableTree.getArgument();
        String code = variableName + " = " + ((Rule)valueTree).generateCode() + ";";

        Variable variable = VariableStore.findVariable(Runner.getCurrentMethod(), variableName);
        if(!variable.isDeclared()) {
            code = TypeIdentifier.getType(getType()) + " " + code;
            variable.makeDeclared();
        }

        return code;
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that the rule starts with the keyword 'store' and contains the word 'in'. The remaining rule is then validated by the appropriate child node.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException if the rule does not start with the 'store' keyword or the 'in' connective
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        if(ruleContent.isEmpty()) {
            throw new InvalidRuleStructureException("Store Value Node", "Rule must not be empty after Store keyword");
        }

        try {
            valueTree = RuleNode.getChildNode(ruleContent, RuleNode.CHILD_NODE);
        } catch(ChildNodeSelectionException e) {
            throw new InvalidRuleStructureException("Store Value Node", "Caused by:", e);
        }
        endIndex += valueTree.getEndIndex();

        String requiredSegment = ruleContent.substring(endIndex);
        if(!(requiredSegment.startsWith(" in") && !requiredSegment.trim().equals("in"))) {
            throw new InvalidRuleStructureException("Store Value Node", "Expected the keyword 'in' after the first argument");
        }
        requiredSegment = requiredSegment.substring(4);
        endIndex += 4;

        variableTree = new VariableNode();
        try {
            variableTree.validateRule(requiredSegment);
            variableTree.getVariable().setType(getType());
            endIndex += variableTree.getEndIndex();
        } catch(InvalidRuleStructureException e) {
            throw new InvalidRuleStructureException("Store Value Node", "Caused by:", e);
        }
    }

    private Type getType() {
        return valueTree.getType();
    }
}
