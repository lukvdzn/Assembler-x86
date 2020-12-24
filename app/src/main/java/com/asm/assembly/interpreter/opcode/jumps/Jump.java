package com.asm.assembly.interpreter.opcode.jumps;

import com.asm.assembly.interpreter.Flags;
import com.asm.assembly.interpreter.opcode.Instruction;

public abstract class Jump extends Instruction {

    protected String address;

    public Jump(String address)
    {
        this.address = address;
    }

    public abstract boolean jumpConditionSatisfied(Flags flags);

    public String getAddress()
    {
        return address;
    }
}
