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

    //don't use doesn't work
    /*
    public static boolean containsDiceNumbers(ArrayList<Integer> numbers, ArrayList<Dice> dices)
    {
        for (int number : numbers)
        {
            for (Dice currentDice : dices)
            {
                if (currentDice.getDiceNumber() == number)
                {
                    return true;
                }
            }
        }
        return false;
    }
*/
    public static boolean isStreet(ArrayList<Dice> dices, boolean streetEnabled)
    {
        if (streetEnabled)
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


    public static int getScoreFromAllDicesInRound(ArrayList<Round> roundArrayList, Settings settings)
    {
        int sum = 0;
        for (Round currentRound : roundArrayList)
        {
            for (Roll currentRoll : currentRound.getRollArrayList())
            {
                sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices(), settings);
            }
        }
        return sum;
    }

    public static int getScoreFromAllDices(ArrayList<Turn> turnArrayList, Settings settings)
    {
        int sum = 0;
        for (Turn currentTurn : turnArrayList)
        {
            for (Round currentRound : currentTurn.getRoundArrayList())
            {
                if (currentRound.isValid())
                {
                    for (Roll currentRoll : currentRound.getRollArrayList())
                    {
                        sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices(), settings);
                    }
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
            //diceHashMap.putIfAbsent(diceNumber, 0);
            if (diceHashMap.get(diceNumber) == null) //it's so complicated, because android javafx
                diceHashMap.put(diceNumber, 0);      // doesn't support putIfAbsend
            diceHashMap.put(diceNumber, diceHashMap.get(diceNumber) + 1);
        }
        return diceHashMap;
    }

    //dices are all thrown in one roll
    private static int getScoreFromDicesInRoll(ArrayList<Dice> dices, Settings settings)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        int score = 0;

        if (isStreet(dices, settings.isStreetEnabled()))
        {
            return settings.getScoreStreet();
        }

        if (isThreeTimesTwo(dices, settings.isThreeXTwoEnabled()))
        {
            return settings.getScoreThreeXTwo();
        }

        if (isSixDicesInARow(dices, settings.isSixDicesInARowEnabled(), settings.getTotalDiceNumber()))
        {
            return settings.getScoreSixDicesInARow();
        }

        //normal cases
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

        return score;
    }

    public static boolean isThreeTimesTwo(ArrayList<Dice> dices, boolean threeXTwoEnabled)
    {
        if (threeXTwoEnabled)
        {
            HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
            int numberOfDicesWithTwoOccurrences = 0;
            for (Integer key : diceHashMap.keySet())
            {
                if (diceHashMap.get(key) == 2) // FIXME: 15.07.16 
                {
                    numberOfDicesWithTwoOccurrences++;
                }
            }

            if (numberOfDicesWithTwoOccurrences >= 3)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean isSixDicesInARow(ArrayList<Dice> dices, boolean sixDicesInARowEnabled, int totalDiceNumber)
    {
        if (sixDicesInARowEnabled)
        {
            HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
            for (Integer key : diceHashMap.keySet())
            {
                if (diceHashMap.get(key) >= totalDiceNumber)
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

}
