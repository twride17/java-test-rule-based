package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.variables.VariableStore;

import java.lang.reflect.Type;

/**
 * Rule node that deals with getting the value of fields, variables or method calls.
 * This node is designed to be the root node of a tree generated from rules that start with the keyword 'get'.
 *
 * @author Toby Wride
 * */

public class GetValueNode extends TreeNode {

    private ValueNode valueNode;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves determining the required type and selecting a new variable to use along with the code
     * generated from the appropriate child node.
     *
     * @return a full line of code with an identifier, variable name and method of reading the required value.
     * */

    @Override
    public String generateCode() {
        Type type = valueNode.getType();
        String valueCall = valueNode.getCallName();

        if (!(valueCall.endsWith("Value") || valueCall.endsWith("value"))) {
            valueCall += "Value";
        }

        return TypeIdentifier.getType(type) + " " + VariableStore.getNextUnusedVariableName(Runner.getCurrentMethod(), valueCall, type) +
                                                " = " + valueNode.generateCode() + ";";
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that the rule starts with the keyword 'get' then validity of the rule is dependent on the validity of the
     * remaining rule to be determined by the appropriate child node.
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not start with the keyword 'get' or the remaining rule
     * is found to not be valid.
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        if(ruleContent.split(" ")[0].equalsIgnoreCase("get")) {
            if(ruleContent.length() > 4) {
                valueNode = new ValueNode();
                valueNode.validateRule(ruleContent.substring(4));
                endIndex = 4 + valueNode.getEndIndex();
            }
        }
        throw new InvalidRuleStructureException(ruleContent, "Get Value Node");
    }
}
