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

    public static File findFile(String filename) {
        return findFile(filename, "");
    }

    public static File findFile(String filename, String topDirectory) {
        List<File> directoryFiles = getFiles(topDirectory);
        for(File file: directoryFiles) {
            if(file.getPath().contains(filename) && !file.getPath().contains("generated")) {
                return file;
            }
        }
        return null;
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

    public static List<String> getDistinctDirectoryNames(String sectionName) {
        List<String> directories = new ArrayList<>();
        List<File> javaFiles = getFiles(sectionName);
        for(File javaFile: javaFiles) {
            String name = javaFile.getPath();
            String directoryName = name.substring(0, name.lastIndexOf("\\"));
            if(!directories.contains(directoryName)) {
                directories.add(directoryName);
            }
        }
        return directories;
    }

    public static List<String> getClassNames(List<File> files, String topDir) {
        List<String> classNames = new ArrayList<>();
        for(File file: files) {
            classNames.add(getClassName(file.getPath(), topDir));
        }
        return classNames;
    }

    public static String getClassName(String className, String topDir) {
        return className.substring(className.indexOf(topDir)+topDir.length(), className.indexOf(".")).replaceAll("\\\\", ".");
    }
}
