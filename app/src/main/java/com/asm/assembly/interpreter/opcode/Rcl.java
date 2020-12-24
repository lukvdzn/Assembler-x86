package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public class Rcl extends Instruction {
    private String op0;
    private String op1;

    public Rcl(String op0, String op1)
    {
        this.op0 = op0;
        this.op1 = op1;
    }

    @Override
    public void accept(AsmVisitor visitor)
    {
        visitor.visit(this);
    }

    public String getOp0() {
        return op0;
    }

    public String getOp1() {
        return op1;
    }
}
