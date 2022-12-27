package tw.jruletest.parse.rules;

import tw.jruletest.analyzers.ImportCollector;

public class ExpectationRule implements Rule {
    @Override
    public String decodeRule(String rule) {
        ImportCollector.addImport("import tw.jruletest.expectations.*;");
        return "Expectations.expect(value)." +
                "toEqual(13);";
    }
}
