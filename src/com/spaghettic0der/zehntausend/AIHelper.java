package com.spaghettic0der.zehntausend;


import java.util.ArrayList;
import java.util.HashMap;

public class AIHelper
{

    public static boolean containsOneOrFive(ArrayList<Dice> dices)
    {
        if (Scoring.containsDiceNumber(1, dices) || Scoring.containsDiceNumber(5, dices))
        {
            return true;
        }
        return false;
    }

    private static Dice getDiceWithNumber(int number, ArrayList<Dice> dices, ArrayList<Dice> toAdd)
    {
        for (Dice dice : dices)
        {
            if (dice.getDiceNumber() == number && !toAdd.contains(dice))
            {
                return dice;
            }
        }
        return null;
    }

    /**
     * returns multiple dices in an arrayList for the AI bots. With occ > 2
     */
    public static ArrayList<Dice> getMultipleDices(ArrayList<Dice> dices)
    {
        ArrayList<Dice> multipleDices = new ArrayList<>();
        HashMap<Integer, Integer> diceHashMap = Scoring.getDiceHashMap(dices);
        for (Integer key : diceHashMap.keySet())
        {
            if (diceHashMap.get(key) > 2)
            {
                for (int i = 0; i < diceHashMap.get(key); i++)
                {
                    multipleDices.add(getDiceWithNumber(key, dices, multipleDices));
                }
            }
        }
        return multipleDices;
    }
}
