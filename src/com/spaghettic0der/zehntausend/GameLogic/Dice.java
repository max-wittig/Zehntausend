package com.spaghettic0der.zehntausend.GameLogic;

import java.util.Random;

public class Dice
{
    private static int i = 0;
    private final boolean isDebug = false;
    private int diceNumber;
    private transient Random random;

    public Dice()
    {
        if (isDebug)
        {
            i++;
            random = new Random(i);
        }
        else
        {
            random = new Random();
        }
    }

    public int getDiceNumber()
    {
        return diceNumber;
    }

    public void setDiceNumber(int diceNumber)
    {
        this.diceNumber = diceNumber;
    }

    public void roll()
    {
        diceNumber = random.nextInt(6)+1;
    }

    @Override
    public String toString()
    {
        return String.valueOf(diceNumber);
    }
}
