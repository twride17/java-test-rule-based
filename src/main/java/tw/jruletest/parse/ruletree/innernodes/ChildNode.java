package tw.jruletest.parse.ruletree.innernodes;

import tw.jruletest.parse.ruletree.RuleNode;

import java.lang.reflect.Type;

public abstract class ChildNode extends RuleNode {

    public abstract Type getType();
}
