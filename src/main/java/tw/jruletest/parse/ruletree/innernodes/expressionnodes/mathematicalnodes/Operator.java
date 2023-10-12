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

        if((operator == '+') && (leftNodeType.equals("String") || rightNodeType.equals("String"))) {
            return String.class;
        } else if(leftNodeType.equals("double") || rightNodeType.equals("double")) {
            return double.class;
        } else if((leftNodeType.equals("int") && rightNodeType.equals("float")) || ((leftNodeType.equals("float") && rightNodeType.equals("int")))) {
            return float.class;
        } else if(leftNodeType.equals("int") && rightNodeType.equals("int")) {
            return int.class;
        } else {
            // Make exception - type mismatch
            return null;
        }
    }

    public char getOperator() {
        return operator;
    }
}
