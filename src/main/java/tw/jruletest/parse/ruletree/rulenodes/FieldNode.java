package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.files.source.SourceMember;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals explicitly with static or instance fields from source classes.
 *
 * @author Toby Wride
 * */

public class FieldNode implements TreeNode {

    private SourceField field;
    private String className;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves concatenating the name of the field to with the defining class and importing the correct class.
     *
     * @return the generated code segment for accessing the field's value
     * */

    @Override
    public String generateCode() {
        ImportCollector.addImport("import " + field.getFullClassName() + ";");
        return className + "." + field.getName();
    }



    @Override
    public int validateRule(String ruleContent) throws InvalidRuleStructureException {
        String fieldCall = ruleContent;
        int nextSpaceIndex = ruleContent.indexOf(' ');
        if(nextSpaceIndex != -1) {
            fieldCall = fieldCall.substring(0, nextSpaceIndex);
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(fieldCall);
        if(!matcher.matches()) {
            if(fieldCall.charAt(fieldCall.length()-1) == ',') {
                matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(fieldCall.substring(0, fieldCall.length()-1));
                if(!matcher.matches()) {
                    throw new InvalidRuleStructureException(fieldCall, "Field Node");
                } else {
                    fieldCall = fieldCall.substring(0, fieldCall.length()-1);
                    nextSpaceIndex -= 1;
                }
            } else {
                throw new InvalidRuleStructureException(fieldCall, "Field Node");
            }
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

        if(nextSpaceIndex == -1) {
            return fieldCall.length();
        } else {
            return nextSpaceIndex;
        }

    }

    /**
     * Gets the type of the field
     *
     * @return the type of the stored field
     * */

    public Type getType() {
        return field.getType();
    }

    /**
     * Gets the name of the field
     *
     * @return the name of the stored field
     * */

    public String getFieldName() {
        return field.getName();
    }
}
