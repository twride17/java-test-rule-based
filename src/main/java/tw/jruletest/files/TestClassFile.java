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
        int methodNameLength = methodName.length();
        int numExists = 1;
        boolean ruleAdded = false;
        do {
            if (extractedRules.containsKey(methodName)) {
                numExists++;
                methodName = methodName.substring(0, methodNameLength) + numExists;
            } else {
                ruleAdded = true;
            }
        } while(!ruleAdded);
        extractedRules.put(methodName, rule);
    }

    public Map<String, String> getExtractedRules() {
        return extractedRules;
    }
}
