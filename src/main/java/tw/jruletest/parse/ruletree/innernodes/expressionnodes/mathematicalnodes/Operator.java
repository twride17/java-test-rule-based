package tw.jruletest.parse.ruletree.innernodes.expressionnodes.mathematicalnodes;

import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;

public class Operator {

    private char operator;

    public Operator(char operator) {
        this.operator = operator;
    }

    public Type getFinalType(ChildNode leftNode, ChildNode rightNode) {
        String leftNodeType = TypeIdentifier.getType(leftNode.getType());
        String rightNodeType = TypeIdentifier.getType(rightNode.getType());

        if(leftNodeType.equals("boolean") || rightNodeType.equals("boolean")) {
            // Make exception, booleans not allowed in mathematical expressions
            return null;
        } else if(leftNodeType.equals("String") || rightNodeType.equals("String")) {
            if(operator == '+') {
                return String.class;
            }
        } else if(leftNodeType.equals(rightNodeType)) {
            return leftNode.getType();
        } else if(leftNodeType.equals("double") || rightNodeType.equals("double")) {
            return double.class;
        }

        // Make exception, types not allowed as combination
        return null;
    }

    public char getOperator() {
        return operator;
    }
}
