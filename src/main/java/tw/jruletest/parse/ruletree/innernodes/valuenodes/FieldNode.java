package tw.jruletest.parse.ruletree.innernodes.valuenodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousClassException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnknownClassException;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.files.source.SourceField;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.RuleNode;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals explicitly with static or instance fields from source classes.
 *
 * @author Toby Wride
 * */

public class FieldNode extends ChildNode implements Rule {

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
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
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

        SourceField field;
        try {
            SourceClass cls = SourceClassAnalyzer.identifySourceClass(fieldCall.split("\\.")[0]);
            className = cls.getClassName();
            field = cls.findField(fieldCall.split("\\.")[1]);
            if(field != null) {
                this.field = field;
            } else {
                throw new InvalidRuleStructureException(fieldCall, "Field Node");
            }
        } catch(AmbiguousClassException | UnknownClassException e) {
            throw new InvalidRuleStructureException(fieldCall, "Field Node");
        }

        if(nextSpaceIndex == -1) {
            endIndex = fieldCall.length();
        } else {
            endIndex = nextSpaceIndex;
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
