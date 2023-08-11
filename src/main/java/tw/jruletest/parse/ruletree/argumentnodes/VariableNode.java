package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableNode extends ArgumentNode implements TreeNode {

    @Override
    public String generateCode() {
        return argumentString;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO allow for boolean expressions
        // TODO Check whether variable exists
        Matcher matcher = Pattern.compile("^(([a-z][A-Z0-9a-z]*)|(([A-Z][A-Z0-9a-z]*)\\.([a-z][A-Z0-9a-z]*)))").matcher(rule);
        if(matcher.find()) {
            if(!((matcher.end() != rule.length()) && (rule.charAt(matcher.end()) != ' ') && (rule.charAt(matcher.end()) != ','))) {
                if (!(matcher.group().equals("true") || matcher.group().equals("false"))) {
                    argumentString = matcher.group();
                    return matcher.end();
                }
            }
        }
        throw new InvalidRuleStructureException(rule, "Variable Node");
    }

    public String getArgument() {
        return argumentString;
    }
}
