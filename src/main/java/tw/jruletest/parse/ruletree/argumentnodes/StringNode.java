package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;

public class StringNode extends ArgumentNode implements TreeNode {

    @Override
    public String generateCode() {
        return argumentString.replace("`", "\"");
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        int possibleQuoteIndex = rule.indexOf('`');
        if(possibleQuoteIndex == 0) {
            try {
                int nextQuoteIndex = rule.substring(1).indexOf('`') + 1;
                if(nextQuoteIndex > possibleQuoteIndex) {
                    argumentString = rule.substring(0, nextQuoteIndex + 1);
                    if(!argumentString.contains("\"")) {
                        return nextQuoteIndex + 1;
                    }
                }
            } catch(StringIndexOutOfBoundsException e) { }
        }
        throw new InvalidRuleStructureException(rule, "String Node");
    }

    public Type getType() {
        return String.class;
    }

    /* Only required for testing */
    public String getArgumentString() {
        return argumentString;
    }
}