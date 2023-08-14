package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.Runner;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.variables.VariableStore;

import java.lang.reflect.Type;
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
        String foundVariable = rule.substring(0, validateStructure(rule));
        if(VariableStore.variableExists(Runner.getCurrentMethod(), foundVariable)) {
            return argumentString.length();
        }
        throw new InvalidRuleStructureException(rule, "Variable Node");
    }

    public int validateStructure(String rule) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^(([a-z][A-Z0-9a-z]*)|(([A-Z][A-Z0-9a-z]*)\\.([a-z][A-Z0-9a-z]*)))").matcher(rule);
        if(matcher.find()) {
            if(!((matcher.end() != rule.length()) && (rule.charAt(matcher.end()) != ' ') && (rule.charAt(matcher.end()) != ','))) {
                if(!matcher.group().equals("true") && !matcher.group().equals("false")) {
                    argumentString = matcher.group();
                    return matcher.end();
                }
            }
        }
        throw new InvalidRuleStructureException(rule, "Variable Node");
    }

    public Type getType() {
        return VariableStore.findVariable(Runner.getCurrentMethod(), argumentString).getType();
    }

    public String getArgument() {
        return argumentString;
    }
}
