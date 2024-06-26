package tw.jruletest.parse.ruletree.innernodes.argumentnodes;

import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with constants of any primitive type, (such as int, char, boolean etc)
 *
 * @author Toby Wride
 * */

public class ConstantNode extends ChildNode implements Rule {

    private String constantString;

    private Type type;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generates the code by returning the stored argument.
     *
     * @return the stored argument as the required code segment
     * */

    @Override
    public String generateCode() {
        return constantString;
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that at least part of the rule segment matches the required structure for one primitive constant.
     * If a valid segment is found, the type is stored
     *
     * @param rule rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not start with any primitive constant structure
     * */

    @Override
    public void validateRule(String rule) throws InvalidRuleStructureException {
        // TODO Previously defined constants???
        Matcher matcher = Pattern.compile("^((-?)([0-9]+)((\\.[0-9]+)?)(f?))").matcher(rule);
        int endIndex;
        if(matcher.find()) {
            if(matcher.group().equals(rule) || (rule.charAt(matcher.end()) == ' ') || (rule.charAt(matcher.end()) == ',')) {
                constantString = matcher.group();
                endIndex = matcher.end();
            } else {
                throw new InvalidRuleStructureException("Constant Node", "'" + matcher.group() + "' should contain a space or comma at the end");
            }
        } else if(rule.startsWith("true") & !((rule.length() > 4) && (rule.charAt(4) != ' ') && (rule.charAt(4) != ','))) {
            constantString = "true";
            endIndex = 4;
        } else if(rule.startsWith("false") & !((rule.length() > 5) && (rule.charAt(5) != ' ') && (rule.charAt(5) != ','))) {
            constantString = "false";
            endIndex = 5;
        } else {
            throw new InvalidRuleStructureException("Constant Node", "Could not identify constant value. Expected constants " +
                                                                        "are integers, doubles, floats, chars or booleans");
        }

        type = findType();
        if(type != null) {
            this.endIndex = endIndex;
        } else {
            throw new InvalidRuleStructureException("Constant Node", "Failed to identify type of '" + constantString + "'");
        }
    }

    private Type findType() {
        if(constantString.equals("true") || constantString.equals("false")) {
            return boolean.class;
        } else {
            try {
                Integer.parseInt(constantString);
                return int.class;
            } catch (NumberFormatException e) { }

            try {
                Double.parseDouble(constantString);
                if(constantString.charAt(constantString.length()-1) == 'f') {
                    return float.class;
                }
                return double.class;
            } catch (NumberFormatException e) { }

            if(constantString.length() == 1) {
                return char.class;
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the stored type of the constant
     *
     * @return the type of the constant being represented
     * */

    public Type getType() {
        return type;
    }
}
