package com.asm.assembly.interpreter.reg;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.asm.assembly.R;
import com.asm.assembly.interpreter.Operations;

public class SpecialRegister extends Register
{
    private Dialog popUpDialog;

    public SpecialRegister(String name, Context context)
    {
        super(name);
                                //extended             //med
        bytePart = new String[]{"0000000000000000", "0000000000000000"};

        popUpDialog = new Dialog(context);
    }

    @Override
    public String updateReg(String part, String value)
    {
        if(part.equals("med"))
            bytePart[1] = value;
        else {
            bytePart[0] = value.substring(0, 16);
            bytePart[1] = value.substring(16);
        }
        return bytePart[0] + bytePart[1];
    }

    @Override
    public String getReg(String part)
    {
        if(part.equals("med"))
            return bytePart[1];
        return bytePart[0] + bytePart[1];
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
