package com.spaghettic0der.zehntausend.Helper;


import com.spaghettic0der.zehntausend.GameLogic.Dice;

import java.io.FileWriter;
import java.util.ArrayList;

public class Debug
{
    private static String filename = "debug.txt";

    private static String getTime()
    {
        return "";
    }

    public static String diceArrayListToString(ArrayList<Dice> arrayList)
    {
        StringBuilder stringBuilder = new StringBuilder(0);
        for (Dice dice : arrayList)
        {
            if (stringBuilder.capacity() < 1)
            {
                stringBuilder.append("\t" + dice.getDiceNumber());
            }
            else
            {
                stringBuilder.append(" - " + dice.getDiceNumber());
            }

        }
        return stringBuilder.toString();
    }

    public static String getClassName(Object object)
    {
        return object.getClass().getCanonicalName();
    }

    public static String getLineNumber()
    {
        return "Line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + ":";
    }

    private static void print(String text)
    {
        System.out.println(text);
    }

    public static void write(String text)
    {
        print(text);
        writeToFile(text);
    }

    public static void writeToFile(String text)
    {
        try
        {
            FileWriter fileWriter = new FileWriter(filename, true);
            fileWriter.append(text + "\n");
            fileWriter.close();
        }
        catch (Exception e)
        {

        }


    }
}
