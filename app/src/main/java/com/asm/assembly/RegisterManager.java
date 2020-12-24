package com.asm.assembly;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;

import com.asm.assembly.interpreter.reg.GeneralRegister;
import com.asm.assembly.interpreter.reg.Register;
import com.asm.assembly.interpreter.reg.SpecialRegister;

import java.util.HashMap;
import java.util.Map;

public class RegisterManager {

    private Map<String, Register> registerTable;
    private int [] textViewResources = {R.id.eax_value, R.id.ebx_value, R.id.ecx_value, R.id.edx_value,
        R.id.esi_value, R.id.edi_value, R.id.ebp_value, R.id.esp_value, R.id.carry_bit, R.id.zero_bit,
        R.id.overflow_bit, R.id.sign_bit};

    private int [] cardViewResources = {R.id.card_view_eax, R.id.card_view_ebx, R.id.card_view_ecx,
        R.id.card_view_edx, R.id.card_view_esi, R.id.card_view_edi, R.id.card_view_ebp, R.id.card_view_esp,
        R.id.card_view_carry, R.id.card_view_zero, R.id.card_view_overflow, R.id.card_view_sign};

    public static final int LAST_REGISTER_INDEX = 7;

    public RegisterManager(Context context)
    {
        GeneralRegister eax = new GeneralRegister(context, "EAX");
        GeneralRegister ebx = new GeneralRegister(context, "EBX");
        GeneralRegister ecx = new GeneralRegister(context, "ECX");
        GeneralRegister edx = new GeneralRegister(context, "EDX");
        SpecialRegister edi = new SpecialRegister("EDI", context);
        SpecialRegister esi = new SpecialRegister("ESI", context);
        SpecialRegister ebp = new SpecialRegister("EBP", context);
        SpecialRegister esp = new SpecialRegister("ESP", context);
        //Stack size should be set to 1028-- there are no elements on the stack
        esp.updateReg("med", "0000010000000100");

        registerTable = new HashMap<>();
        registerTable.put("eax", eax);
        registerTable.put("ebx", ebx);
        registerTable.put("ecx", ecx);
        registerTable.put("edx", edx);
        registerTable.put("edi", edi);
        registerTable.put("esi", esi);
        registerTable.put("ebp", ebp);
        registerTable.put("esp", esp);

        //Add ClickListener for Register CardViews
        initClickListener(context);
    }

    public String updateReg(String part, String value)
    {
        switch (part)
        {
            case "eax": return registerTable.get("eax").updateReg("ext", value);
            case "ax": return registerTable.get("eax").updateReg("med", value);
            case "ah": return registerTable.get("eax").updateReg("high", value);
            case "al": return registerTable.get("eax").updateReg("low", value);
            case "ebx": return registerTable.get("ebx").updateReg("ext", value);
            case "bx": return registerTable.get("ebx").updateReg("med", value);
            case "bh": return registerTable.get("ebx").updateReg("high", value);
            case "bl": return registerTable.get("ebx").updateReg("low", value);
            case "ecx": return registerTable.get("ecx").updateReg("ext", value);
            case "cx": return registerTable.get("ecx").updateReg("med", value);
            case "ch": return registerTable.get("ecx").updateReg("high", value);
            case "cl": return registerTable.get("ecx").updateReg("low", value);
            case "edx": return registerTable.get("edx").updateReg("ext", value);
            case "dx": return registerTable.get("edx").updateReg("med", value);
            case "dh": return registerTable.get("edx").updateReg("high", value);
            case "dl": return registerTable.get("edx").updateReg("low", value);
            case "esi": return registerTable.get("esi").updateReg("ext", value);
            case "si": return registerTable.get("esi").updateReg("med", value);
            case "edi": return registerTable.get("edi").updateReg("ext", value);
            case "di": return registerTable.get("edi").updateReg("med", value);
            case "ebp": return registerTable.get("ebp").updateReg("ext", value);
            case "bp": return registerTable.get("ebp").updateReg("med", value);
            case "esp": return registerTable.get("esp").updateReg("ext", value);
            case "sp": return registerTable.get("esp").updateReg("med", value);
        }
        return null;
    }

    public String getReg(String part)
    {
        switch (part)
        {
            case "eax": return registerTable.get("eax").getReg("ext");
            case "ax": return registerTable.get("eax").getReg("med");
            case "ah": return registerTable.get("eax").getReg("high");
            case "al": return registerTable.get("eax").getReg("low");
            case "ebx": return registerTable.get("ebx").getReg("ext");
            case "bx": return registerTable.get("ebx").getReg("med");
            case "bh": return registerTable.get("ebx").getReg("high");
            case "bl": return registerTable.get("ebx").getReg("low");
            case "ecx": return registerTable.get("ecx").getReg("ext");
            case "cx": return registerTable.get("ecx").getReg("med");
            case "ch": return registerTable.get("ecx").getReg("high");
            case "cl": return registerTable.get("ecx").getReg("low");
            case "edx": return registerTable.get("edx").getReg("ext");
            case "dx": return registerTable.get("edx").getReg("med");
            case "dh": return registerTable.get("edx").getReg("high");
            case "dl": return registerTable.get("edx").getReg("low");
            case "esi": return registerTable.get("esi").getReg("ext");
            case "si": return registerTable.get("esi").getReg("med");
            case "edi": return registerTable.get("edi").getReg("ext");
            case "di": return registerTable.get("edi").getReg("med");
            case "ebp": return registerTable.get("ebp").getReg("ext");
            case "bp": return registerTable.get("ebp").getReg("med");
            case "esp": return registerTable.get("esp").getReg("ext");
            case "sp": return registerTable.get("esp").getReg("med");
        }
        return null;
    }

    public String getFullRegister(String part)
    {
        switch (part)
        {
            case "eax": return "eax";
            case "ax": return "eax";
            case "ah": return "eax";
            case "al": return "eax";
            case "ebx": return "ebx";
            case "bx": return "ebx";
            case "bh": return "ebx";
            case "bl": return "ebx";
            case "ecx": return "ecx";
            case "cx": return "ecx";
            case "ch": return "ecx";
            case "cl": return "ecx";
            case "edx": return "edx";
            case "dx": return "edx";
            case "dh": return "edx";
            case "dl": return "edx";
            case "esi": return "esi";
            case "si": return "esi";
            case "edi": return "edi";
            case "di": return "edi";
            case "ebp": return "ebp";
            case "bp": return "ebp";
            case "esp": return "esp";
            case "sp": return "esp";
        }
        return null;
    }

    /**INSTRUCTIONS LIKE MUL NEEDS DIFFERENT PARTS OF EAX REGISTER */
    public String getRegEaxForMulDiv(String part)
    {
        if(part.equals("eax") ||part.equals("ebx") || part.equals("ecx") ||
                part.equals("edx") || part.equals("esi") || part.equals("edi") || part.equals("ebp") || part.equals("esp"))
            return "eax";
        else if(part.equals("ax") ||part.equals("bx") || part.equals("cx") || part.equals("dx") ||
                part.equals("si") || part.equals("di") || part.equals("bp") || part.equals("sp"))
            return "ax";

        return "al";
    }

    public int getViewResource(String part)
    {
        switch (part) {
            case "eax":
                return textViewResources[0];
            case "ax":
                return textViewResources[0];
            case "ah":
                return textViewResources[0];
            case "al":
                return textViewResources[0];
            case "ebx":
                return textViewResources[1];
            case "bx":
                return textViewResources[1];
            case "bh":
                return textViewResources[1];
            case "bl":
                return textViewResources[1];
            case "ecx":
                return textViewResources[2];
            case "cx":
                return textViewResources[2];
            case "ch":
                return textViewResources[2];
            case "cl":
                return textViewResources[2];
            case "edx":
                return textViewResources[3];
            case "dx":
                return textViewResources[3];
            case "dh":
                return textViewResources[3];
            case "dl":
                return textViewResources[3];
            case "esi":
                return textViewResources[4];
            case "edi":
                return textViewResources[5];
            case "ebp":
                return textViewResources[6];
            case "esp":
                return textViewResources[7];
            case "carry": return textViewResources[8];
            case "zero": return textViewResources[9];
            case "overflow": return textViewResources[10];
            case "sign": return textViewResources[11];
        }
        return 0;
    }

    public int getCardViewResources(String part)
    {
        switch (part) {
            case "eax": return cardViewResources[0];
            case "ebx": return cardViewResources[1];
            case "ecx": return cardViewResources[2];
            case "edx": return cardViewResources[3];
            case "esi":
                break;
            case "edi":
                break;
            case "ebp":
                break;
            case "esp":
                break;
            case "carry": return cardViewResources[8];
            case "zero": return cardViewResources[9];
            case "overflow": return cardViewResources[10];
            case "sign": return cardViewResources[11];
        }
        return 0;
    }

    public int [] getCardViewResources()
    {
        return cardViewResources;
    }

    public int [] getTextViewResources(){return textViewResources;}

    public void initClickListener(Context context)
    {
        CardView cardView = ((Activity)context).findViewById(cardViewResources[0]);
        cardView.setOnClickListener(registerTable.get("eax"));

        cardView = ((Activity)context).findViewById(cardViewResources[1]);
        cardView.setOnClickListener(registerTable.get("ebx"));

        cardView = ((Activity)context).findViewById(cardViewResources[2]);
        cardView.setOnClickListener(registerTable.get("ecx"));

        cardView = ((Activity)context).findViewById(cardViewResources[3]);
        cardView.setOnClickListener(registerTable.get("edx"));

        cardView = ((Activity)context).findViewById(cardViewResources[4]);
        cardView.setOnClickListener(registerTable.get("esi"));

        cardView = ((Activity)context).findViewById(cardViewResources[5]);
        cardView.setOnClickListener(registerTable.get("edi"));

        cardView = ((Activity)context).findViewById(cardViewResources[6]);
        cardView.setOnClickListener(registerTable.get("ebp"));

        cardView = ((Activity)context).findViewById(cardViewResources[7]);
        cardView.setOnClickListener(registerTable.get("esp"));
    }
}
