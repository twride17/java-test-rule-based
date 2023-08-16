package tw.jruletest.files;

import org.junit.*;
import tw.jruletest.files.FileFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFileFinder {

    private final ArrayList<String> FILES = new ArrayList<>(Arrays.asList("TestClass1.java","TestClass2.java","TestClass3.java",
                                                                            "TestClass4.java","TestClass5.java","TestClass6.java"));
    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src\\test\\java");
    }

    @Test
    public void testClassFileSearch() {
        List<File> files = FileFinder.getFiles("examples");
        Assert.assertEquals(6, files.size());
        for(File file: files) {
            Assert.assertTrue(FILES.contains(file.getName()));
        }
    }
}
