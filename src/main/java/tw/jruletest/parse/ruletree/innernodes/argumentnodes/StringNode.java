package tw.jruletest.parse.ruletree.innernodes.argumentnodes;

import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

/**
 * Rule node that deals with Strings as arguments
 *
 * @author Toby Wride
 * */

public class StringNode extends ChildNode implements Rule {

    private String stringValue;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generates the code by returning the stored argument and replacing the '`' character with speech marks (").
     *
     * @return the stored argument, along with syntactically correct quote marks, as the required code segment
     * */

    @Override
    public String generateCode() {
        return stringValue.replace("`", "\"");
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
    public void validateRule(String rule) throws InvalidRuleStructureException {
        int possibleQuoteIndex = rule.indexOf('`');
        if(possibleQuoteIndex == 0) {
            try {
                int nextQuoteIndex = rule.substring(1).indexOf('`') + 1;
                if(nextQuoteIndex > possibleQuoteIndex) {
                    stringValue = rule.substring(0, nextQuoteIndex + 1);
                    if(!stringValue.contains("\"")) {
                        endIndex = nextQuoteIndex + 1;
                    } else {
                        throw new InvalidRuleStructureException("String Node", "Rules for strings cannot contain '\"' characters");
                    }
                } else {
                    throw new InvalidRuleStructureException("String Node", "Rules for strings must end with a '`' character");
                }
            } catch(StringIndexOutOfBoundsException e) {
                throw new InvalidRuleStructureException("String Node", "Rules for strings must be at least two characters long");
            }
        } else {
            throw new InvalidRuleStructureException("String Node", "Rules with strings must start with a '`' character");
        }
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
        return stringValue;
    }
}
