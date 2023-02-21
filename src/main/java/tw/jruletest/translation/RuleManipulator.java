package tw.jruletest.translation;

public class RuleManipulator {

    public static String removeValueOfDetail(String ruleContents) {
        return ruleContents.replace("value of ", "");
    }

    public static String getVariableName(String rule) {
        return rule.substring(rule.lastIndexOf(" ")).trim();
    }
}
