package com.spaghettic0der.zehntausend;


import java.util.ArrayList;

//saves all drawn dices
public class Roll
{

    private ArrayList<Dice> drawnDices;
    private boolean isConfirmingRoll = false;

    public Roll()
    {
        drawnDices = new ArrayList<>();
    }

    public boolean needsConfirmation(int diceNumber)
    {
        if (getDrawnDices().size() >= diceNumber)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public ArrayList<Dice> getDrawnDices()
    {
        return drawnDices;
    }

    @Deprecated
    public void removeDiceWithNumber(int number)
    {
        for (Dice toRemove : getDrawnDices())
        {
            if (toRemove.getDiceNumber() == number)
            {
                getDrawnDices().remove(toRemove);
                break;
            }
        }
    }

    public void removeDice(Dice dice)
    {
        drawnDices.remove(dice);
    }
}
