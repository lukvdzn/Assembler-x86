package com.asm.assembly.interpreter;

public class Stack
{
    private String [] stack;
    private int stackIndex;

    public Stack(int size)
    {
        stack = new String[size];
        stackIndex = 0;
    }

    public String[] getStack()
    {
        return stack;
    }

    /*Push value onto stack*/
    public void push(String value)
    {
        if(stackIndex < stack.length)
            stack[stackIndex++] = value;
    }

    /*Pops stack[stackIndex]*/
    public String pop()
    {
        String value = "0xFFFFFFFF";

        if(stackIndex > 0)
        {
            value = stack[stackIndex - 1];
            stack[stackIndex - 1] = "0x0";
            stackIndex--;
        }
        return value;
    }

    public void reserveSpaceOnCall(int retAdress, int framePointer, int arguments)
    {
        if(stackIndex + 1 < stack.length)
        {
            for (int i = stackIndex - 1; i >= stackIndex - arguments; i--)
            {
                stack[i + 2] = stack[i];
            }

           // stack[stackIndex - arguments] = retAdress;
            //sstack[stackIndex - arguments + 1] = framePointer;
            stackIndex += 2;
        }
    }
}

