package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.ImportCollector;
import tw.jruletest.analyzers.JavaClassAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpectationRule implements Rule {

    private static final String[] POSSIBLE_OPERATORS = {"equal"};

    @Override
    public String decodeRule(String rule) {
        ImportCollector.addImport("import tw.jruletest.expectations.*;");
        rule = rule.trim();

        int toIndex = rule.indexOf("to");
        String expectSegment = rule.substring(0, toIndex).trim();
        String remains = rule.substring(toIndex);

        int operatorIndex = -1;
        for(String operator: POSSIBLE_OPERATORS) {
            if(remains.contains(" " + operator + " ")) {
                operatorIndex = remains.indexOf(operator) + operator.length();
            }
        }

        String operatorSegment = remains.substring(0, operatorIndex).trim();
        String actualSegment = remains.substring(operatorIndex).trim();

        String code = "Expectations.expect(" + getValueArgument(expectSegment) + ").";
        return code + getOperation(operatorSegment) + "(" + getValueArgument(actualSegment) + ");";
    }

    private String getValueArgument(String segment) {
        String requiredCall = segment.split(" ")[0];
        if(segment.startsWith("value of")) {
            requiredCall = segment.substring(segment.indexOf("value of")+9);
        }

        System.out.println(requiredCall);
        if(JavaClassAnalyzer.isMethodCall(requiredCall)) {
            return (new GetValueRule()).constructMethodCall(segment, requiredCall).replace(";", "");
        } else {
            return requiredCall;
        }
    }

    private String getOperation(String segment) {
        String code = "to";
        ArrayList<String> segmentParts = new ArrayList<>(Arrays.asList(segment.split(" ")));
        segmentParts.remove("to");

        if(segmentParts.contains("not")) {
            code += "Not";
            segmentParts.remove("not");
        }

        String firstChar = String.valueOf(segmentParts.get(0).charAt(0)).toUpperCase();
        return code + firstChar + segmentParts.get(0).substring(1);
    }
}
