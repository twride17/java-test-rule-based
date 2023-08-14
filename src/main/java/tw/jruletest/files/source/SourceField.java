package tw.jruletest.files.source;

import java.lang.reflect.Field;

public class SourceField extends SourceMember {

    public SourceField(Field field, String className) {
        super(field, className);
        type = field.getType();
    }
}
