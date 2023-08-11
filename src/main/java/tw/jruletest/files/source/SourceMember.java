package tw.jruletest.files.source;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class SourceMember {

    protected String name;
    protected Type type;
    protected boolean isStatic = false;
    protected boolean isPublic = false;

    public SourceMember(Member member) {
        name = member.getName();
        switch(member.getModifiers()) {
            case Modifier.PUBLIC:
                isPublic = true;
                break;
            case Modifier.STATIC:
                isStatic = true;
                break;
            case (Modifier.STATIC + Modifier.PUBLIC):
                isPublic = true;
                isStatic = true;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
