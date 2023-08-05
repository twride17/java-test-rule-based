package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableNode implements TreeNode {

    private String variableString;

    @Override
    public String generateCode() {
        return variableString;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO allow for boolean expressions
        // TODO Check whether variable exists
        Matcher matcher = Pattern.compile("^(([a-z][A-Z0-9a-z]*)|(([A-Z][A-Z0-9a-z]*)\\.([a-z][A-Z0-9a-z]*)))").matcher(rule);
        if(matcher.find()) {
            variableString = matcher.group();
            return matcher.end();
        } else {
            throw new InvalidRuleStructureException(rule, "Variable Node");
        }
    }
}
