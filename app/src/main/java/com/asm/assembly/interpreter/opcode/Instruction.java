package com.asm.assembly.interpreter.opcode;

import com.asm.assembly.interpreter.AsmVisitor;

public abstract class Instruction {
  public abstract void accept(AsmVisitor visitor);
}
