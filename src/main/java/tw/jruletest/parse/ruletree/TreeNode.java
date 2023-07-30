package tw.jruletest.parse.ruletree;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;

public interface TreeNode {

    String generateCode();

    int validateRule(String rule) throws InvalidRuleStructureException;
}
