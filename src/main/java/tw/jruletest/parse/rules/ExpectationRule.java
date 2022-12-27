package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.ImportCollector;

public class ExpectationRule implements Rule {

    @Override
    public String decodeRule(String rule) {
        String[] parts = rule.trim().split(" ");
        ImportCollector.addImport("import tw.jruletest.expectations.*;");
        String code = "Expectations.expect(" + parts[0] + ").to";
        String firstChar = String.valueOf(parts[2].charAt(0)).toUpperCase();
        parts[2] = firstChar + parts[2].substring(1);
        return code + parts[2] + "(" + parts[3] + ");";
    }
}
