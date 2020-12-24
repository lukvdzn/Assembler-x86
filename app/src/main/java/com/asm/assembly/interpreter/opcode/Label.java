package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public class Label extends Instruction
{
    private String name;

    public Label(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(AsmVisitor visitor) {
        visitor.visit(this);
    }
}
