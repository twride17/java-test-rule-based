package tw.jruletest.files.source;

import java.lang.reflect.Field;

/**
 * Class which represents a field from a compiled and loaded source class
 *
 * @author Toby Wride
 * */

public class SourceField extends SourceMember {

    /**
     * SourceField constructor extends from the SourceMember class
     *
     * @param field the class's declared field
     * @param className the name of the class which declared the field
     * */

    public SourceField(Field field, String className) {
        super(field, className);
        type = field.getType();
    }
}
