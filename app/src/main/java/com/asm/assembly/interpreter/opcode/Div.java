package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public class Div extends Instruction {
    private String op0;

    public Div(String op0)
    {
        this.op0 = op0;
    }

    @Override
    public void accept(AsmVisitor visitor)
    {
        visitor.visit(this);
    }

    public String getOp0() {
        return op0;
    }
}
