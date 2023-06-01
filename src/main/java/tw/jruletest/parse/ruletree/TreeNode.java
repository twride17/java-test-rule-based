package tw.jruletest.parse.ruletree;

import tw.jruletest.exceptions.UnparsableRuleException;

public interface TreeNode {
    String generateCode() throws UnparsableRuleException;
}
