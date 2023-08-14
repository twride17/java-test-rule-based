package tw.jruletest.variables;

import java.lang.reflect.Type;

public class Variable {

    private String name;
    private Type type;
    private boolean declared = false;

    public Variable(String name, Type type, boolean declared) {
        this.name = name;
        this.type = type;
        this.declared = declared;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isDeclared() {
        return declared;
    }

    public void makeDeclared() {
        declared = true;
    }
}
