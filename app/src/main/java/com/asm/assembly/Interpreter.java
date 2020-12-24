package com.asm.assembly;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.asm.assembly.interpreter.Stack;
import com.asm.assembly.interpreter.opcode.*;
import com.asm.assembly.interpreter.AsmVisitor;
import com.asm.assembly.interpreter.Flags;
import com.asm.assembly.interpreter.opcode.jumps.*;
import com.asm.assembly.interpreter.Operations;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Interpreter implements AsmVisitor
{
    private Context context;
    private TableLayout tableLayout;

    private RegisterManager registerManager;
    private String [] tasks;
    private ArrayList<Instruction> instructions;
    private Map<String, Integer> labelMap;
    private Map<String, Integer> functionMap;

    private int programCounter;
    private Flags flags;
    private Stack stack;

    public Interpreter(String [] tasks, Context context, TableLayout tableLayout)
    {
        this.context = context;
        this.registerManager = new RegisterManager(context);
        this.tasks = tasks;
        this.tableLayout = tableLayout;

        instructions = new ArrayList<>();
        this.labelMap = new HashMap<>();
        this.functionMap = new HashMap<>();
        programCounter = 0;
        flags = new Flags();
        stack = new Stack(1024);

        try {
            translate();
        }catch (Exception e)
        {
            Log.d("ERROR", e.toString());
        }
    }

    /**DEBUG STEP BY STEP*/
    public void debug()
    {
        /**IN THE FUNCTION CMAIN WHEN RET WILL BE EXECUTED IT WILL POP THE RETURN ADDRESS FOR THE NEXT EXECUTION;
         * HOWEVER DUE TO THE FACT THAT THIS WILL BE THE FINAL INSTRUCTION AND THERE ARE NO ELEMENTS ON THE STACK
         * TO BE POPPED, THE STACK WILL RETURN 0xFFFFFFFF -> -1, SO THE PROGRAM STOPS DEBUGGING*/
        if(programCounter != -1)
        {
            setInstructionCardViewBackground();
            instructions.get(programCounter).accept(this);
        }
        else {
                Snackbar.make(((Activity) context).findViewById(R.id.eax_text), "end of instructions",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    /**STEP OVER THE FUNCTION*/
    public void stepOver()
    {
        if(programCounter != -1)
        {
            int next = programCounter + 1;
            setInstructionCardViewBackground();
            while(programCounter != next && programCounter != -1)
            {
                instructions.get(programCounter).accept(this);
            }
        }
        else {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text), "end of instructions",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void translate() throws Exception
    {
        for(int i = 0; i < tasks.length; i++)
        {
            Instruction instruction = processTask(tasks[i], i);
            instructions.add(instruction);
        }
    }

    /**PROCESS THE INSTRUCTION FROM THE STRINGTASK */
    public Instruction processTask(String task, int index) throws Exception
    {
        if(!task.contains(":"))
        {
            String [] instructionSet = getInstructionSet(task);
            switch (instructionSet[0])
            {
                //Imul and Idiv not included yet
                case "xor": return new Xor(instructionSet[1], instructionSet[2]);
                case "mov": return new Mov(instructionSet[1], instructionSet[2]);
                case "or": return new Or(instructionSet[1], instructionSet[2]);
                case "and": return new And(instructionSet[1], instructionSet[2]);
                case "add": return new Add(instructionSet[1], instructionSet[2]);
                case "sub": return new Sub(instructionSet[1], instructionSet[2]);
                case "neg": return new Neg(instructionSet[1]);
                case "not": return new Not(instructionSet[1]);
                case "cmp": return new Cmp(instructionSet[1], instructionSet[2]);
                case "inc": return new Inc(instructionSet[1]);
                case "dec": return new Dec(instructionSet[1]);
                case "div": return new Div(instructionSet[1]);
                case "mul": return new Mul(instructionSet[1]);
                case "shl": return new Shl(instructionSet[1], instructionSet[2]);
                case "shr": return new Shr(instructionSet[1], instructionSet[2]);
                case "sal": return new Shl(instructionSet[1], instructionSet[2]); // sal identical to shl
                case "sar": return new Sar(instructionSet[1], instructionSet[2]);
                case "rol": return new Rol(instructionSet[1], instructionSet[2]);
                case "ror": return new Ror(instructionSet[1], instructionSet[2]);
                case "rcr": return new Rcr(instructionSet[1], instructionSet[2]);
                case "rcl": return new Rcl(instructionSet[1], instructionSet[2]);
                case "xchg": break;
                case "call": return new Call(instructionSet[1]);
                case "ret": return new Ret();
                case "push": return new Push(instructionSet[1]);
                case "pop": return new Pop(instructionSet[1]);

                case "jmp": return new Jmp(instructionSet[1]);
                case "jz": return new Jz(instructionSet[1]);
                case "jnz": return new Jnz(instructionSet[1]);
                case "jc": return new Jc(instructionSet[1]);
                case "jnc": return new Jnc(instructionSet[1]);
                case "jo": return new Jo(instructionSet[1]);
                case "jno": return new Jno(instructionSet[1]);
                case "je": return new Je(instructionSet[1]);
                case "jne": return new Jne(instructionSet[1]);
                case "jg": return new Jg(instructionSet[1]);
                case "jng": return new Jng(instructionSet[1]);
                case "js": return new Js(instructionSet[1]);
                case "jns": return new Jns(instructionSet[1]);
                case "jl": return new Jl(instructionSet[1]);
                case "jnl": return new Jnl(instructionSet[1]);
                case "jle": return new Jle(instructionSet[1]);
                case "jnle": return new Jnle(instructionSet[1]);
                case "jge": return new Jge(instructionSet[1]);
                case "jnge": return new Jnge(instructionSet[1]);
            }
        }else
        {
            if(!task.contains("  ")) // if its a function
            {
                String name = task.replace("  ", "").replace(":", "").toLowerCase();
                functionMap.put(name, index);
                return new Label(name);
            }else {
                //If its a jump label
                String name = task.replace(":", "").replace("  ", "").toLowerCase();
                labelMap.put(name, index);
                return new Label(name);
            }
        }
        return null;
    }

    /**INSTRUCTION SET*/
    private String [] getInstructionSet(String instruction) throws Exception
    {
        instruction = instruction.replace("  ", "");

        //if its not a label
        if(!instruction.contains(":"))
        {
            //If its an instruction with two operands eg. MOV EAX, 0x1
            if(instruction.contains(","))
            {
                String command = instruction.split(",")[0].split(" ")[0].toLowerCase();
                String [] operands = instruction.replaceAll(command, "")
                        .replaceAll(" ", "").split(",");
                for(int i = 0; i < operands.length; i++)
                {
                    if(!(operands[i].contains("0x") || operands[i].contains("0X")))
                    {
                        //If its a register
                        if(registerManager.getFullRegister(operands[i]) != null)
                            operands[i] = operands[i].toLowerCase();
                        else // if its a decimal Value
                            operands[i] = Operations.convertDecToHex(operands[i]);
                    }else
                    {
                        operands[i] = "0x" + operands[i].replace("0x", "").replace("0X", "").toUpperCase();
                    }
                }
                return new String[]{command, operands[0], operands[1]};
            }else //INSTRUCTIONS LIKE MUL EBX
            {
                if(instruction.toLowerCase().contains("ret"))
                    return new String[]{"ret"};

                return new String[]{instruction.split(" ")[0].toLowerCase(),
                        instruction.split(" ")[1].toLowerCase()};
            }
        }
        return null;
    }

    @Override
    public void visit(Xor xor)
    {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(xor.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(xor.getOp1()) != null ?
                registerManager.getReg(xor.getOp1()) : xor.getOp1();

        //pass the flags for operations to modify
        String result = Operations.xorOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(xor.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(xor.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(xor.getOp0()));
        textView.setText(result);
        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});


        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Mov mov)
    {
        //first operand is always a register
        String reg = mov.getOp0();
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(mov.getOp1()) != null ?
                registerManager.getReg(mov.getOp1()) : mov.getOp1();

        if(secondValue.contains("0x"))
            secondValue = Operations.straighten(secondValue, Operations.desiredBitLengthTable(mov.getOp0()));

        registerManager.updateReg(reg, secondValue);
        //convert back to hex
        secondValue = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(mov.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(mov.getOp0()));
        textView.setText(secondValue);
        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(And and)
    {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(and.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(and.getOp1()) != null ?
                registerManager.getReg(and.getOp1()) : and.getOp1();

        //pass the flags for operations to modify
        String result = Operations.andOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(and.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(and.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(and.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Or or) {

        //first operand is always a register
        String valueOfReg = registerManager.getReg(or.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(or.getOp1()) != null ?
                registerManager.getReg(or.getOp1()) : or.getOp1();

        //pass the flags for operations to modify
        String result = Operations.orOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(or.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(or.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(or.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});


        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Add add) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(add.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(add.getOp1()) != null ?
                registerManager.getReg(add.getOp1()) : add.getOp1();

        //pass the flags for operations to modify
        String result = Operations.addOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(add.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(add.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(add.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});


        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Sub sub)
    {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(sub.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(sub.getOp1()) != null ?
                registerManager.getReg(sub.getOp1()) : sub.getOp1();

        //pass the flags for operations to modify
        String result = Operations.subOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(sub.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(sub.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(sub.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});


        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Dec dec) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(dec.getOp0());
        //if the second operand is also a register, then return its value

        Flags throwAway = new Flags();
        //pass throwAway flags for operations to modify
        String result = Operations.subOp(valueOfReg, "0x1", throwAway);

        //free memory
        throwAway = null;

        //update the register
        registerManager.updateReg(dec.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(dec.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(dec.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Inc inc) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(inc.getOp0());
        //if the second operand is also a register, then return its value

        Flags throwAway = new Flags();
        //pass throwAway flags for operations to modify
        String result = Operations.addOp(valueOfReg, "0x1", throwAway);

        //free memory
        throwAway = null;

        //update the register
        registerManager.updateReg(inc.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(inc.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(inc.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Shl shl) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(shl.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(shl.getOp1()) && !shl.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        String secondValue = registerManager.getReg(shl.getOp1()) != null ?
                registerManager.getReg(shl.getOp1()) : shl.getOp1();

        //If its decimal or hexadecimal
        if(!shl.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.shiftLeft(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(shl.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(shl.getOp0())));

       TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(shl.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Shr shr) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(shr.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(shr.getOp1()) && !shr.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(shr.getOp1()) != null ?
                registerManager.getReg(shr.getOp1()) : shr.getOp1();

        //If its decimal or hexadecimal
        if(!shr.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.shiftRight(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(shr.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(shr.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(shr.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});


        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Cmp cmp)
    {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(cmp.getOp0());
        //if the second operand is also a register, then return its value
        String secondValue = registerManager.getReg(cmp.getOp1()) != null ?
                registerManager.getReg(cmp.getOp1()) : cmp.getOp1();

        //pass the flags for operations to modify && we dont need the result
        Operations.subOp(valueOfReg, secondValue, flags);

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Not not) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(not.getOp0());

        String result = Operations.notOp(valueOfReg);
        //update the register
        registerManager.updateReg(not.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(not.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(not.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Neg neg) {

        //first operand is always a register
        String valueOfReg = registerManager.getReg(neg.getOp0());

        //pass the flags for operations to modify
        String result = Operations.negOp(valueOfReg, flags);
        //update the register
        registerManager.updateReg(neg.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(neg.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(neg.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});
        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Jump jump)
    {
        String address = jump.getAddress();
        if(!labelMap.containsKey(address))
        {
            Snackbar.make(tableLayout, "Unknown Label or Function name used", Snackbar.LENGTH_LONG).show();
            return;
        }

        //If the condition is satisfied
        if(jump.jumpConditionSatisfied(flags))
            programCounter = labelMap.get(address);
        else {
            //increment program counter
            programCounter++;
        }
    }

    @Override
    public void visit(Label label)
    {
        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Mul mul) {
        //first operand is register EAX
        String eaxPart = registerManager.getRegEaxForMulDiv(mul.getOp0());
        String valueOfEax = registerManager.getReg(eaxPart);
        String secondValue = registerManager.getReg(mul.getOp0());

        //pass the flags for operations to modify
        String result = Operations.mulOp(valueOfEax, secondValue, flags);

        //Register Update
        if(eaxPart.equals("al"))
        {
            registerManager.updateReg("ax", result);
            //convert back to hex
            result = Operations.convertToHex(registerManager.getReg("eax"));
            TextView textView = ((Activity) context).findViewById(
                    registerManager.getViewResource("eax"));
            textView.setText(result);

            //set Colors of Register TextViews
            setRegisterModifiedState(new TextView[]{textView});
        }
        else if(eaxPart.equals("ax"))
        {
            registerManager.updateReg("dx", result.substring(0, 16));
            String hex = Operations.convertToHex(registerManager.getReg("edx"));

            TextView textView = ((Activity) context).findViewById(
                    registerManager.getViewResource("edx"));
            textView.setText(hex);

            registerManager.updateReg("ax", result.substring(16));
            hex = Operations.convertToHex(registerManager.getReg("eax"));
            TextView textView1 = ((Activity) context).findViewById(
                    registerManager.getViewResource("eax"));
            textView1.setText(hex);

            //set Colors of Register TextViews
            setRegisterModifiedState(new TextView[]{textView, textView1});

        }else if(eaxPart.equals("eax"))
        {
            registerManager.updateReg("edx", result.substring(0, 32));

            String hex = Operations.convertToHex(registerManager.getReg("edx"));
            TextView textView = ((Activity) context).findViewById(
                    registerManager.getViewResource("edx"));
            textView.setText(hex);

            registerManager.updateReg("eax", result.substring(32));

            hex = Operations.convertToHex(registerManager.getReg("eax"));
            TextView textView1 = ((Activity) context).findViewById(
                    registerManager.getViewResource("eax"));
            textView1.setText(hex);

            //set Colors of Register TextViews
            setRegisterModifiedState(new TextView[]{textView, textView1});
        }

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Div div) {

    }

    @Override
    public void visit(Sar sar) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(sar.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(sar.getOp1()) && !sar.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(sar.getOp1()) != null ?
                registerManager.getReg(sar.getOp1()) : sar.getOp1();

        //If its decimal or hexadecimal
        if(!sar.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.sarOp(valueOfReg, secondValue, flags);
        //update the register
        registerManager.updateReg(sar.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(sar.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(sar.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Sal sal) {
        //NOT NEEDED
    }

    @Override
    public void visit(Rol rol) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(rol.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(rol.getOp1()) && !rol.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(rol.getOp1()) != null ?
                registerManager.getReg(rol.getOp1()) : rol.getOp1();

        //If its decimal or hexadecimal
        if(!rol.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.rolOp(valueOfReg, secondValue, flags);

        //update the register
        registerManager.updateReg(rol.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(rol.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(rol.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Ror ror) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(ror.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(ror.getOp1()) && !ror.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(ror.getOp1()) != null ?
                registerManager.getReg(ror.getOp1()) : ror.getOp1();

        //If its decimal or hexadecimal
        if(!ror.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.rorOp(valueOfReg, secondValue, flags);

        //update the register
        registerManager.updateReg(ror.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(ror.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(ror.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Rcr rcr) {

        //first operand is always a register
        String valueOfReg = registerManager.getReg(rcr.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(rcr.getOp1()) && !rcr.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(rcr.getOp1()) != null ?
                registerManager.getReg(rcr.getOp1()) : rcr.getOp1();

        //If its decimal or hexadecimal
        if(!rcr.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.rcrOp(valueOfReg, secondValue, flags);

        //update the register
        registerManager.updateReg(rcr.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(rcr.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(rcr.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Rcl rcl) {
        //first operand is always a register
        String valueOfReg = registerManager.getReg(rcl.getOp0());

        //if the second operand is not cl, than exception
        if(!Operations.isNumber(rcl.getOp1()) && !rcl.getOp1().equals("cl")) {
            Snackbar.make(((Activity) context).findViewById(R.id.eax_text),
                    "shl can only accept an immediate value or the register cl",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String secondValue = registerManager.getReg(rcl.getOp1()) != null ?
                registerManager.getReg(rcl.getOp1()) : rcl.getOp1();

        //If its decimal or hexadecimal
        if(!rcl.getOp1().equals("cl"))
            secondValue = Operations.convertToBin(secondValue, valueOfReg.length());

        //pass the flags for operations to modify
        String result = Operations.rclOp(valueOfReg, secondValue, flags);

        //update the register
        registerManager.updateReg(rcl.getOp0(), result);

        //convert back to hex
        result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(rcl.getOp0())));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(rcl.getOp0()));
        textView.setText(result);

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Push push)
    {
        //first operand is always a register or memory address
        String valueOfReg = registerManager.getReg(push.getOp0());
        String hex = Operations.convertToHex(valueOfReg);

        stack.push(hex);

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource("esp"));
        String currentValue = textView.getText().toString();
        int current = new BigInteger(currentValue.replace("0x", "0"), 16).intValue();
        current -= 4;
        textView.setText(Operations.convertDecToHex(current + ""));

        registerManager.updateReg("esp", Operations.convertToBin(current + "", 32));

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        //Update Flags
        updateFlags();

        //increment program counter
        programCounter++;
    }

    @Override
    public void visit(Pop pop)
    {
        //first operand is always a register or memory address
        String register = pop.getOp0();
        String hex = stack.pop();
        int bitLength = Operations.desiredBitLengthTable(register);

          //update the register
        registerManager.updateReg(register, Operations.straighten(hex, bitLength));

        //convert back to hex
        String result = Operations.convertToHex(registerManager.getReg(
                registerManager.getFullRegister(register)));

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource(register));
        textView.setText(result);

        TextView textView1 = ((Activity) context).findViewById(
                registerManager.getViewResource("esp"));
        String currentValue = textView1.getText().toString();
        int current = new BigInteger(currentValue.replace("0x", "0"), 16).intValue();
        current += 4;
        textView1.setText(Operations.convertDecToHex(current + ""));

        registerManager.updateReg("esp", Operations.convertToBin(current + "", 32));

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView, textView1});

        //Update Flags
        updateFlags();

        programCounter++;
    }

    @Override
    public void visit(Call call) {
        String returnAddress = Operations.convertDecToHex(programCounter + 1 + "");
        stack.push(returnAddress);

        String address = call.getAddress();
        if(!functionMap.containsKey(address))
        {
            Snackbar.make(tableLayout, "Unknown Label or Function name used", Snackbar.LENGTH_LONG).show();
            return;
        }

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource("esp"));
        String currentValue = textView.getText().toString();
        int current = new BigInteger(currentValue.replace("0x", "0"), 16).intValue();
        current -= 4;
        textView.setText(Operations.convertDecToHex(current + ""));

        registerManager.updateReg("esp", Operations.convertToBin(current + "", 32));

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});

        programCounter = functionMap.get(address);
    }

    @Override
    public void visit(Ret ret) {
        String returnAddress = stack.pop();
        returnAddress = Operations.convertBinToDec(Operations.straighten(returnAddress, 32));
        programCounter = Integer.parseInt(returnAddress);

        TextView textView = ((Activity) context).findViewById(
                registerManager.getViewResource("esp"));
        String currentValue = textView.getText().toString();
        int current = new BigInteger(currentValue.replace("0x", "0"), 16).intValue();
        current += 4;
        textView.setText(Operations.convertDecToHex(current + ""));

        registerManager.updateReg("esp", Operations.convertToBin(current + "", 32));

        //set Colors of Register TextViews
        setRegisterModifiedState(new TextView[]{textView});
    }

    public void updateFlags()
    {
        TextView carryTextView = ((TextView)((Activity) context).findViewById(
                registerManager.getViewResource("carry")));
        TextView zeroTextView = ((TextView)((Activity) context).findViewById(
                registerManager.getViewResource("zero")));
        TextView overFlowTextView = ((TextView)((Activity) context).findViewById(
                registerManager.getViewResource("overflow")));
        TextView signTextView = ((TextView)((Activity) context).findViewById(
                registerManager.getViewResource("sign")));
        /*
        int oldCarryBit = Integer.parseInt(carryTextView.getText().toString());
        int oldZeroBit = Integer.parseInt(zeroTextView.getText().toString());
        int oldOverflowBit = Integer.parseInt(overFlowTextView.getText().toString());
        int oldSignBit = Integer.parseInt(signTextView.getText().toString());
        */
        int newCarryBit = flags.isCarryBit(); int newZeroBit = flags.isZeroBit();
        int newOverflowBit = flags.isOverflowBit(); int newSignBit = flags.isSignBit();

        carryTextView.setText("" + newCarryBit);
        zeroTextView.setText("" + newZeroBit);
        overFlowTextView.setText("" + newOverflowBit);
        signTextView.setText("" + newSignBit);
    }

    /**SET THE CHANGED REGISTER TEXTVIEWS TO A YELLOW TONE, RESET THE OTHER ONES*/
    public void setRegisterModifiedState(TextView[] textViews)
    {
        for(int i = 0; i <= registerManager.LAST_REGISTER_INDEX; i++)
        {
            TextView tempView = ((Activity)context).findViewById(registerManager.getTextViewResources()[i]);
            tempView.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.white));
        }

        for(TextView t: textViews) {
            t.setTextColor(
                    ContextCompat.getColorStateList(context, R.color.asmModifiedRegister));
        }
    }

    /**CHANGE THE ACTIVE INSTRUCTION CARDVIEW BACKGROUND TO A YELLOW TONE AND RESET THE OTHER CARDVIEW BACKGROUNDS*/
    public void setInstructionCardViewBackground()
    {
        for(int i = 0; i < instructions.size(); i++)
        {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            TextView textView = row.findViewById(R.id.text_container);
            if(!textView.getText().toString().contains(":"))
                textView.setTextColor(context.getColor(R.color.white));
        }

        TableRow row = (TableRow) tableLayout.getChildAt(programCounter);
        //The selected Instruction should have a yellow color
        TextView textView = row.findViewById(R.id.text_container);
        textView.setTextColor(context.getColor(R.color.asmModifiedRegister));
    }
}
