package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.CallType;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.*;
import tw.jruletest.translation.RuleManipulator;

public class StoreValueRule implements Rule {

    @Override
    public String decodeRule(String rule) throws UnparsableRuleException {
        rule = RuleManipulator.removeValueOfDetail(rule);
        String variableName = RuleManipulator.getVariableName(rule);
        int inIndex = rule.indexOf("in");
        try {
            String methodCall = rule.substring(0, inIndex).trim();
            boolean isMethod = JavaClassAnalyzer.getCallType(methodCall) == CallType.METHOD;
            return setVariableName(methodCall, variableName, isMethod);
        } catch(UnidentifiedCallException e) {
            e.getUnidentifiedCall();
            throw new UnparsableRuleException(rule);
        }
    }

    public String setVariableName(String varName, String callName, boolean isMethod) {
        String call;
        if(isMethod) {
            call = (new ValueOfCallRule()).constructMethodCall(callName.split("\\.")[0], callName.split("\\.")[1]);
        } else {
            call = callName;
        }
        return JavaClassAnalyzer.getReturnType(callName, isMethod).getTypeName() + " " + varName + " = " + call;
    }
}
