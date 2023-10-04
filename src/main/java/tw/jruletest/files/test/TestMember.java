package tw.jruletest.files.test;

import java.lang.reflect.Member;

/**
 * Utility superclass used to represent a member defined by a class
 *
 * @author Toby Wride
 * */

public class TestMember {

    /**
     * Instance field storing the name of the field or method
     * */

    protected String name;

    /**
     * Superclass constructor from which TestField and TestMethod classes inherit
     *
     * @param member the original member represented by this object
     * */

    public TestMember(Member member) {
        name = member.getName();
    }

    /**
     * Gets the name of the represented member
     *
     * @return the name of the member
     * */

    public String getName() {
        return name;
    }

    /**
     * Static helper method used to determine if a line of code defines either a field or method
     *
     * @param name the name of the field or method that is being checked for a match
     * @param code the line of code to check against
     *
     * @return true if the name matches the code representation and false if not
     * */

    public static boolean nameMatches(String name, String code) {
        if(name.contains(".")) {
            name = name.substring(name.lastIndexOf("."));
        }

        if(code.contains("(")) {
            return name.equals(code.substring(0, code.indexOf("(")));
        }
        else if(code.contains(";")){
            return name.equals(code.substring(0, code.indexOf(";")));
        }
        else {
            return name.equals(code);
        }
    }
}
