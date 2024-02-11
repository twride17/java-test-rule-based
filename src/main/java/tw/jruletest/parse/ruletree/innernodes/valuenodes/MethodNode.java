package tw.jruletest.parse.ruletree.innernodes.valuenodes;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.SourceClassAnalyzer;
import tw.jruletest.exceptions.AmbiguousClassException;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.exceptions.UnknownClassException;
import tw.jruletest.files.source.SourceClass;
import tw.jruletest.files.source.SourceMethod;
import tw.jruletest.parse.Rule;
import tw.jruletest.parse.ruletree.innernodes.ChildNode;
import tw.jruletest.parse.ruletree.innernodes.MethodArgumentNode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rule node that deals with calling a method.
 *
 * @author Toby Wride
 * */

public class MethodNode extends ChildNode implements Rule {

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
        if (ruleContent.charAt(0) == ' ') {
            throw new InvalidRuleStructureException(ruleContent, "Method Node");
        }

        if (ruleContent.substring(methodCallStart).startsWith("method ")) {
            methodCallStart += 7;
        }

        String methodCall;

        int colonIndex = ruleContent.indexOf(":");
        if (colonIndex == -1) {
            methodCall = ruleContent.substring(methodCallStart);
        } else {
            methodCall = ruleContent.substring(methodCallStart, colonIndex);
        }

        int nextSpaceIndex = methodCall.indexOf(' ');
        if (nextSpaceIndex != -1) {
            if (methodCall.charAt(nextSpaceIndex - 1) == ',') {
                methodCall = methodCall.substring(0, nextSpaceIndex - 1);
            } else {
                methodCall = methodCall.substring(0, nextSpaceIndex);
            }
        }

        Matcher matcher = Pattern.compile("([A-Z][a-z0-9A-z]*)\\.([a-z][A-Z0-9a-z]*)").matcher(methodCall);
        if (!matcher.matches()) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        } else {
            methodName = methodCall;
        }

        String[] argumentList = {};

        if (colonIndex == -1) {
            int currentEnd = methodCallStart + methodCall.length();
            if (currentEnd != ruleContent.length()) {
                String remainingRule = ruleContent.substring(currentEnd);
                if (!remainingRule.startsWith(" and ") && !remainingRule.startsWith(" in ") && !remainingRule.startsWith(", ") && !remainingRule.startsWith(" then ")) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }
            }
            endIndex = methodCallStart + methodCall.length();
        } else {
            boolean colonForDifferentMethod = false;
            String middleWords = ruleContent.substring(methodCallStart + methodCall.length(), colonIndex);
            if (!middleWords.isEmpty()) {
                if (middleWords.trim().isEmpty()) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                } else {
                    String firstWord = middleWords.trim().split(" ")[0];
                    if (firstWord.equals(",") || firstWord.equals("and") || firstWord.equals("in") || firstWord.equals("then")) {
                        endIndex = methodCallStart + methodCall.length();
                        colonForDifferentMethod = true;
                    } else if (!(middleWords.equals(" with arguments") || middleWords.equals(" with"))) {
                        throw new InvalidRuleStructureException(ruleContent, "Method Node");
                    }
                }
            }

            if (!colonForDifferentMethod) {
                if (colonIndex == ruleContent.length() - 1) {
                    throw new InvalidRuleStructureException(ruleContent, "Method Node");
                }

                String remainingRule = ruleContent.substring(colonIndex + 2);
                arguments = new MethodArgumentNode();
                arguments.validateRule(remainingRule);

                endIndex = arguments.getEndIndex() + colonIndex + 2;
                argumentList = remainingRule.substring(0, arguments.getEndIndex()).split("((\\s(and))|,)\\s");
            }
        }

        // Test method call exists with correct name and parameters
        SourceClass cls;
        try {
            cls = SourceClassAnalyzer.identifySourceClass(methodCall.split("\\.")[0]);
        } catch (AmbiguousClassException | UnknownClassException e) {
            throw new InvalidRuleStructureException(methodCall, "Method Node");
        }

        int numArguments = 0;
        ArrayList<String> currentArguments = new ArrayList<>();
        ArrayList<Type> currentTypes = new ArrayList<>();
        ArrayList<String> currentConnectives = new ArrayList<>();
        ArrayList<ChildNode> argumentNodes = new ArrayList<>();
        if(arguments != null) {
            argumentNodes = arguments.getArguments();
        }
        String remainingRule = ruleContent.substring(colonIndex + 2);
        for (int i = -1; i < argumentNodes.size(); i++) {
            if (i >= 0) {
                String connective = "";
                String argument = "";
                if(remainingRule.indexOf(" and ") == 0) {
                    connective = " and ";
                } else if(remainingRule.indexOf(", ") == 0) {
                    connective = ", ";
                }
                remainingRule = remainingRule.substring(connective.length());
                ChildNode methodArgument = argumentNodes.get(i);
                argument = remainingRule.substring(0, methodArgument.getEndIndex());
                remainingRule = remainingRule.substring(methodArgument.getEndIndex());
                currentArguments.add(argument);
                currentConnectives.add(connective);
                currentTypes.add(methodArgument.getType());
            }

            SourceMethod method = cls.findMethod(methodCall.split("\\.")[1], currentTypes);
            if (method != null) {
                this.method = method;
                numArguments = currentArguments.size();
            }
        }

        if ((method == null) || ((numArguments == 0) && (!argumentNodes.isEmpty()))) {
            throw new InvalidRuleStructureException(ruleContent, "Method Node");
        } else if (!currentArguments.isEmpty()) {
            endIndex = colonIndex + 2;
            for (int i = 0; i < numArguments; i++) {
                endIndex += currentConnectives.get(i).length() + argumentNodes.get(i).getEndIndex();
            }
            arguments.setArgumentSubset(numArguments);
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
