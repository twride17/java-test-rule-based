package tw.jruletest.parse.ruletree;

import tw.jruletest.exceptions.parsing.ChildNodeSelectionException;
import tw.jruletest.exceptions.parsing.InvalidRuleStructureException;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;
import tw.jruletest.parse.ruletree.innernodes.expressionnodes.booleannodes.*;
import tw.jruletest.parse.ruletree.innernodes.expressionnodes.mathematicalnodes.MathematicalExpressionNode;
import tw.jruletest.parse.ruletree.innernodes.valuenodes.*;
import tw.jruletest.parse.ruletree.innernodes.argumentnodes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the expected implementation of nodes to be added to a tree in order to validate rules and generate the required code.
 *
 * @author Toby Wride
 * */

public abstract class RuleNode {

    protected int endIndex = 0;

    public static final int CHILD_NODE = 1;
    public static final int VALUE_RETRIEVAL_NODE = 2;
    public static final int BOOLEAN_EXPRESSION_NODE = 3;
    public static final int OPERABLE_NODE = 4;

    /**
     * Gets the index where the validity of the rule ends
     *
     * @return the index of the last valid character
     * */

    public int getEndIndex() {
        return endIndex;
    }

    public static ChildNode getChildNode(String ruleContent, int possibleNodeIndex) throws ChildNodeSelectionException {
        ChildNode[] possibleNodes = {};
        switch(possibleNodeIndex) {
            case CHILD_NODE:
                possibleNodes = new ChildNode[] {new NegatedExpressionNode(), new BinaryExpressionNode(),
                                                new LogicalComparisonNode(), new MathematicalExpressionNode(), new ValueNode(),
                                                new StringNode(), new ConstantNode()};
                break;
            case VALUE_RETRIEVAL_NODE:
                possibleNodes = new ChildNode[] {new MethodNode(), new FieldNode(), new VariableNode()};
                break;
            case BOOLEAN_EXPRESSION_NODE:
                possibleNodes = new ChildNode[] {new BinaryExpressionNode(), new NegatedExpressionNode(), new LogicalComparisonNode(),
                                                new ValueNode(), new ConstantNode()};
                break;
            case OPERABLE_NODE:
                possibleNodes = new ChildNode[] {new MathematicalExpressionNode(), new ValueNode(), new StringNode(), new ConstantNode()};
                break;
        }


        List<InvalidRuleStructureException> invalidStructureExceptions = new ArrayList<>();
        for(ChildNode possibleNode: possibleNodes) {
            try {
                ((Rule)possibleNode).validateRule(ruleContent);
                return possibleNode;
            } catch(InvalidRuleStructureException e) {
                invalidStructureExceptions.add(e);
            }
        }
        throw new ChildNodeSelectionException(invalidStructureExceptions, ruleContent);
    }
}
