package tw.jruletest.files.source;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Superclass for the source fields and methods from which SourceField and SourceMethod classes inherit
 *
 * @author Toby Wride
 * */

public class SourceMember {

    /**
     * Instance field for the full class name including the packages
     * */

    protected String fullClassName;

    /**
     * Instance field for the name of the member
     * */

    protected String name;

    /**
     * Instance field for the declared type of the member
     * */

    protected Type type;

    /**
     * Instance field for a boolean flag determining if the member is static
     * */

    protected boolean isStatic = false;

    /**
     * Instance field for a boolean flag determining if the member is public
     * */

    protected boolean isPublic = false;

    /**
     * Superclass constructor using the member object to determine the required details such as if the name and if the member is static and/or public
     *
     * @param member the member defined in the compiled source file
     * @param className the name of the class which defined the member
     * */

    public SourceMember(Member member, String className) {
        fullClassName = className;
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

    /**
     * Gets the name of the member
     *
     * @return the name of the member represented by this object
     * */

    public String getName() {
        return name;
    }

    /**
     * Gets the type of the member
     *
     * @return the type of the member represented by this object
     * */

    public Type getType() {
        return type;
    }

    /**
     * Gets the name of the class defining the member, including the packages
     *
     * @return the full name of the class defining the member
     * */

    public String getFullClassName() {
        return fullClassName;
    }
}
