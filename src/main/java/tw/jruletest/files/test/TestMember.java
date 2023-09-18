package tw.jruletest.files.test;

import java.lang.reflect.Member;

public class TestMember {

    protected String fullClassName;
    protected String name;

    public TestMember(Member member, String className) {
        fullClassName = className;
        name = member.getName();
    }

    public String getName() {
        return name;
    }

    public String getFullClassName() {
        return fullClassName;
    }

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
