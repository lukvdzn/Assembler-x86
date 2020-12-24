package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public class Ret extends Instruction {

    @Override
    public void accept(AsmVisitor visitor)
    {
        visitor.visit(this);
    }
}
