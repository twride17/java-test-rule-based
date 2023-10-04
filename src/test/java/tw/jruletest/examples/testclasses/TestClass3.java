package tw.jruletest.examples.testclasses;

public class TestClass3 {

    public static String currentRule;

    public static void rule1() {
        currentRule = "Get value of Class.field";
    }

    public static void rule2() {
        currentRule = "Get value of Class.method";
    }

    public static void rule3() {
        currentRule = "Get value of Class.field";
    }
}
