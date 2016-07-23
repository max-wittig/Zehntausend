package com.spaghettic0der.zehntausend;


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

    public boolean isValid(Settings settings)
    {
        for (int i = 0; i < roundArrayList.size(); i++)
        {
            Round currentRound = roundArrayList.get(i);
            Round nextRound = null;
            if (i + 1 < roundArrayList.size())
            {
                nextRound = roundArrayList.get(i + 1);
            }

            if (currentRound.isValid() || currentRound == getCurrentRound())
            {
                for (Roll roll : currentRound.getRollArrayList())
                {
                    if (roll.getDrawnDices().size() >= settings.getTotalDiceNumber())
                    {
                        if (settings.isClearAllNeedsConfirmationInNextRound())
                        {
                            if (nextRound == null)
                            {
                                return false;
                            }
                            else
                            {
                                if (Scoring.getScoreFromDicesInRoll(nextRound.getDrawnDices(), settings) < settings.getMinScoreToConfirm())
                                {
                                    return false;
                                }
                            }
                        }
                    }
                }

            }
        }
        return true;
    }

}