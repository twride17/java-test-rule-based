package tw.jruletest.parse.ruletree.rulenodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousMemberException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.files.source.SourceMember;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.parse.ruletree.TreeNode;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with calling a method.
 *
 * @author Toby Wride
 * */

public class MethodNode extends TreeNode {

    private SourceMethod method;

    private String methodName;
    private MethodArgumentNode arguments = null;

    /**
     * Implementation of code generation from TreeNode interface.
     * Generation of code involves generating the code to call the method and inserting the code for any arguments the method requires.
     *
     * @return the generated code segment for calling a method with or without arguments.
     * */

    @Override
    public String generateCode() {
        ImportCollector.addImport("import " + method.getFullClassName() + ";");

        String code = methodName + "(";
        if(arguments != null) {
            code += arguments.generateCode();
        }
        return code + ")";
    }

    /**
     * Implementation of rule validation from TreeNode interface.
     * Checks that the method call has a valid structure for the name and exists in the loaded source classes.
     * If there are arguments required, the validity of the rule depends on the validity of the arguments
     *
     * @param ruleContent rule segment to be validated
     *
     * @return the index required to extract the valid segment from the rule
     *
     * @throws InvalidRuleStructureException thrown if the method name is not of a valid structure or does not exist in the loaded source classes
     * */

    @Override
    public void validateRule(String ruleContent) throws InvalidRuleStructureException {
        int methodCallStart = 0;
        if(ruleContent.charAt(0) == ' ') {
            throw new InvalidRuleStructureException(ruleContent, "Method Node");
        }

        if(ruleContent.substring(methodCallStart).startsWith("method ")) {
            methodCallStart += 7;
        }

        String methodCall;

        int colonIndex = ruleContent.indexOf(":");
        if(colonIndex == -1) {
            methodCall = ruleContent.substring(methodCallStart);
        } else {
            methodCall = ruleContent.substring(methodCallStart, colonIndex);
        }

        int nextSpaceIndex = methodCall.indexOf(' ');
        if(nextSpaceIndex != -1) {
            if(methodCall.charAt(nextSpaceIndex - 1) == ',') {
                methodCall = methodCall.substring(0, nextSpaceIndex-1);
            } else {
                methodCall = methodCall.substring(0, nextSpaceIndex);
            }
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(methodCall);
        if(!matcher.matches()) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        } else {
            methodName = methodCall;
        }

        // Test method call exists
        SourceMember method;
        try {
            method = JavaClassAnalyzer.identifySourceClass(methodCall.split("\\.")[0]).getMember(methodCall.split("\\.")[1]);
            if(method instanceof SourceMethod) {
                this.method = (SourceMethod) method;
            } else {
                throw new InvalidRuleStructureException(methodCall, "Method Node");
            }
        } catch(AmbiguousMemberException | UnidentifiedCallException e) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        }

        if(colonIndex == -1) {
            int currentEnd = methodCallStart + methodCall.length();
            if(currentEnd != ruleContent.length()) {
                String remainingRule = ruleContent.substring(currentEnd);
                if(!remainingRule.startsWith(" and ") && !remainingRule.startsWith(" in ") && !remainingRule.startsWith(", ") && !remainingRule.startsWith(" then ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }
            }
            endIndex = methodCallStart + methodCall.length();
        } else {
            String middleWords = ruleContent.substring(methodCallStart + methodCall.length(), colonIndex);
            if(!middleWords.isEmpty()) {
                if(middleWords.trim().isEmpty()) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                } else {
                    String firstWord = middleWords.trim().split(" ")[0];
                    if (firstWord.equals(",") || firstWord.equals("and") || firstWord.equals("in") || firstWord.equals("then")) {
                        endIndex = methodCallStart + methodCall.length();
                        return;
                    } else if (!(middleWords.equals(" with arguments") || middleWords.equals(" with"))) {
                        throw new InvalidRuleStructureException(ruleContent, "Method Node");
                    }
                }
            }

            if(colonIndex == ruleContent.length()-1) {
                throw new InvalidRuleStructureException(ruleContent, "Method Node");
            }

            arguments = new MethodArgumentNode();
            arguments.validateRule(ruleContent.substring(colonIndex+2));

            endIndex = arguments.getEndIndex() + colonIndex + 2;
        }
    }

    /**
     * Gets the type of the required method
     *
     * @return the type of the method found
     * */

    public Type getType() {
        return method.getType();
    }

    /**
     * Gets the name of the required method
     *
     * @return the name of the method found
     * */

    public String getMethodName() {
        return method.getName();
    }
}
