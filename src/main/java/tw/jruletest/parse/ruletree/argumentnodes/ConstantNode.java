package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with constants of any primitive type, (such as int, char, boolean etc)
 *
 * @author Toby Wride
 * */

public class ConstantNode extends ArgumentNode implements TreeNode {
    private Type type;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generates the code by returning the stored argument.
     *
     * @return the stored argument as the required code segment
     * */

    @Override
    public String generateCode() {
        return argumentString;
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
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO Previously defined constants???
        Matcher matcher = Pattern.compile("^((-?)([0-9]+)((\\.[0-9]+)?)(f?))").matcher(rule);
        int endIndex;
        if(matcher.find()) {
            if(!(!matcher.group().equals(rule) && (rule.charAt(matcher.end()) != ' ') && (rule.charAt(matcher.end()) != ','))) {
                argumentString = matcher.group();
                endIndex = matcher.end();
            } else {
                throw new InvalidRuleStructureException(rule, "Constant Node");
            }
        } else if(rule.startsWith("true") & !((rule.length() > 4) && (rule.charAt(4) != ' ') && (rule.charAt(4) != ','))) {
            argumentString = "true";
            endIndex = 4;
        } else if(rule.startsWith("false") & !((rule.length() > 5) && (rule.charAt(5) != ' ') && (rule.charAt(5) != ','))) {
            argumentString = "false";
            endIndex = 5;
        } else {
            throw new InvalidRuleStructureException(rule, "Constant Node");
        }

        type = findType();
        if(type != null) {
            return endIndex;
        } else {
            throw new InvalidRuleStructureException(rule, "Constant Node");
        }
    }

    private Type findType() {
        if(argumentString.equals("true") || argumentString.equals("false")) {
            return boolean.class;
        } else {
            try {
                Integer.parseInt(argumentString);
                return int.class;
            } catch (NumberFormatException e) { }

            try {
                Float.parseFloat(argumentString);
                return float.class;
            } catch (NumberFormatException e) { }

            try {
                Double.parseDouble(argumentString);
                return double.class;
            } catch (NumberFormatException e) { }

            if(argumentString.length() == 1) {
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
