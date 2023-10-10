package tw.jruletest.parse;

import tw.jruletest.exceptions.InvalidRuleStructureException;

public interface Rule {

    /**
     * Generate code based on child nodes' code generation and any extra syntax required for the code to compile.
     *
     * @return the code generated by the node.
     * */

    String generateCode();

    /**
     * Validates the structure of the rule based on expected structures and validity of the rule determined by the required child nodes
     *
     * @param ruleContent rule segment to be validated
     *
     * @throws InvalidRuleStructureException thrown if the rule is not found to be valid.
     * */

    void validateRule(String ruleContent) throws InvalidRuleStructureException;
}
