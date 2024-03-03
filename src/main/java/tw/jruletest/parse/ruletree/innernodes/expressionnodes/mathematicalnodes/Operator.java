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
        }

        if((leftNodeType.equals("String") || rightNodeType.equals("String"))) {
            if(operator == '+') {
                return String.class;
            }
            // Make exception, strings only allowed in concatenation expressions
            return null;
        }

        if(leftNodeType.equals("double") || rightNodeType.equals("double")) {
            return double.class;
        } else if(leftNodeType.equals("float") || rightNodeType.equals("float")) {
            return float.class;
        } else if(leftNodeType.equals("int") && rightNodeType.equals("int")) {
            return int.class;
        }

        // Make exception, types not compatible
        return null;
    }

    public char getOperator() {
        return operator;
    }

    public static void main(String[] args) {
        int z = 2;
        double x = 4.5;
        float y = 2.5f;
        System.out.println(z+y);
    }
}
