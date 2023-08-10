package tw.jruletest.parse.parser;

import org.junit.*;
import tw.jruletest.parse.Parser;

public class TestParserCodeGeneration {

    @Test
    public void testCodeGenerationOfSingleRules() {
        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                            "Call method Example.method then store value of xValue2 in xValue and expect xValue to equal `New string`",
                            "call Example.method: -0.987f, call Example.method2 then store value of Example.method3 with: true in x and expect x to not equal `String`",
                            "  Store -100 in x, store 0.5f in y, store value of Example.method with arguments: false and `New string`, -3.4 in test",
                            "Get value of xValue, store Example.method2 with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};

        String[] expectedCodeBlocks = {"variable = Class.method(1, \"Hello\");\nxValue = value;\nExpectations.expect(xValue).toEqual(1);\n",
                                        "Example.method();\nxValue = xValue2;\nExpectations.expect(xValue).toEqual(\"New string\");\n",
                                        "Example.method(-0.987f);\nExample.method2();\nx = Example.method3(true);\nExpectations.expect(x).toNotEqual(\"String\");\n",
                                        "x = -100;\ny = 0.5f;\ntest = Example.method(false, \"New string\", -3.4);\n",
                                        "variable = xValue;\nz = Example.method2(\"Hello and goodbye\", 1.5, false);\nExpectations.expect(x).toEqual(z);\n"};

        for(int i = 0; i < rules.length; i++) {
            Assert.assertEquals(expectedCodeBlocks[i], Parser.parseRule(rules[i]));
        }
    }

    @Test
    public void testCodeGenerationOfRuleBlock() {
        String[] rules = {"Get value of Class.method: 1 and `Hello`, store value in xValue then expect xValue to equal 1",
                "Call method Example.method then store value of xValue2 in xValue and expect xValue to equal `New string`",
                "call Example.method: -0.987f, call Example.method2 then store value of Example.method3 with: true in x and expect x to not equal `String`",
                "  Store -100 in x, store 0.5f in y, store value of Example.method with arguments: false and `New string`, -3.4 in test",
                "Get value of xValue, store Example.method2 with: `Hello and goodbye`, 1.5 and false in z then expect x to equal z  "};

        String expectedCode = "variable = Class.method(1, \"Hello\");\nxValue = value;\nExpectations.expect(xValue).toEqual(1);\n"
                            + "Example.method();\nxValue = xValue2;\nExpectations.expect(xValue).toEqual(\"New string\");\n"
                            + "Example.method(-0.987f);\nExample.method2();\nx = Example.method3(true);\nExpectations.expect(x).toNotEqual(\"String\");\n"
                            + "x = -100;\ny = 0.5f;\ntest = Example.method(false, \"New string\", -3.4);\n"
                            + "variable = xValue;\nz = Example.method2(\"Hello and goodbye\", 1.5, false);\nExpectations.expect(x).toEqual(z);\n";

        Assert.assertEquals(expectedCode, Parser.parseRules(rules));
    }
}
