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
        String[] expectedPackages = {mainTop + "\\analyzers", mainTop + "\\exceptions\\classanalysis", mainTop + "\\exceptions",
                                    mainTop + "\\exceptions\\parsing", mainTop + "\\expectations", mainTop + "\\files",
                                    mainTop + "\\files\\source", mainTop + "\\files\\test", mainTop + "\\generators",
                                    mainTop + "\\loggers", mainTop + "\\parse", mainTop + "\\parse\\ruletree\\innernodes\\argumentnodes",
                                    mainTop + "\\parse\\ruletree\\innernodes", mainTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\booleannodes",
                                    mainTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\mathematicalnodes",
                                    mainTop + "\\parse\\ruletree\\innernodes\\valuenodes", mainTop + "\\parse\\ruletree\\rootnodes",
                                    mainTop + "\\parse\\ruletree", mainTop, mainTop + "\\variables", mainTop + "\\virtualmachine",
                                    testTop + "\\analyzers", testTop + "\\examples\\sourceclasses", testTop + "\\examples\\sourceclasses\\programs",
                                    testTop + "\\examples\\sourceclasses\\subpackage1", testTop + "\\examples\\sourceclasses\\subpackage2",
                                    testTop + "\\examples\\testclasses", testTop + "\\expectations", testTop + "\\files\\source",
                                    testTop + "\\files\\test", testTop + "\\files", testTop + "\\generators",
                                    testTop + "\\parse\\parser", testTop + "\\parse\\ruletree\\innernodes\\argumentnodes",
                                    testTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\booleannodes",
                                    testTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\mathematicalnodes",
                                    testTop + "\\parse\\ruletree\\innernodes", testTop + "\\parse\\ruletree\\innernodes\\valuenodes",
                                    testTop + "\\parse\\ruletree\\rootnodes", testTop + "\\variables"};

        List<String> packages = FileFinder.getDistinctPackageNames();
        for(String packageName: packages) {
            System.out.println(packageName);
        }

        Assert.assertEquals(expectedPackages.length, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetDistinctPackageNamesWithDirectory() {
        String topPackage = "C:\\Users\\toby\\Documents\\ComputerScience\\PersonalProjects\\java-test-rule-based\\src\\";
        String mainTop = "main\\java\\tw\\jruletest";
        String testTop = "test\\java\\tw\\jruletest";
        String[] expectedPackages = {mainTop + "\\parse", mainTop + "\\parse\\ruletree\\innernodes\\argumentnodes",
                                        mainTop + "\\parse\\ruletree\\innernodes", mainTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\booleannodes",
                                        mainTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\mathematicalnodes",
                                        mainTop + "\\parse\\ruletree\\innernodes\\valuenodes", mainTop + "\\parse\\ruletree\\rootnodes",
                                        mainTop + "\\parse\\ruletree", testTop + "\\parse\\parser", testTop + "\\parse\\ruletree\\innernodes\\argumentnodes",
                                        testTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\booleannodes",
                                        testTop + "\\parse\\ruletree\\innernodes\\expressionnodes\\mathematicalnodes",
                                        testTop + "\\parse\\ruletree\\innernodes", testTop + "\\parse\\ruletree\\innernodes\\valuenodes",
                                        testTop + "\\parse\\ruletree\\rootnodes"};

        List<String> packages = FileFinder.getDistinctPackageNames("parse");
        Assert.assertEquals(expectedPackages.length, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetDistinctPackageNamesWithClassList() {
        String topPackage = "C:\\Users\\toby\\Documents\\ComputerScience\\PersonalProjects\\java-test-rule-based\\src\\";
        String mainTop = "main\\java\\tw\\jruletest";
        String testTop = "test\\java\\tw\\jruletest";
        String[] expectedPackages = {mainTop + "\\files", mainTop + "\\files\\source", mainTop + "\\files\\test",
                                    testTop + "\\files\\source",testTop + "\\files\\test", testTop + "\\files"};

        List<String> packages = FileFinder.getDistinctPackageNames(FileFinder.getFiles("files"));
        Assert.assertEquals(6, packages.size());
        for(int i = 0; i < expectedPackages.length; i++) {
            Assert.assertEquals(topPackage + expectedPackages[i], packages.get(i));
        }
    }

    @Test
    public void testGetClassNameWithFile() {
        Assert.assertEquals("tw.jruletest.loggers.CompilationLogger", FileFinder.getClassName(FileFinder.getFiles("loggers").get(0)));
    }

    @Test
    public void testGetClassNameWithFilename() {
        Assert.assertEquals("tw.jruletest.loggers.CompilationLogger", FileFinder.getClassName(FileFinder.getFiles("loggers").get(0).getPath()));
    }

    @Test
    public void testGetClassNameWithFilenameAndRoot() {
        Assert.assertEquals("tw.jruletest.loggers.CompilationLogger", FileFinder.getClassName(FileFinder.getFiles("loggers").get(0).getPath(), "main\\java\\"));
    }
}
