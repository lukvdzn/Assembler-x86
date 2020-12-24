package com.asm.assembly.interpreter;

public class Flags {
    private int carryBit;
    private int overflowBit;
    private int signBit;
    private int zeroBit;

    public Flags()
    {
        carryBit = 0;
        overflowBit = 0;
        signBit = 0;
        zeroBit = 0;
    }

    public int isCarryBit() {
        return carryBit;
    }

    public int isOverflowBit() {
        return overflowBit;
    }

    public int isSignBit() {
        return signBit;
    }

    public int isZeroBit() {
        return zeroBit;
    }

    public void setCarryBit(int carryBit) {
        this.carryBit = carryBit;
    }

    public void setOverflowBit(int overflowBit) {
        this.overflowBit = overflowBit;
    }

    public void setSignBit(int signBit) {
        this.signBit = signBit;
    }

    public void setZeroBit(int zeroBit) {
        this.zeroBit = zeroBit;
    }

    public void resetFlags()
    {
        zeroBit = 0;
        carryBit = 0;
        signBit = 0;
        overflowBit = 0;
    }
}
