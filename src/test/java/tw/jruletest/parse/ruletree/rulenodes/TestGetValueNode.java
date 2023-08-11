package tw.jruletest.parse.ruletree.rulenodes;

import org.junit.*;
import tw.jruletest.exceptions.InvalidRuleStructureException;
import tw.jruletest.translation.VariableStore;

public class TestGetValueNode {

    private GetValueNode node;

    /* Testing rule validation for Get Value node */

    @Test
    public void testGetValueRuleWithKeyword() {
        String rule = "Get value of xValue";
        node = new GetValueNode("");
        try {
            Assert.assertEquals(rule.length(), node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    @Test
    public void testGetValueRuleWithNoKeyword() {
        String rule = "value of xValue";
        node = new GetValueNode("");
        try {
            node.validateRule(rule);
            Assert.fail(rule + ": passed when should fail");
        } catch(InvalidRuleStructureException e) { }
    }

    @Test
    public void testValidGetValueRuleWithExtraRule() {
        String rule = "Get value of xValue and get value of y";
        node = new GetValueNode("");
        try {
            Assert.assertEquals(19, node.validateRule(rule));
        } catch(InvalidRuleStructureException e) {
            System.out.println(rule);
            Assert.fail("Failed");
        }
    }

    /* Testing code generation for Get Value node */
    @Test
    public void testCodeGeneration() {
        // TODO Update tests to use class fields eg: Example.x
        // Currently incorrectly determined to be Method nodes
        String[] rules = {"Get value of xValue", "Get Class.method", /*"Get value of Example.x and store in y",*/
                            "Get Class.method with arguments: `Hello world`, 10 and -0.89f, xValue"};

        String[] expectedStrings = {"xValue2 = xValue;", "methodValue = Class.method();", /*"Example.x",*/
                                    "methodValue = Class.method(\"Hello world\", 10, -0.89f, xValue);"};

        for(int i = 0; i < rules.length; i++) {
            node = new GetValueNode("");
            try {
                VariableStore.addVariable("", "xValue");
                node.validateRule(rules[i]);
                Assert.assertEquals(node.generateCode(), expectedStrings[i]);
                VariableStore.reset();
            } catch(InvalidRuleStructureException e) {
                Assert.fail(rules[i] + ": failed");
            }
        }
    }

    @After
    public void teardown() {
        VariableStore.reset();
    }
}
