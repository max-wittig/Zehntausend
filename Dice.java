package com.spaghettic0der;


import java.util.Random;

public class Dice
{
    private int diceNumber;
    private int diceID;
    private boolean canDiceBeDrawnThisRound = true;
    private Random random;

    public Dice(int diceID)
    {
        random = new Random();
        this.diceID = diceID;
    }

    public int getDiceNumber()
    {
        //return 1;
        return diceNumber;
    }

    public void setDiceNumber(int diceNumber)
    {
        this.diceNumber = diceNumber;
    }

    public int getDiceID()
    {
        return diceID;
    }

    public boolean canDiceBeDrawnThisRound()
    {
        return canDiceBeDrawnThisRound;
    }

    public void setDiceDrawnThisRound(boolean canDiceBeDrawnThisRound)
    {
        this.canDiceBeDrawnThisRound = canDiceBeDrawnThisRound;
    }

    public void roll()
    {
        diceNumber = random.nextInt(6)+1;
    }

}
