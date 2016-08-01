package com.spaghettic0der.zehntausend;


import java.util.Random;

public class Dice
{
    private int diceNumber;
    private transient Random random;

    public Dice()
    {
        random = new Random();
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

}
