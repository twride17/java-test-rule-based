package tw.jruletest.parse;

import org.junit.*;
import tw.jruletest.exceptions.UnparsableRuleException;

import java.util.ArrayList;

public class TestParser {

    public void testRules(String[] rules, String[][] expectedSubRules) {
        for(int i = 0; i < rules.length; i++) {
            try {
                ArrayList<String> subRules = Parser.generateTrees(rules[i]);
                Assert.assertEquals(expectedSubRules[i].length, subRules.size());
                for(int j = 0; j < subRules.size(); j++) {
                    Assert.assertEquals(expectedSubRules[i][j], subRules.get(j));
                }
                System.out.println(rules[i]);
            } catch (UnparsableRuleException e) {
                System.out.println();
                Assert.fail("Failed to parse: " + rules[i]);
            }
        }
    }

    @Test
    public void testStoreValueRules() {
        String[][] subRules = {{"Store value of Class.method with arguments: 67, `Hello world`, true and -0.5f in test"},
                               {"store 10.5 in xValue", "store -10.65f in other", "store value of Class.method in variable"},
                               {"Store true in value1", "store false in value2", "store value of Class.method: value1 and value2 in value3"},
                               {"Store value of Example.xValue in test", "store 1 in y", "store `Hello world` in z"},
                               {"Store value of x in y", "Store value of y in z"}};

        String[] rules = {"Store value of Class.method with arguments: 67, `Hello world`, true and -0.5f in test",
                          "store 10.5 in xValue, store -10.65f in other then store value of Class.method in variable",
                          "Store true in value1, store false in value2 then store value of Class.method: value1 and value2 in value3",
                          "Store value of Example.xValue in test then store 1 in y and store `Hello world` in z ",
                          "      Store value of x in y then Store value of y in z    "};

        testRules(rules, subRules);
    }

    @Test
    public void testGetValueRules() {
        String[][] subRules = {{"Get value of Class.method with arguments: 67, `Hello world`, true and -0.5f"},
                               {"get xValue", "get value of Class.method with arguments: -0.98f, 45, false and `New string`", "get yValue"},
                               {"Get result of Example.x", "get Class.method with: `New string`, 56", "get Class.method"},
                               {"Get result of Class.method with arguments: x, y and z", "get value of xValue"},
                               {"Get x", "get y", "get value of z", "get value of Class.method: `Hello, me`, -0.98f, true and 123"}};

        String[] rules = {"Get value of Class.method with arguments: 67, `Hello world`, true and -0.5f",
                          "get xValue, get value of Class.method with arguments: -0.98f, 45, false and `New string` and get yValue",
                          "Get result of Example.x and get Class.method with: `New string`, 56, get Class.method",
                          "  Get result of Class.method with arguments: x, y and z then get value of xValue",
                          "Get x and get y, get value of z then get value of Class.method: `Hello, me`, -0.98f, true and 123   "};

        testRules(rules, subRules);
    }

    @Test
    public void testMethodCallRules() {
        String[][] subRules = {{"Call method Example.method", "call method Example.method with: 0, `Hello` and 4"},
                               {"call method Example.method with arguments: `Hello world, this is a cool and nice string`, 1f"},
                               {"call Example.method: 1, 2 and 3", "call method Example.example", "call Class.example with: -0.89f"},
                               {"call Example.method", "call Example.method2 with: 1 and 2", "call method Example.m2", "call Test.method"},
                               {"Call method Example.example with arguments: 1", "call Example.method2", "call Example.method3: xValue"}};

        String[] rules = {"Call method Example.method, call method Example.method with: 0, `Hello` and 4",
                          "  call method Example.method with arguments: `Hello world, this is a cool and nice string`, 1f",
                          "call Example.method: 1, 2 and 3 then call method Example.example and call Class.example with: -0.89f",
                          "call Example.method, call Example.method2 with: 1 and 2 and call method Example.m2 then call Test.method",
                          "Call method Example.example with arguments: 1 then call Example.method2 and call Example.method3: xValue"};

        testRules(rules, subRules);
    }

    @Test
    public void testExpectationRules() {
        String[][] subRules = {{"Expect 1 to equal xValue", "Expect value2 to not equal 3", "expect value of CLass.method to equal -0.98f"},
                               {"Expect x to equal 1", "expect y to not equal `Hello`", "expect result of Example.method to equal false"},
                               {"Expect value of Example.method with: 56 and 0.98 to equal 3", "expect xValue to equal `New string`"},
                               {"expect Class.method with arguments: 109, `New and cool string, this is` and -90.2f to not equal -0.9f"},
                               {"Expect 30.5 to not equal xValue", "expect string to equal Class.method", "expect Class.method2: 1 to equal 0"}};

        String[] rules = {"Expect 1 to equal xValue, Expect value2 to not equal 3 and expect value of CLass.method to equal -0.98f",
                          "  Expect x to equal 1, expect y to not equal `Hello` then expect result of Example.method to equal false",
                          "Expect value of Example.method with: 56 and 0.98 to equal 3 and expect xValue to equal `New string`     ",
                          "      expect Class.method with arguments: 109, `New and cool string, this is` and -90.2f to not equal -0.9f",
                          "Expect 30.5 to not equal xValue, expect string to equal Class.method and expect Class.method2: 1 to equal 0"};

        testRules(rules, subRules);
    }

    @Test
    public void testDifferentRuleCombinations() {
        String[][] subRules = {{"Get value of Class.method: 1 and `Hello`", "store value in xValue", "expect xValue to equal 1"},
                               {"Call method Example.method", "store value of Example.x in xValue", "expect xValue to equal `New string`"},
                               {"call Example.method: -0.987f", "call Example.method2", "store value of Example.method3 with: true in x", "expect x to not equal `String`"},
                               {"Store -100 in x", "store 0.5f in y", "store value of Example.method with arguments: false and `New string`, -3.4 in test"},
                               {"store Example.x in x", "store Example.method2 with: `Hello and goodbye`, 1.5 and false in z", "expect x to equal z"}};

        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                          "Call method Example.method then store value of Example.x in xValue and expect xValue to equal `New string`",
                          "call Example.method: -0.987f, call Example.method2 then store value of Example.method3 with: true in x and expect x to not equal `String`",
                          "  Store -100 in x, store 0.5f in y, store value of Example.method with arguments: false and `New string`, -3.4 in test",
                          "store Example.x in x, store Example.method2 with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};
        testRules(rules, subRules);
    }

    @Test
    public void testInvalidRuleStructureEffects() {
        String[] rules = {"get x get y", "store 100 and expect 0 to equal x", "expect .5 to equal 3", "store x in y, z",
                          "Example.method with: 1", "expect store x in y to equal 3", "call Example.method, expect to equal 1",
                          "Call method x", "Store value and expect value to equal 0", "get result of Class.method and expect 0",
                          "expect x  to equal 0", "call x", "call 0.5f", "store value of `hello` in z", "get value of 100",
                          "tore x", "cal", "except 0 to equal 1", "x", "before calling x", "get x store in y",
                          "expect 0 not to equal 1", "get value of x and store", "store value in y and expect", "get x and get"};
        for(String rule: rules) {
            try {
                Parser.generateTrees(rule);
                Assert.fail("'" + rule + "': passed validation");
            } catch (UnparsableRuleException e) { }
        }
    }
}
