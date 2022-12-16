package tw.jruletest;

import tw.jruletest.app.Runner;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRunner {

    private final ArrayList<String> FILES = new ArrayList<>(Arrays.asList("TestClass1.java","TestClass2.java","TestClass3.java",
                                                                            "TestClass4.java","TestClass5.java","TestClass6.java"));

    @Test
    public void testClassFileSearch() {
        File topFile = new File(System.getProperty("user.dir") + "/src/test/java/tw/jruletest/examples");
        List<File> files = Runner.searchFiles(topFile, new ArrayList<>());
        Assert.assertEquals(6, files.size());
        for(File file: files) {
            Assert.assertTrue(FILES.contains(file.getName()));
        }
    }
}
