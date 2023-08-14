package tw.jruletest.parse.ruletree;

import tw.jruletest.exceptions.InvalidRuleStructureException;

public interface TreeNode {

    String generateCode();

    int validateRule(String rule) throws InvalidRuleStructureException;
}
