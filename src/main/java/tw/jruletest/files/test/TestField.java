package tw.jruletest.files.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestField extends TestMember {

    private Field field;

    public TestField(Field field, String className) {
        super(field, className);
        this.field = field;
    }

    public String readValue(Object instance) throws IllegalAccessException {
        if(field.getType().isArray()) {
            return readArrayValue(instance);
        } else {
            return (String)field.get(instance);
        }
    }

    public String readArrayValue(Object instance) throws IllegalAccessException {
        Object[] set = (Object[])field.get(instance);
        String ruleSet = "";
        for(Object rule: set) {
            ruleSet += rule + "\n";
        }
        return ruleSet;
    }

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
                                orderedFields.add(new TestField(field, className));
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
