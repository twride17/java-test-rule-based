package tw.jruletest.parse.ruletree.innernodes.valuenodes;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;
import tw.jruletest.variables.Variable;
import tw.jruletest.variables.VariableStore;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with constants of any primitive type, (such as int, char, boolean etc)
 *
 * @author Toby Wride
 * */

public class VariableNode extends ChildNode implements Rule {

    private Variable variable;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generates the code by returning the stored argument.
     *
     * @return the stored argument as the required code segment
     * */

    @Override
    public String generateCode() {
        return variable.getName();
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Assuming the variable has an appropriate structure, the existence of the variable is then checked.
     *
     * @param rule rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule has a correct structure but has not been defined yet.
     * */

    @Override
    public void validateRule(String rule) throws InvalidRuleStructureException {
        String foundVariable = rule.substring(0, validateStructure(rule));
        if(!VariableStore.variableExists(Runner.getCurrentMethod(), foundVariable)) {
            VariableStore.addVariable(Runner.getCurrentMethod(), foundVariable, null, false);
        }
        variable = VariableStore.findVariable(Runner.getCurrentMethod(), foundVariable);
        endIndex = variable.getName().length();
    }

    /**
     * Helper method for rule validation, focussing on validating the structure of the rule
     * Checks that at least part of the rule matches the structure required for a variable. Variables are expected to be in camel case.
     *
     * @param rule rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not have an appropriate structure or is not written in camel case as expected.
     * */

    public int validateStructure(String rule) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^([a-z][A-Z0-9a-z]*)").matcher(rule);
        if(matcher.find()) {
            if((matcher.end() == rule.length()) || (rule.charAt(matcher.end()) == ' ') || (rule.charAt(matcher.end()) == ',')) {
                if(!matcher.group().equals("true") && !matcher.group().equals("false")) {
                    return matcher.end();
                }
                throw new InvalidRuleStructureException("Variable Node", "Boolean values cannot be used as variables");
            }
            throw new InvalidRuleStructureException("Variable Node", "Expected remaining rule after variable name to be empty or start with a space or comma");
        }
        throw new InvalidRuleStructureException("Variable Node", "Variable names must start with a lower case letter and then only contain alphanumeric characters");
    }

    /**
     * Gets the type of the represented variable
     *
     * @return the type of the variable stored by this object
     * */

    public Type getType() {
        return variable.getType();
    }

    /**
     * Gets the stored variable name
     *
     * @return the name of the variable stored by this object
     * */

    public String getArgument() {
        return variable.getName();
    }

    public Variable getVariable() {
        return variable;
    }
}
