package tw.jruletest.files;

import java.util.HashMap;
import java.util.Map;

public class TestClassFile {

    private String className;
    private Map<String, String> extractedRules = new HashMap<>();

    public TestClassFile(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void addRule(String methodName, String rule) {
        extractedRules.put(methodName, rule);
    }

    public Map<String, String> getExtractedRules() {
        return extractedRules;
    }
}
