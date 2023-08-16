package tw.jruletest.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private static List<File> files = new ArrayList<>();

    public static void collectFiles(String topLevelFilename) {
        files = new ArrayList<>();
        collectFiles(new File(topLevelFilename));
    }

    private static void collectFiles(File topLevelFile) {
        File[] fileList = topLevelFile.listFiles();
        for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                files.add(file);
            }
            else if (file.isDirectory()) {
                collectFiles(file);
            }
        }
    }

    public static List<File> getFiles(String directory) {
        List<File> requiredFiles = new ArrayList<>();
        for(File file: files) {
            if(file.getPath().contains(directory)) {
                requiredFiles.add(file);
            }
        }
        return requiredFiles;
    }

    public static List<String> getDistinctPackageNames(String directory) {
        return getDistinctPackageNames(getFiles(directory));
    }

    public static List<String> getDistinctPackageNames() {
        return getDistinctPackageNames(FileFinder.getFiles("src"));
    }

    // Rewrite
    public static List<String> getDistinctPackageNames(List<File> classFiles) {
        List<String> directories = new ArrayList<>();
        for(File classFile: classFiles) {
            String name = classFile.getPath();
            String directoryName = name.substring(0, name.lastIndexOf("\\"));
            if(!directories.contains(directoryName)) {
                directories.add(directoryName);
            }
        }
        return directories;
    }

    public static List<String> getClassNames(List<File> files) {
        List<String> classNames = new ArrayList<>();
        for(File file: files) {
            classNames.add(getClassName(file.getPath()));
        }
        return classNames;
    }

    public static String getClassName(File file) {
        return getClassName(file.getPath());
    }

    public static String getClassName(String filename, String root) {
        return filename.substring(filename.indexOf(root) + root.length(), filename.lastIndexOf('.')).replaceAll("\\\\", ".");
    }

    public static String getClassName(String filename) {
        return getClassName(filename, "\\java\\");
    }
}
