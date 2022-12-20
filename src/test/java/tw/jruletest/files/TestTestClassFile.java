package tw.jruletest.files;

import org.junit.After;
import org.junit.*;

import java.util.Map;

public class TestTestClassFile {

    private TestClassFile file;

    @Before
    public void setup() {
        file = new TestClassFile("Test");
        file.addRule("Example", "Example Rule 1");
    }

    @Test
    public void testAddingRuleWithNewMethod() {
        file.addRule("Example 2", "Example Rule 2");
        Map<String, String> rules = file.getExtractedRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("Example Rule 1", rules.get("Example"));
        Assert.assertEquals("Example Rule 2", rules.get("Example 2"));
    }

    @Test
    public void testAddingRuleWithExistingMethodName() {
        file.addRule("Example", "Example Rule 2");
        Map<String, String> rules = file.getExtractedRules();
        Assert.assertEquals(2, rules.size());
        Assert.assertEquals("Example Rule 1", rules.get("Example"));
        Assert.assertTrue(rules.containsKey("Example2"));
        Assert.assertEquals("Example Rule 2", rules.get("Example2"));
    }
}
