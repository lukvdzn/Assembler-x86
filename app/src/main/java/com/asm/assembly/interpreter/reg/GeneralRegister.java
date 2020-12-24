package com.asm.assembly.interpreter.reg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.asm.assembly.R;
import com.asm.assembly.interpreter.Operations;


import java.util.HashMap;
import java.util.Map;

public class GeneralRegister extends Register
{
    private static Map<Character, String> hexMap;
    private Dialog popUpDialog;

    public GeneralRegister(Context context, String name)
    {
        super(name);
                                //extended          //high      //low
        bytePart = new String[]{"0000000000000000", "00000000", "00000000"};

        hexMap  = new HashMap<>();
        hexMap = new HashMap<>();
        hexMap.put('0', "0000");
        hexMap.put('1', "0001");
        hexMap.put('2', "0010");
        hexMap.put('3', "0011");
        hexMap.put('4', "0100");
        hexMap.put('5', "0101");
        hexMap.put('6', "0110");
        hexMap.put('7', "0111");
        hexMap.put('8', "1000");
        hexMap.put('9', "1001");
        hexMap.put('A', "1010");
        hexMap.put('B', "1011");
        hexMap.put('C', "1100");
        hexMap.put('D', "1101");
        hexMap.put('E', "1110");
        hexMap.put('F', "1111");

        popUpDialog = new Dialog(context);
    }

    /**UPDATE REGISTERS BY REGISTER PARTS*/
    public String updateReg(String part, String value)
    {
        switch (part)
        {
            case "low": //eg CL
                bytePart[2] = straighten(value, 8);
                break;
            case "high": //eg CH
                bytePart[1] = straighten(value, 8);
                break;
            case "med":  //eg CX
                value = straighten(value, 16);
                bytePart[1] = value.substring(0, 8);
                bytePart[2] = value.substring(8);
                break;
            case "ext":  //eg ECX
                value = straighten(value, 32);
                bytePart[0] = value.substring(0, 16);
                bytePart[1] = value.substring(16, 24);
                bytePart[2] = value.substring(24);
                break;
        }

        return bytePart[0] + bytePart[1] + bytePart[2];
    }

    /**RETURN REGISTERS BY REGISTER PARTS*/
    public String getReg(String part)
    {
        switch (part)
        {
            case "low": return bytePart[2];
            case "high": return bytePart[1];
            case "med": return bytePart[1] + bytePart[2];
            default: return bytePart[0] + bytePart[1] + bytePart[2];
        }
    }

    /**CONVERT HEX TO BINARY*/
    public String straighten(String value, int desiredLength)
    {
        //if value is hex
        if(value.contains("x"))
        {
            StringBuilder sb = new StringBuilder();
            value = value.replace("0x", "").replace("0X", "");

            for(int i = value.length() - 1; i >= 0; i--)
            {
                char hex = value.charAt(i);
                sb.insert(0, hexMap.get(hex));
            }
            if(sb.length() != desiredLength)
            {
                int margin = desiredLength - sb.length();

                for(int i = 0; i < margin; i++)
                    sb.insert(0, "0");
            }
            return sb.toString();
        }else
        {
            //DECIMAL NOT SUPPORTED YET
            return value;
        }
    }

    @Override
    public void onClick(View v)
    {
        popUpDialog.setContentView(R.layout.register_popup);
        TextView registerName = popUpDialog.findViewById(R.id.register_name);
        TextView binValueText = popUpDialog.findViewById(R.id.register_value_bin);
        TextView hexValueText = popUpDialog.findViewById(R.id.register_value_hex);
        TextView decValueText = popUpDialog.findViewById(R.id.register_value_dec);

        binValueText.setText(getReg("ext"));
        hexValueText.setText(Operations.convertToHex(getReg("ext")));
        decValueText.setText(Operations.convertBinToDec(getReg("ext")));

        registerName.setText(name);
        popUpDialog.show();
    }
}
