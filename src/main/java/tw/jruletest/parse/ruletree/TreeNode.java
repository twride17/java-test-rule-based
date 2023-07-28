package tw.jruletest.parse.ruletree;

import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnparsableRuleException;

public interface TreeNode {

    String generateCode() throws UnparsableRuleException;

    int validateRule(String rule) throws InvalidRuleStructureException;
}
