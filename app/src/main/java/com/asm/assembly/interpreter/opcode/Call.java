package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public class Call extends Instruction {
    private String address;

    public Call(String address)
    {
        this.address = address;
    }

    @Override
    public void accept(AsmVisitor visitor)
    {
        visitor.visit(this);
    }

    public String getAddress() {
        return address;
    }
}
