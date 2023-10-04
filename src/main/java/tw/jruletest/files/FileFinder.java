package tw.jruletest.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class providing methods capable of collecting Java files as well as finding files from a specific directory and getting the class name from a provided file.
 *
 * @author Toby Wride
 * */

public class FileFinder {

    /**
     * Static list of all Java files found within the root directory (usually 'src')
     * */

    private static List<File> files = new ArrayList<>();

    /**
     * Collects all Java files from within the root folder specified by the provided file name
     *
     * @param topLevelFilename the name of the folder from which to start the depth first search for Java files
     * */

    public static void collectFiles(String topLevelFilename) {
        files = new ArrayList<>();
        collectFiles(new File(topLevelFilename));
    }

    /**
     * Collects all Java files from within the root folder specified by the provided file object
     *
     * @param topLevelFile the folder from which to start the depth first search for Java files
     * */

    public static void collectFiles(File topLevelFile) {
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

    /**
     * Gets the files from within a specific sub-directory
     *
     * @param directory the name of the directory from which to find the files
     *
     * @return a list of all the files contained within the directory
     * */

    public static List<File> getFiles(String directory) {
        List<File> requiredFiles = new ArrayList<>();
        for(File file: files) {
            if(file.getPath().contains(directory)) {
                requiredFiles.add(file);
            }
        }
        return requiredFiles;
    }

    /**
     * Gets the package names for all the files located within the entire project with no duplicated entries
     *
     * @return list of distinct package names
     * */

    public static List<String> getDistinctPackageNames() {
        return getDistinctPackageNames(FileFinder.getFiles("src"));
    }

    /**
     * Gets the package names for all the files located within a specific directory with no duplicated entries
     *
     * @param directory the name of the root directory
     *
     * @return list of distinct package names
     * */

    public static List<String> getDistinctPackageNames(String directory) {
        return getDistinctPackageNames(getFiles(directory));
    }

    /**
     * Gets the package names for a specified set of classes with no duplicated entries
     *
     * @param classFiles the list of classes to get the package names from
     *
     * @return list of distinct package names
     * */

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

    /**
     * Gets the names of the classes from a specified list of files, including the packages
     *
     * @param files the list of classes from which to get the class names
     *
     * @return list of class names
     * */

    public static List<String> getClassNames(List<File> files) {
        List<String> classNames = new ArrayList<>();
        for(File file: files) {
            classNames.add(getClassName(file.getPath()));
        }
        return classNames;
    }

    /**
     * Gets the name of the class declared within the specified file.
     *
     * @param file a File object representing the file that defined the class
     *
     * @return the full name of the class
     * */

    public static String getClassName(File file) {
        return getClassName(file.getPath());
    }

    /**
     * Gets the name of the class identified by the specified file name using the top 'java' directory as the default root folder.
     * The file name is used to generate the file name, not the contents of the file.
     *
     * @param filename the name of the file that defined the class
     *
     * @return the full name of the class
     * */

    public static String getClassName(String filename) {
        return getClassName(filename, "\\java\\");
    }

    /**
     * Gets the name of the class identified by the specified file name using a specific folder as the root directory.
     * The file name is used to generate the file name, not the contents of the file.
     *
     * @param filename the name of the file that defined the class
     * @param root the name of the folder(s) that will act as the root folder
     *
     * @return the full name of the class
     * */

    public static String getClassName(String filename, String root) {
        return filename.substring(filename.indexOf(root) + root.length(), filename.lastIndexOf('.')).replaceAll("\\\\", ".");
    }
}
