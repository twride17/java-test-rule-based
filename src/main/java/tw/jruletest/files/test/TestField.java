package tw.jruletest.files.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Utility class used to represent a field defined by a class and provide methods capable of reading the represented field's value
 *
 * @author Toby Wride
 * */

public class TestField extends TestMember {

    /**
     * Instance field storing a reference to the original field being represented by this object
     * */

    private Field field;

    /**
     * TestField class constructor inherits from TestMember class
     *
     * @param field the defined field being represented by this object
     * */

    public TestField(Field field) {
        super(field);
        this.field = field;
    }

    /**
     * Reads the value stored in the field represented by this object
     *
     * @param instance an instantiation of the class defining this field
     *
     * @return the value stored in the field
     *
     * @throws IllegalAccessException thrown when the field cannot be accessed, probably because the field has not been made public
     * */

    public String readValue(Object instance) throws IllegalAccessException {
        if(field.getType().isArray()) {
            return readArrayValue(instance);
        } else {
            return (String)field.get(instance);
        }
    }

    /**
     * Reads all the values stored in an array field
     *
     * @param instance an instantiation of the class defining this field
     *
     * @return a string representation of all the array values concatenated together
     *
     * @throws IllegalAccessException thrown when the field cannot be accessed, probably because the field has not been made public
     * */

    public String readArrayValue(Object instance) throws IllegalAccessException {
        Object[] set = (Object[])field.get(instance);
        String ruleSet = "";
        for(Object rule: set) {
            ruleSet += rule + "\n";
        }
        return ruleSet;
    }

    /**
     * Static helper method used to re-order the fields stored in the provided list so that they are in order in which they were defined in the provided class
     *
     * @param className the name of the defining class
     * @param fields array of original fields to be re-ordered
     *
     * @return list of TestField objects representing the re-ordered fields
     * */

    public static ArrayList<TestField> orderFields(String className, Field[] fields) {
        ArrayList<TestField> orderedFields = new ArrayList<>();
        String filePath = System.getProperty("user.dir") + "\\src\\test\\java\\" + className.replaceAll("\\.", "\\\\") + ".java";
        try {
            BufferedReader javaReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filePath))));
            while(orderedFields.size() < fields.length) {
                String[] terms = javaReader.readLine().split(" ");
                for(String term: terms) {
                    if(!term.isEmpty()) {
                        for(Field field: fields) {
                            if(TestMember.nameMatches(field.getName(), term)) {
                                orderedFields.add(new TestField(field));
                            }
                        }
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(NullPointerException e) {}
        return orderedFields;
    }
}
