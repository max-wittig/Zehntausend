package com.spaghettic0der;


import java.util.Random;

public class Dice
{
    private int diceNumber;
    private int diceID;
    private boolean isDiceDrawn = false;
    private boolean canDiceBeDrawnThisRound = true;
    private Random random;

    public Dice(int diceID)
    {
        random = new Random();
        this.diceID = diceID;
    }

    public int getDiceNumber()
    {
        return diceNumber;
    }

    public int getDiceID()
    {
        return diceID;
    }

    public boolean isCanDiceBeDrawnThisRound()
    {
        return canDiceBeDrawnThisRound;
    }

    public void roll()
    {
        diceNumber = random.nextInt(6);
    }
}
