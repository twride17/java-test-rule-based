package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.CallType;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.*;
import tw.jruletest.translation.RuleManipulator;
import tw.jruletest.translation.VariableStore;

public class GetValueRule implements Rule {

    @Override
    public String decodeRule(String rule) throws UnparsableRuleException {
        try {
            String newRule = RuleManipulator.removeValueOfDetail(rule).trim();
            boolean expectedMethod = JavaClassAnalyzer.getCallType(newRule.split(" ")[0]) == CallType.METHOD;
            return (new StoreValueRule()).setVariableName(deriveVariableName(newRule.split(" ")[0]), newRule, expectedMethod);
        } catch(UnidentifiedCallException e) {
            e.getUnidentifiedCall();
            throw new UnparsableRuleException("Cannot parse rule: Get " + rule);
        }
    }

    public String deriveVariableName(String call) {
        String callName = call.split("\\.")[1];
        return VariableStore.getNextUnusedName(callName, callName + "Value");
    }
}