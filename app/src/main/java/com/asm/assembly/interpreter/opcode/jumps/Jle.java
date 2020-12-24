package com.asm.assembly.interpreter.opcode.jumps;

import com.asm.assembly.interpreter.AsmVisitor;
import com.asm.assembly.interpreter.Flags;

public class Jle extends Jump {
    public Jle(String address)
    {
        super(address);
    }

    @Override
    public void accept(AsmVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public boolean jumpConditionSatisfied(Flags flags) {
        if((flags.isSignBit() == 1 && flags.isCarryBit() == 1) || flags.isZeroBit() == 1)
            return true;
        return false;
    }
}
