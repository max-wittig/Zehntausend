package com.spaghettic0der;


import java.util.ArrayList;
import java.util.HashMap;

public class Scoring
{

    public static boolean containsDiceNumber(int number, ArrayList<Dice> dices)
    {
        for (Dice currentDice : dices)
        {
            if (currentDice.getDiceNumber() == number)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isStreet(ArrayList<Dice> dices)
    {
        if (Settings.STREET_ENABLED)
        {
            if (containsDiceNumber(1, dices) && containsDiceNumber(2, dices) &&
                    containsDiceNumber(3, dices) && containsDiceNumber(4, dices) &&
                    containsDiceNumber(5, dices) && containsDiceNumber(6, dices))
            {
                return true;
            }

        }
        else
        {
            return false;
        }
        return false;
    }


    public static int getScoreFromAllDicesInRound(ArrayList<Round> roundArrayList)
    {
        int sum = 0;
        for (Round currentRound : roundArrayList)
        {
            for (Roll currentRoll : currentRound.getRollArrayList())
            {
                sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices());
            }
        }
        return sum;
    }

    public static int getScoreFromAllDices(ArrayList<Turn> turnArrayList)
    {
        int sum = 0;
        for (Turn currentTurn : turnArrayList)
        {
            for (Round currentRound : currentTurn.getRoundArrayList())
            {
                for (Roll currentRoll : currentRound.getRollArrayList())
                {
                    sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices());
                }
            }
        }
        return sum;
    }


    public static boolean containsMultiple(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        System.out.println(diceHashMap.keySet());
        System.out.println(diceHashMap.values());
        for (Integer key : diceHashMap.keySet())
        {
            if (key != 5 && key != 1)
            {
                if (diceHashMap.get(key) < 3)
                {
                    return false;
                }
            }
        }

        return true;
    }


    private static HashMap<Integer, Integer> getDiceHashMap(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = new HashMap<>();
        for (Dice currentDice : dices)
        {
            int diceNumber = currentDice.getDiceNumber();
            diceHashMap.putIfAbsent(diceNumber, 0);
            diceHashMap.put(diceNumber, diceHashMap.get(diceNumber) + 1);
        }
        return diceHashMap;
    }

    //dices are all thrown in one roll
    private static int getScoreFromDicesInRoll(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        int score = 0;

        if (isStreet(dices))
        {
            score += Settings.SCORE_STREET;
        }
        else
        {
            //key is number [1,2,3,4,5,6]
            //diceHashMap.get(key) is occurrence of number
            for (Integer key : diceHashMap.keySet())
            {
                int diceNumber = key;
                int occurrence = diceHashMap.get(key);
                if (diceHashMap.get(key) > 2)
                {
                    //3 times 1 == 1000, 4 times 1 == 2000 etc...
                    if (diceNumber == 1)
                    {
                        int sum = diceNumber * 1000;
                        for (int i = 1; i < occurrence - 2; i++)
                        {
                            sum *= 2;
                        }
                        score += sum;
                    }
                    else
                    {
                        int sum = diceNumber * 100;
                        for (int i = 1; i < occurrence - 2; i++)
                        {
                            sum *= 2;
                        }
                        score += sum;
                    }
                }
                else
                {
                    //no more occurrences then 2 --> normal system
                    if (diceNumber == 5)
                    {
                        score += 50 * occurrence;
                    }

                    if (diceNumber == 1)
                    {
                        score += 100 * occurrence;
                    }
                }
            }
        }
        return score;
    }

}
