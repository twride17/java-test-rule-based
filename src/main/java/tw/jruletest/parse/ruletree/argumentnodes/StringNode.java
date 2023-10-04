package tw.jruletest.parse.ruletree.argumentnodes;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;

/**
 * Rule node that deals with Strings as arguments
 *
 * @author Toby Wride
 * */

public class StringNode extends ArgumentNode implements TreeNode {

    /**
     * Implementation of code generation from TreeNode interface.
     * Generates the code by returning the stored argument and replacing the '`' character with speech marks (").
     *
     * @return the stored argument, along with syntactically correct quote marks, as the required code segment
     * */

    @Override
    public String generateCode() {
        return argumentString.replace("`", "\"");
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that part, if not all, of the rule segment is surrounded by two '`' characters and that there are no speech marks (") in between them.
     *
     * @param rule rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the rule does not have a pair of '`' characters or if the contained segment has one or more speech marks (").
     * */

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

    /**
     * Returns the type of this argument as the String class
     *
     * @return the String class as the type of this argument
     * */

    public Type getType() {
        return String.class;
    }

    /**
     * Gets the content of the argument
     *
     * @return the content of the String argument, including the quotes
     * */

    public String getArgumentString() {
        return argumentString;
    }
}
