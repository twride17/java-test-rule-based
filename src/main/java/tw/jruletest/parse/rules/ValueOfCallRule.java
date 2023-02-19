package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.CallType;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.exceptions.UnparsableRuleException;

import java.util.ArrayList;
import java.util.Arrays;

public class ValueOfCallRule implements Rule {

    @Override
    public String decodeRule(String rule) throws UnparsableRuleException {
        String classCall = rule.substring(0, rule.indexOf(" "));
        try {
            CallType callType = JavaClassAnalyzer.getCallType(classCall);
            if(callType == CallType.FIELD) {
                return classCall;
            } else {
                return constructMethodCall(rule, classCall);
            }
        } catch(UnidentifiedCallException e) {
            e.getUnidentifiedCall();
            throw new UnparsableRuleException(rule);
        }
    }

    public String constructMethodCall(String rule, String classCall) {
        return (new MethodCallRule()).decodeRule(rule.substring(rule.indexOf(classCall)));
    }
}
