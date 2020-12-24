package com.asm.assembly;

public class Commands {
    public static final String [] oneOperandCodes = {
            "inc", "dec", "neg", "mul", "div", "not", "call", "jmp", "push", "pop", "jz", "jnz", "jc",
            "jnc", "jo", "jno", "Je", "jne", "jg", "jng", "js", "jns", "jl", "jnl", "jle", "jnle",
            "jge", "jnge", "idiv", "imul"
    };

    public static final String [] twoOperandCodes = {
            "add", "sub", "and", "xor", "or", "cmp", "sal", "sar", "shl", "shr", "rol", "ror", "rcr", "rcl", "mov"
    };
}
