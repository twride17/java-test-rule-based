package tw.jruletest.generators;

import org.junit.*;

public class TestTestCaseGenerator {

    @Test
    public void testWritingTestCase() {
        String expectedCode = "\tpublic void test() {" +
                                "\n\t\tExample Line 1;" +
                                "\n\t\tExample Line 2;" +
                                "\n\t}";

        String actualCode = TestCaseGenerator.writeTestCase("Example Line 1;\nExample Line 2;", "test");
        Assert.assertEquals(expectedCode, actualCode);
    }
}
