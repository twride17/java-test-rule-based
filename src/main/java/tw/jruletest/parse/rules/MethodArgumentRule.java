package tw.jruletest.parse.rules;

public class MethodArgumentRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        rule = rule.replace("with", "").replace("arguments", "").replace(":", "").trim();
        return rule.replace(" and", ",");
    }
}
