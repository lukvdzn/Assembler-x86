package com.asm.assembly.interpreter.opcode.jumps;

import com.asm.assembly.interpreter.AsmVisitor;
import com.asm.assembly.interpreter.Flags;

public class Jnl extends Jump {
    public Jnl(String address)
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
        if(flags.isSignBit() == 0 && flags.isCarryBit() == 0)
            return true;
        return false;
    }
}
