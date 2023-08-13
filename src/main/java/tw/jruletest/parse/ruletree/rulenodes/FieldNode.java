package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMember;
import tw.jruletest.parse.ruletree.TreeNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldNode implements TreeNode {

    private SourceField field;
    private String className;

    @Override
    public String generateCode() {
        return className + "." + field.getName();
    }

    @Override
    public int validateRule(String rule) throws InvalidRuleStructureException {
        String fieldCall = rule;
        int nextSpaceIndex = rule.indexOf(' ');
        if(nextSpaceIndex != -1) {
            fieldCall = fieldCall.substring(0, nextSpaceIndex);
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(fieldCall);
        if(!matcher.matches()) {
            throw new InvalidRuleStructureException(fieldCall, "Field Node");
        }

        SourceMember field;
        try {
            SourceClass cls = JavaClassAnalyzer.identifySourceClass(fieldCall.split("\\.")[0]);
            className = cls.getClassName();
            field = cls.getMember(fieldCall.split("\\.")[1]);
            if(field instanceof SourceField) {
                this.field = (SourceField) field;
            } else {
                throw new InvalidRuleStructureException(fieldCall, "Field Node");
            }
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            throw new InvalidRuleStructureException(fieldCall, "Field Node");
        }

        return nextSpaceIndex;
    }

    public SourceField getField() {
        return field;
    }
}
