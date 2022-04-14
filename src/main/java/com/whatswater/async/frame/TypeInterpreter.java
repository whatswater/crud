package com.whatswater.async.frame;


import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class TypeInterpreter extends BasicInterpreter {
    static TypeInterpreter instance = new TypeInterpreter();

    public TypeInterpreter() {
        super(ASM9);
    }

    @Override
    public BasicValue newValue(Type type) {
        if (type != null && (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY)) {
            return new BasicValue(type);
        }
        return super.newValue(type);
    }
}
