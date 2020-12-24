package com.asm.assembly.interpreter.reg;

import android.view.View;

public abstract class Register implements View.OnClickListener
{
    protected String [] bytePart;
    protected String name;

    public Register(String name)
    {
        this.name = name;
    }

    public abstract String updateReg(String part, String value);

    public abstract String getReg(String part);
}
