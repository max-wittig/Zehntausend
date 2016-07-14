package com.spaghettic0der;


import java.util.ArrayList;

//saves all drawn dices
public class Roll
{

    private ArrayList<Dice> drawnDices;

    public Roll()
    {
        drawnDices = new ArrayList<>();
    }

    public ArrayList<Dice> getDrawnDices()
    {
        return drawnDices;
    }

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
}
