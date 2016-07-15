package com.spaghettic0der;


import java.util.ArrayList;

public class Turn
{
    private ArrayList<Round> roundArrayList;

    public Turn()
    {
        roundArrayList = new ArrayList<>();
        nextRound();
    }

    public ArrayList<Round> getRoundArrayList()
    {
        return roundArrayList;
    }

    //from last turn only
    public ArrayList<Dice> getAllDrawnDices()
    {
        ArrayList<Dice> dices = new ArrayList<>();

        for (Round currentRound : getRoundArrayList())
        {
            for (Roll currentRoll : currentRound.getRollArrayList())
            {
                for (Dice currentDice : currentRoll.getDrawnDices())
                {
                    dices.add(currentDice);
                }
            }
        }
        return dices;
    }

    public Round getCurrentRound()
    {
        return roundArrayList.get(roundArrayList.size() - 1);
    }

    //starts new round
    public void nextRound()
    {
        //no new turn, because turn is only renewed, when player presses next
        Round round = new Round();
        roundArrayList.add(round);
    }

}
