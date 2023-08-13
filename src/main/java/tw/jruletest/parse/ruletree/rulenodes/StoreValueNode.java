package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.Runner;
import tw.jruletest.analyzers.TypeIdentifier;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.parse.ruletree.TreeNode;
import tw.jruletest.parse.ruletree.argumentnodes.ArgumentNode;
import tw.jruletest.parse.ruletree.argumentnodes.VariableNode;
import tw.jruletest.translation.Variable;
import tw.jruletest.translation.VariableStore;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreValueNode implements TreeNode {

    private TreeNode valueTree;
    private VariableNode variableTree;

    @Override
    public String generateCode() {
        String variableName = variableTree.getArgument();
        String code = variableName + " = " + valueTree.generateCode() + ";";

        Variable variable = VariableStore.findVariable(Runner.getCurrentMethod(), variableName);
        if(!variable.isDeclared()) {
            code = TypeIdentifier.getType(getType()) + " " + code;
            variable.makeDeclared();
        }

        return code;
    }

    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        Matcher matcher = Pattern.compile("^(((S|s)tore\\s)?(.+))\\s(in)\\s([a-z][a-zA-Z0-9]*)").matcher(ruleContent);
        if(matcher.find()) {
            String requiredSegment;
            int endIndex;
            if(ruleContent.toLowerCase().startsWith("store")) {
                endIndex = 6;
            } else {
                endIndex = 0;
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.isEmpty()) {
                throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
            }

            valueTree = new ValueNode();
            try {
                endIndex += valueTree.validateRule(requiredSegment);
            } catch(InvalidRuleStructureException e) {
                try {
                    valueTree = Argument.getArgumentNode(requiredSegment);
                    endIndex += valueTree.generateCode().length();
                } catch(InvalidRuleStructureException e2) {
                    throw new InvalidRuleStructureException(requiredSegment, "Get Value Node");
                }
            }

            requiredSegment = ruleContent.substring(endIndex);
            if(requiredSegment.startsWith(" in") && !requiredSegment.trim().equals("in")) {
                requiredSegment = requiredSegment.substring(4);
                endIndex += 4;
            }

            variableTree = new VariableNode();
            endIndex += variableTree.validateStructure(requiredSegment);
            if(!VariableStore.variableExists(Runner.getCurrentMethod(), variableTree.getArgument())) {
                VariableStore.addVariable(Runner.getCurrentMethod(), variableTree.getArgument(), getType(), false);
            }
            return endIndex;
        } else {
            throw new InvalidRuleStructureException(ruleContent, "Store Value Node");
        }
    }

    private Type getType() {
        if(valueTree instanceof ValueNode) {
            return ((ValueNode) valueTree).getType();
        } else {
            return ((ArgumentNode) valueTree).getType();
        }
    }
}
