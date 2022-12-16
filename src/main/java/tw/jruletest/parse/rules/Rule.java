package tw.jruletest.parse.rules;

public interface Rule {

    /**
     * @author Toby Wride
     *
     * Defines the commands to be injected into test suite based on a given rule
     */

    String decodeRule(String rule);
}
