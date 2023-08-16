package tw.jruletest.files;

import org.junit.*;
import tw.jruletest.files.FileFinder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFileFinder {
    @Before
    public void setup() {
        FileFinder.collectFiles(System.getProperty("user.dir") + "\\src");
    }

    @Test
    public void testFileSearch() {
        String[] expectedFiles = {"Class.java", "Example.java", "Test.java"};
        List<File> files = FileFinder.getFiles("programs");
        Assert.assertEquals(3, files.size());
        for(int i = 0; i < expectedFiles.length; i++) {
            Assert.assertEquals(expectedFiles[i], files.get(i).getName());
        }
    }

    @Test
    public void testGetDistinctPackageNames() {
        String topPackage = "C:\\Users\\toby\\Documents\\ComputerScience\\PersonalProjects\\java-test-rule-based\\src\\";
        String mainTop = "main\\java\\tw\\jruletest";
        String testTop = "test\\java\\tw\\jruletest";
        String[] expectedPackages = {mainTop + "\\analyzers", mainTop + "\\exceptions", mainTop + "\\expectations",
                                    mainTop + "\\files", mainTop + "\\files\\source", mainTop + "\\generators",
                                    mainTop + "\\logging", mainTop + "\\parse", mainTop + "\\parse\\ruletree\\argumentnodes",
                                    mainTop + "\\parse\\ruletree\\rulenodes", mainTop + "\\parse\\ruletree", mainTop,
                                    mainTop + "\\testing\\examples", mainTop + "\\testing\\examples\\subpackage1",
                                    mainTop + "\\testing\\examples\\subpackage2", mainTop + "\\testing\\programs",
                                    mainTop + "\\variables", mainTop + "\\virtualmachine", testTop + "\\analyzers",
                                    testTop + "\\examples", testTop + "\\expectations", testTop + "\\files",
                                    testTop + "\\generators", testTop + "\\parse\\parser", testTop + "\\parse\\ruletree\\argumentnodes",
                                    testTop + "\\parse\\ruletree\\rulenodes", testTop + "\\variables"};

        List<String> packages = FileFinder.getDistinctPackageNames();

        Assert.assertEquals(27, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetDistinctPackageNamesWithDirectory() {
        String topPackage = "C:\\Users\\toby\\Documents\\ComputerScience\\PersonalProjects\\java-test-rule-based\\src\\";
        String mainTop = "main\\java\\tw\\jruletest";
        String testTop = "test\\java\\tw\\jruletest";
        String[] expectedPackages = {mainTop + "\\parse", mainTop + "\\parse\\ruletree\\argumentnodes",
                                    mainTop + "\\parse\\ruletree\\rulenodes", mainTop + "\\parse\\ruletree", testTop + "\\parse\\parser",
                                    testTop + "\\parse\\ruletree\\argumentnodes", testTop + "\\parse\\ruletree\\rulenodes"};

        List<String> packages = FileFinder.getDistinctPackageNames("parse");
        Assert.assertEquals(7, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetDistinctPackageNamesWithClassList() {
        String topPackage = "C:\\Users\\toby\\Documents\\ComputerScience\\PersonalProjects\\java-test-rule-based\\src\\";
        String mainTop = "main\\java\\tw\\jruletest";
        String testTop = "test\\java\\tw\\jruletest";
        String[] expectedPackages = {mainTop + "\\files", mainTop + "\\files\\source", testTop + "\\files"};

        List<String> packages = FileFinder.getDistinctPackageNames(FileFinder.getFiles("files"));
        Assert.assertEquals(3, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetClassNameWithFile() {
        Assert.assertEquals("tw.jruletest.logging.TestLogger", FileFinder.getClassName(FileFinder.getFiles("logging").get(0)));
    }

    @Test
    public void testGetClassNameWithFilename() {
        Assert.assertEquals("tw.jruletest.logging.TestLogger", FileFinder.getClassName(FileFinder.getFiles("logging").get(0).getPath()));
    }

    @Test
    public void testGetClassNameWithFilenameAndRoot() {
        Assert.assertEquals("tw.jruletest.logging.TestLogger", FileFinder.getClassName(FileFinder.getFiles("logging").get(0).getPath(), "main\\java\\"));
    }
}
