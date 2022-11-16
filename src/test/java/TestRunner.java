package test.java;

import main.java.tw.jruletest.app.Runner;
import org.junit.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestRunner {

    private final ArrayList<String> CLASSES = new ArrayList<>(Arrays.asList("TestClass1.class","TestClass2.class","TestClass3.class",
                                                                            "TestClass4.class","TestClass5.class","TestClass6.class"));

    @Test
    public void testClassFileSearch() {
        File topFile = new File(System.getProperty("user.dir") + "/src/test/java/examples");
        List<File> files = Runner.searchFiles(topFile, new ArrayList<>());
        Assert.assertEquals(6, files.size());
        for(File file: files) {
            Assert.assertTrue(CLASSES.contains(file.getName()));
        }
    }
}
