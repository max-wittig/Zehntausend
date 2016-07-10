package com.spaghettic0der;


import java.util.ArrayList;

public class Turn
{
    private ArrayList<Round> roundArrayList;

    public Turn()
    {
        roundArrayList = new ArrayList<>();
    }

    public ArrayList<Round> getRoundArrayList()
    {
        return roundArrayList;
    }

    public Round getLastRound()
    {
        Round round;
        if (roundArrayList.size() > 0)
        {
            round = roundArrayList.get(roundArrayList.size() - 1);
        }
        else
        {
            round = new Round();
            roundArrayList.add(round);
        }
        return round;
    }

    public void addToTurn(Round round)
    {
        roundArrayList.add(round);
    }

    public ArrayList<Dice> getDrawnDicesFromLastRound()
    {
        ArrayList<Dice> dices = new ArrayList<>();

        for (Roll currentRoll : getLastRound().getRollArrayList())
        {
            for (Dice currentDice : currentRoll.getDrawnDices())
            {
                dices.add(currentDice);
            }
        }

        return dices;
    }
}
