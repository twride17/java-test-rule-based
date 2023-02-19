package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.CallType;
import tw.jruletest.analyzers.JavaClassAnalyzer;
import tw.jruletest.exceptions.UnidentifiedCallException;
import tw.jruletest.exceptions.UnparsableRuleException;
import tw.jruletest.translation.RuleManipulator;
import tw.jruletest.translation.VariableStore;

public class GetValueRule implements Rule {

    @Override
    public String decodeRule(String rule) throws UnparsableRuleException {
        try {
            String newRule = RuleManipulator.removeValueOfDetail(rule);
            boolean expectedMethod = JavaClassAnalyzer.getCallType(newRule.split(" ")[0]) == CallType.METHOD;
            String declaration = (new StoreValueRule()).setVariableName(deriveVariableName(newRule.split(" ")[0]), rule, expectedMethod);
            String assignment = (new ValueOfCallRule()).decodeRule(rule);
            return declaration + assignment + ";";
        } catch(UnidentifiedCallException e) {
            e.getUnidentifiedCall();
            throw new UnparsableRuleException("Get value of " + rule);
        }
    }

    public String deriveVariableName(String call) {
        String callName = call.split("\\.")[1];
        return VariableStore.getNextUnusedName(callName, callName + "Value");
    }
}