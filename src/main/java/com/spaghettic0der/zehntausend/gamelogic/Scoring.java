package com.spaghettic0der.zehntausend.gamelogic;


import com.spaghettic0der.zehntausend.extras.Settings;

import java.util.ArrayList;
import java.util.Comparator;
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

    public static int getLowestDiceNumber(ArrayList<Dice> dices)
    {
        int lowestNumber = 10000;
        for (Dice dice : dices)
        {
            if (dice.getDiceNumber() < lowestNumber)
            {
                lowestNumber = dice.getDiceNumber();
            }
        }
        return lowestNumber;
    }

    public static boolean isStreet(ArrayList<Dice> dices, boolean streetEnabled, int totalNumberDice)
    {
        if (streetEnabled)
        {
            int oneCountInDices = 0;
            HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
            if (diceHashMap != null)
            {
                if (!diceHashMap.isEmpty())
                {
                    for (int diceNumber = 1; diceNumber <= totalNumberDice; diceNumber++)
                    {
                        if (diceHashMap.get(diceNumber) != null)
                        {
                            if (diceHashMap.get(diceNumber) != 1)
                            {
                                return false;
                            }
                            else
                            {
                                oneCountInDices++;
                            }
                        }
                    }
                    if (oneCountInDices == totalNumberDice)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getScoreFullHouse(ArrayList<Dice> dicesFromRoll)
    {
        int score = 0;
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dicesFromRoll);
        for (Integer key : diceHashMap.keySet())
        {
            int diceNumber = key;
            int occurrenceOfNumber = diceHashMap.get(key);

            if (occurrenceOfNumber == 2)
            {
                occurrenceOfNumber++;
            }

            if (occurrenceOfNumber == 3)
            {
                int sum = diceNumber * 100;
                for (int i = 1; i < occurrenceOfNumber - 2; i++)
                {
                    sum *= 2;
                }
                score += sum;
            }
        }
        return score;
    }

    public static boolean isFullHouse(ArrayList<Dice> dicesFromRoll, boolean fullHouseEnabled, int totalNumberOfDice)
    {
        if (fullHouseEnabled && totalNumberOfDice == 5)
        {
            HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dicesFromRoll);
            int numberOfDicesWithThreeOccurrences = 0;
            int numberOfDicesWithTwoOccurrences = 0;
            for (Integer key : diceHashMap.keySet())
            {
                int diceNumber = key;
                int occurrenceOfNumber = diceHashMap.get(key);

                if (occurrenceOfNumber == 2)
                {
                    numberOfDicesWithTwoOccurrences++;
                }
                else if (occurrenceOfNumber == 3)
                {
                    numberOfDicesWithThreeOccurrences++;
                }
            }

            if (numberOfDicesWithThreeOccurrences == 1 && numberOfDicesWithTwoOccurrences == 1)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if the minScoreIsReached
     *
     * @return
     */
    public static boolean minScoreReached(Player currentPlayer, Settings settings)
    {
        if (Scoring.getScoreFromAllDicesInRound(currentPlayer.getCurrentTurn().getRoundArrayList(), true, settings) >= settings.getMinScoreRequiredToSaveInRound())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public static int getScoreFromAllDicesInRound(ArrayList<Round> roundArrayList, boolean validCheck, Settings settings)
    {
        int sum = 0;
        for (Round currentRound : roundArrayList)
        {
            if (currentRound.isValid() || !validCheck)
            {
                for (Roll currentRoll : currentRound.getRollArrayList())
                {
                    sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices(), settings);
                }

            }

        }
        return sum;
    }

    public static int getScoreFromAllDices(ArrayList<Turn> turnArrayList, Settings settings, boolean turnValidCheck, boolean roundValidCheck, Round activeRound)
    {
        int sum = 0;
        for (Turn currentTurn : turnArrayList)
        {
            if (currentTurn.isValid(settings))
            {
                for (Round currentRound : currentTurn.getRoundArrayList())
                {
                    //the current active round should not be validated, because it could be in later
                    if (currentRound.isValid() || currentRound == activeRound)
                    {
                        for (Roll currentRoll : currentRound.getRollArrayList())
                        {
                            sum += getScoreFromDicesInRoll(currentRoll.getDrawnDices(), settings);
                        }
                    }
                }
            }
        }
        return sum;
    }


    public static boolean containsMultiple(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        for (Integer key : diceHashMap.keySet())
        {
            if (diceHashMap.get(key) >= 3)
            {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<Dice> getSortedDices(ArrayList<Dice> dices)
    {
        dices.sort(new Comparator<Dice>()
        {
            @Override
            public int compare(Dice o1, Dice o2)
            {
                if (o1.getDiceNumber() > o2.getDiceNumber())
                {
                    return 1;
                }
                else if (o1.getDiceNumber() < o2.getDiceNumber())
                {
                    return -1;
                }

                return 0;
            }
        });

        return dices;
    }


    public static int getScoreFromMultipleDices(int diceNumber, int occurrence)
    {
        int score = 0;
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
        return score;
    }

    public static boolean isPyramid(ArrayList<Dice> dices, boolean pyramidEnabled)
    {
        if (pyramidEnabled)
        {
            HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
            for (int i = 1; i <= diceHashMap.keySet().size() + 1; i++)
            {
                if (diceHashMap.get(i) != null && diceHashMap.get(i + 1) != null && diceHashMap.get(i + 2) != null)
                {
                    if (diceHashMap.get(i) == 1 && diceHashMap.get(i + 1) == 2 && diceHashMap.get(i + 2) == 3)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static HashMap<Integer, Integer> getDiceHashMap(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = new HashMap<>();
        for (Dice currentDice : dices)
        {
            int diceNumber = currentDice.getDiceNumber();
            //diceHashMap.putIfAbsent(diceNumber, 0);
            if (diceHashMap.get(diceNumber) == null) //because android javafx
                diceHashMap.put(diceNumber, 0);      // doesn't support putIfAbsend
            diceHashMap.put(diceNumber, diceHashMap.get(diceNumber) + 1);
        }
        return diceHashMap;
    }

    //dices are all thrown in one roll
    public static int getScoreFromDicesInRoll(ArrayList<Dice> dices, Settings settings)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        int score = 0;

        if (isStreet(dices, settings.isStreetEnabled(), settings.getTotalDiceNumber()))
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

        if (isFullHouse(dices, settings.isFullHouseEnabled(), settings.getTotalDiceNumber()))
        {
            return getScoreFullHouse(dices);
        }

        if (isPyramid(dices, settings.isPyramidEnabled()))
        {
            return settings.getScorePyramid();
        }

        //normal cases
        //key is number [1,2,3,4,5,6]
        //diceHashMap.get(key) is occurrence of number
        for (Integer key : diceHashMap.keySet())
        {
            int diceNumber = key;
            int occurrence = diceHashMap.get(key);
            if (occurrence > 2)
            {
                score += getScoreFromMultipleDices(diceNumber, occurrence);
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
