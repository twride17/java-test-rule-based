package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstantNode extends ArgumentNode implements TreeNode {

    @Override
    public String generateCode() {
        return argumentString;
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        // TODO Previously defined constants???
        Matcher matcher = Pattern.compile("^((-?)([0-9]+)((\\.[0-9]+)?)(f?))").matcher(rule);
        if(matcher.find()) {
            if(!(!matcher.group().equals(rule) && (rule.charAt(matcher.end()) != ' ') && (rule.charAt(matcher.end()) != ','))) {
                argumentString = matcher.group();
                return matcher.end();
            }
        } else if(rule.startsWith("true") & !((rule.length() > 4) && (rule.charAt(4) != ' ') && (rule.charAt(4) != ','))) {
            argumentString = "true";
            return 4;
        } else if(rule.startsWith("false") & !((rule.length() > 5) && (rule.charAt(5) != ' ') && (rule.charAt(5) != ','))) {
            argumentString = "false";
            return 5;
        }
        throw new InvalidRuleStructureException(rule, "Constant Node");
    }
}
