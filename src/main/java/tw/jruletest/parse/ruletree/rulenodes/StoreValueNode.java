package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ArgumentNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;
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

public class StoreValueNode implements TreeNode {

    private TreeNode valueTree;
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
        String code = variableName + " = " + valueTree.generateCode() + ";";

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

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^(((S|s)tore\\s)?(.+))\\s(in)\\s([a-z][a-zA-Z0-9]*)").matcher(ruleContent);
        if(matcher.find()) {
            String requiredSegment;
            int endIndex;
            if(ruleContent.toLowerCase().startsWith("store")) {
                endIndex = 6;
            } else {
                endIndex = 0;
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.isEmpty()) {
                throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
            }

            valueTree = new ValueNode();
            try {
                endIndex += valueTree.validateRule(requiredSegment);
            } catch(InvalidRuleStructureException e) {
                try {
                    valueTree = Argument.getArgumentNode(requiredSegment);
                    endIndex += valueTree.generateCode().length();
                } catch(InvalidRuleStructureException e2) {
                    throw new InvalidRuleStructureException(requiredSegment, "Get Value Node");
                }
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.startsWith(" in") && !requiredSegment.trim().equals("in")) {
                requiredSegment = requiredSegment.substring(4);
                endIndex += 4;
            }

            variableTree = new VariableNode();
            endIndex += variableTree.validateStructure(requiredSegment);
            if(!VariableStore.variableExists(Runner.getCurrentMethod(), variableTree.getArgument())) {
                VariableStore.addVariable(Runner.getCurrentMethod(), variableTree.getArgument(), getType(), false);
            }
            return endIndex;
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
        }
    }

    /**
     * Gets the type from the child node
     *
     * @return the type of the field, method or variable represented by the child node.
     * */

    private Type getType() {
        if(valueTree instanceof ValueNode) {
            return ((ValueNode) valueTree).getType();
        } else {
            return ((ArgumentNode) valueTree).getType();
        }
    }
}
