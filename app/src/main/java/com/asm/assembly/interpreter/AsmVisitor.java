package com.asm.assembly.interpreter;

import com.asm.assembly.interpreter.opcode.*;
import com.asm.assembly.interpreter.opcode.jumps.Jump;

public interface AsmVisitor {

    public void visit(Xor xor);
    public void visit(Mov mov);
    public void visit(And and);
    public void visit(Or or);
    public void visit(Add add);
    public void visit(Sub sub);
    public void visit(Dec dec);
    public void visit(Inc inc);
    public void visit(Shl shl);
    public void visit(Shr shr);
    public void visit(Cmp cmp);
    public void visit(Not not);
    public void visit(Neg neg);
    public void visit(Jump jump);
    public void visit(Label label);
    public void visit(Mul mul);
    public void visit(Div div);
    public void visit(Sar sar);
    public void visit(Sal sal);
    public void visit(Rol rol);
    public void visit(Ror ror);
    public void visit(Rcr rcr);
    public void visit(Rcl rcl);
    public void visit(Push push);
    public void visit(Pop pop);
    public void visit(Call call);
    public void visit(Ret ret);
}
