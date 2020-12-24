package com.asm.assembly.interpreter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Operations {

    /**XOR*/
    public static String xorOp(String o1, String o2, Flags flags)
    {
        String result = "";

        //o1 will always be a register so we want the length of o2 to be the same
        o2 = straighten(o2, o1.length());

        for(int i = 0; i < o1.length(); i++)
        {
            if(o1.charAt(i) == o2.charAt(i))
                result += "0";
            else
                result += (o1.charAt(i) == '1' || o2.charAt(i) == '1') ? "1" : "0";
        }


        flags.resetFlags();

        //if the result is 0 than set zero flag
        if(result.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(result.charAt(0) == '1')
            flags.setSignBit(1);

        return result;
    }

    /**AND*/
    public static String andOp(String o1, String o2, Flags flags)
    {
        String result = "";

        o2 = straighten(o2, o1.length());

        for(int i = 0; i < o1.length(); i++)
        {
            if(o1.charAt(i) == '1' && o2.charAt(i) == '1')
                result += "1";
            else
                result += "0";
        }

        flags.resetFlags();

        //if the result is 0 than set zero flag
        if(result.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(result.charAt(0) == '1')
            flags.setSignBit(1);

        return result;
    }

    /**OR*/
    public static String orOp(String o1, String o2, Flags flags)
    {
        String result = "";

        o2 = straighten(o2, o1.length());

        for(int i = 0; i < o1.length(); i++)
        {
            if(o1.charAt(i) == '0' && o2.charAt(i) == '0')
                result += "0";
            else
                result += "1";
        }

        flags.resetFlags();

        //if the result is 0 than set zero flag
        if(result.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(result.charAt(0) == '1')
            flags.setSignBit(1);

        return result;
    }

    /**ADD*/
    public static String addOp(String o1, String o2, Flags flags)
    {
        String result = "";

        o2 = straighten(o2, o1.length());

        boolean carry = false;
        StringBuilder sb = new StringBuilder();

        for(int i = o1.length() -1; i >= 0; i--)
        {
            if(o1.charAt(i) == '0' && o2.charAt(i) == '0')
            {
                if(carry)
                    sb.insert(0, "1");
                else
                    sb.insert(0, "0");
                carry = false;
            }
            else if(o1.charAt(i) == '1' && o2.charAt(i) == '1')
            {
                if(carry)
                    sb.insert(0, "1");
                else
                    sb.insert(0, "0");
                carry = true;
            }else {
                if(carry) {
                    sb.insert(0, "0");
                    carry = true;
                }else
                    sb.insert(0, "1");
            }
        }

        result = sb.toString();

        flags.resetFlags();

        //carry bit
        if(carry)
            flags.setCarryBit(1);

        //negative + negative != positive || positive + positive != negative
        if((o1.charAt(0) == '1' && o2.charAt(0) == '1' && result.charAt(0) == '0') ||
                (o1.charAt(0) == '0' && o2.charAt(0) == '0' && result.charAt(0) == '1'))
            flags.setOverflowBit(1);

            //if the result is 0 than set zero flag
        if(result.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(result.charAt(0) == '1')
            flags.setSignBit(1);

        return result;
    }

    /**SUB*/
    public static String subOp(String o1, String o2, Flags flags)
    {
        String result = "";

        o2 = straighten(o2, o1.length());

        boolean carry = false;
        StringBuilder sb = new StringBuilder();

        for(int i = o1.length() - 1; i >= 0; i--)
        {
            if(o1.charAt(i) == '0' && o2.charAt(i) == '0')
            {
                if(carry)
                {
                    sb.insert(0, "1");
                }
                else
                    sb.insert(0, "0");
            }
            else if(o1.charAt(i) == '1' && o2.charAt(i) == '1')
            {
                if(carry)
                {
                    sb.insert(0, "1");
                }
                else
                    sb.insert(0, "0");
            }else if(o1.charAt(i) == '1' && o2.charAt(i) == '0')
            {
                if(carry)
                {
                    sb.insert(0, "0");
                    carry = false;
                }else
                    sb.insert(0,"1");

            }else if(o1.charAt(i) == '0' && o2.charAt(i) == '1')
            {
                if(carry)
                    sb.insert(0,"0");
                else
                {
                    sb.insert(0, "1");
                    carry = true;
                }
            }
        }

        result = sb.toString();

        flags.resetFlags();

        //carry bit
        if(carry)
            flags.setCarryBit(1);

        //positive - negative != negative || negative - positive != positive
        if((o1.charAt(0) == '0' && o2.charAt(0) == '1' && result.charAt(0) == '1') ||
                (o1.charAt(0) == '1' && o2.charAt(0) == '0' && result.charAt(0) == '0'))
            flags.setOverflowBit(1);

        //if the result is 0 than set zero flag
        if(result.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(result.charAt(0) == '1')
            flags.setSignBit(1);

        return result;
    }

    /**SHIFT LEFT*/
    public static String shiftLeft(String o1, String o2,  Flags flags)
    {
        int shift = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue();
        boolean carry = false;

        for(int i = 0; i < shift; i++)
        {
            carry = o1.charAt(0) == '1';
            o1 = o1.substring(1) + "0";
        }

        flags.resetFlags();

        if(carry)
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        return o1;
    }

    /**SHIFT RIGHT*/
    public static String shiftRight(String o1, String o2,  Flags flags)
    {
        int shift = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue();
        boolean carry = false;

        for(int i = 0; i < shift; i++)
        {
            carry = o1.charAt(o1.length() - 1) == '1';
            o1 = "0" + o1.substring(0, o1.length() - 1) ;
        }

        flags.resetFlags();

        if(carry)
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        return o1;
    }

    /**SHIFT ARITHMETIC RIGHT*/
    public static String sarOp(String o1, String o2, Flags flags)
    {
        int shift = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue();
        boolean carry = false;
        String highestBit = o1.charAt(0) == '1' ? "1" : "0";

        for(int i = 0; i < shift; i++)
        {
            carry = o1.charAt(o1.length() - 1) == '1';
            o1 = highestBit + o1.substring(0, o1.length() - 1) ;
        }

        flags.resetFlags();

        if(carry)
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        return o1;
    }

    /**ROTATE LEFT*/
    public static String rolOp(String o1, String o2, Flags flags)
    {
        char signBit = o1.charAt(0);
        int rotation = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue();
        boolean carry = false;

        for(int i = 0; i < rotation; i++)
        {
            carry = o1.charAt(0) == '1';
            o1 = o1.substring(1) + o1.charAt(0);
        }

        flags.resetFlags();

        if(carry)
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        //OF
        if(o1.charAt(0) != signBit)
            flags.setOverflowBit(1);

        return o1;
    }

    /**ROTATE RIGHT*/
    public static String rorOp(String o1, String o2, Flags flags)
    {
        char signBit = o1.charAt(0);
        int rotation = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue();
        boolean carry = false;

        for(int i = 0; i < rotation; i++)
        {
            carry = o1.charAt(o1.length() - 1) == '1';
            o1 = o1.charAt(o1.length() - 1) + o1.substring(0, o1.length() - 1);
        }

        flags.resetFlags();

        if(carry)
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        //OF
        if(o1.charAt(0) != signBit)
            flags.setOverflowBit(1);

        return o1;
    }

    /**ROTATE CARRY RIGHT*/
    public static String rcrOp(String o1, String o2, Flags flags)
    {
        char signBit = o1.charAt(0);
        int rotation = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue(); //negative numbers not supported
        char carry = flags.isCarryBit() == 1 ? '1' : '0';

        for(int i = 0; i < rotation; i++)
        {
            char newCarry = o1.charAt(o1.length() - 1);
            o1 = carry + o1.substring(0, o1.length() - 1);
            carry = newCarry;
        }

        flags.resetFlags();

        if(carry == '1')
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        //OF
        if(o1.charAt(0) != signBit)
            flags.setOverflowBit(1);

        return o1;
    }

    /**ROTATE CARRY LEFT*/
    public static String rclOp(String o1, String o2, Flags flags)
    {
        char signBit = o1.charAt(0);
        int rotation = new BigInteger(o2.charAt(0) == '1' ? "0" + o2 : o2, 2).intValue(); //negative numbers not supported
        char carry = flags.isCarryBit() == 1 ? '1' : '0';

        for(int i = 0; i < rotation; i++)
        {
            char newCarry = o1.charAt(0);
            o1 = o1.substring(1) + carry;
            carry = newCarry;
        }

        flags.resetFlags();

        if(carry == '1')
            flags.setCarryBit(1);

        //if the result is 0 than set zero flag
        if(o1.equals(new String(new char[o1.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(o1.charAt(0) == '1')
            flags.setSignBit(1);

        //OF
        if(o1.charAt(0) != signBit)
            flags.setOverflowBit(1);

        return o1;
    }

    /**NOT*/
    public static String notOp(String op)
    {
        String result = "";

        for(int i = 0; i < op.length(); i++)
        {
            result += (op.charAt(i) == '1') ? "0" : "1";
        }
        return result;
    }

    /**NEG*/
    public static String negOp(String op, Flags flags)
    {
        int current = new BigInteger(op, 2).intValue();

        if(current >= 0) // If its positive
        {
            op = notOp(op);
            //throwAway flags -- Add "00...01" to op to build negative complement
            op = addOp(op, new String(new char[op.length()]).replace("\0", "0").substring(1) + "1", new Flags());
        }else
        {
            //throwAway flags -- Subtract '1'= "00...01" to op to build negative complement
            op = subOp(op, new String(new char[op.length()]).replace("\0", "0").substring(1) + "1", new Flags());
            //not
            op = notOp(op);
        }

        flags.resetFlags();

        //if the result is 0 than set zero flag
        if(op.equals(new String(new char[op.length()]).replace("\0", "0")))
            flags.setZeroBit(1);
        else if(op.charAt(0) == '1')
            flags.setSignBit(1);

        return op;
    }

    /**MULTIPLICATION WITH BINARY*/
    public static String mulOp(String o1, String o2, Flags flags)
    {
        //MulOp only supports unsigned integers, if one operand is negative then it could result in a wrong answer
        int first = Integer.parseInt(convertBinToDec(o1.charAt(0) == '1' ? "0" + o1 : o1)); // if o1 is negative
        int second = Integer.parseInt(convertBinToDec(o2.charAt(0) == '1' ? "0" + o2 : o2));
        long result = first * second;
        String str = convertToBin(result + "", o1.length() * 2); // result will be the length of o1 * 2 eg. ax := al * bl
        String highPart = str.substring(0, o1.length());

        boolean resZero = new BigInteger(highPart, 2).intValue() == 0;
        if(!resZero)
        {
            //Mul will set the carry and overflow flag to 1,
            // if the higher part is not 0 eg. mul bl = ax *= bl * al so if ah is not 0 then cF and oF = 1
            flags.setCarryBit(1);
            flags.setOverflowBit(1);
        }
        if(result == 0)
            flags.setZeroBit(1);
        return str;
    }

    /**CONVERT HEX TO BINARY*/
    public static String straighten(String value, int desiredLength)
    {
        Map<Character, String> hexMap;
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

    /**CONVERT DEC TO BINARY*/
    public static String convertToBin(String dec, int desiredLength)
    {
        if(dec.contains("0x"))
        {
            return straighten(dec, desiredLength);
        }

        long rem = Integer.parseInt(dec);
        StringBuilder sb = new StringBuilder();

        if(rem == 0)
        {
            return new String(new char[desiredLength]).replace("\0", "0");
        }
        boolean signed = false;

        if(rem < 0)
        {
            signed = true;
            rem *= -1;
        }

        while(rem != 1)
        {
            if(rem % 2 == 0)
                sb.insert(0, "0");
            else
                sb.insert(0, "1");
            rem /= 2;
        }
        sb.insert(0, "1");

        if(sb.length() < desiredLength)
        {
            int d = desiredLength - sb.length();
            for(int i = 0; i < d; i++)
                sb.insert(0, "0");
        }

        if(signed)
        {
            String res = notOp(sb.toString());
            res = addOp(res, new String(new char[desiredLength - 1]).replace("\0", "0") + "1", new Flags());
            return res;
        }

        return sb.toString();
    }

    /**CONVERT BINARY TO HEX*/
    public static String convertToHex(String value)
    {
        Map<String, Character> binaryMap;
        binaryMap = new HashMap<>();
        binaryMap.put("0000", '0');
        binaryMap.put("0001", '1');
        binaryMap.put("0010", '2');
        binaryMap.put("0011", '3');
        binaryMap.put("0100", '4');
        binaryMap.put("0101", '5');
        binaryMap.put("0110", '6');
        binaryMap.put("0111", '7');
        binaryMap.put("1000", '8');
        binaryMap.put("1001", '9');
        binaryMap.put("1010", 'A');
        binaryMap.put("1011", 'B');
        binaryMap.put("1100", 'C');
        binaryMap.put("1101", 'D');
        binaryMap.put("1110", 'E');
        binaryMap.put("1111", 'F');

        String result = "";

        for(int i = 0; i < value.length(); i+= 4)
        {
            result += binaryMap.get(value.substring(i, i + 4));
        }

        return "0x" + result;
    }

    /**CONVERT DECIMAL TO HEX*/
    public static String convertDecToHex(String value)
    {
        int current = new BigInteger(value).intValue();
        StringBuilder sb = new StringBuilder();

        while(current > 15)
        {
            int remainder = current % 16;
            current /= 16;
            String hex = remainder < 10 ? "" + remainder :
                    new String[]{"A", "B", "C", "D", "E", "F"}[remainder - 10];
            sb.insert(0, hex);
        }
        sb.insert(0, current < 10 ? "" + current : new String[]{"A", "B", "C", "D", "E", "F"}[current - 10]);
        sb.insert(0, "0x");
        return sb.toString();
    }

    /**CONVERT BIN TO DEC*/
    public static String convertBinToDec(String value)
    {
        //If its signed
        if(value.charAt(0) == '1')
        {
            //Binary complement - 1 and notOp
            value = subOp(value, new String(new char[value.length() - 1]).replace("\0", "0") + 1, new Flags());
            value = notOp(value);
            int dec = new BigInteger(value, 2).intValue();
            return "-" + dec;
        }else {
            int dec = new BigInteger(value, 2).intValue();
            return "" + dec;
        }
    }

    public static int desiredBitLengthTable(String part)
    {
        if(part.equals("ax") || part.equals("bx") || part.equals("cx") || part.equals("dx") || part.equals("si") || part.equals("di") ||
                part.equals("bp") || part.equals("sp"))
            return 16;
        else if(part.equals("eax") || part.equals("ebx") || part.equals("ecx") || part.equals("edx")|| part.equals("edi")|| part.equals("esi")
                || part.equals("ebp") || part.equals("esp"))
            return 32;
        return 8;
    }

    public static boolean isNumber(String str)
    {
        try{
            if(str.contains("0x"))
                return true;
            Double.parseDouble(str);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}
