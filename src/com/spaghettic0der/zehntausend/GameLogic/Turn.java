package com.spaghettic0der.zehntausend.GameLogic;


import java.util.ArrayList;

/**
 * a turn contains all rounds
 * turns are kept in the player object
 * next button is pressed by player --> new turn begins before player is changed
 */
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
    @Deprecated
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

    /**
     * checks if turn is valid
     * score >= minScore
     * if confirmation is enabled in settings
     * next round can't be empty || null || not many points worth
     *
     * @param settings
     * @return
     */
    public boolean isValid(Settings settings)
    {
        if (Scoring.getScoreFromAllDicesInRound(getRoundArrayList(), true, settings) < settings.getMinScoreRequiredToSaveInRound())
        {
            return false;
        }

        for (int i = 0; i < roundArrayList.size(); i++)
        {
            Round currentRound = roundArrayList.get(i);
            Round nextRound = null;
            if (i + 1 < roundArrayList.size())
            {
                nextRound = roundArrayList.get(i + 1);
            }

            if (currentRound.isValid())
            {
                for (Roll roll : currentRound.getRollArrayList())
                {
                    if (settings.isClearAllNeedsConfirmationInNextRound())
                    {
                        if (roll.needsConfirmation(settings.getTotalDiceNumber()))
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
            else
            {
                return false;
            }
        }
        return true;
    }

}
