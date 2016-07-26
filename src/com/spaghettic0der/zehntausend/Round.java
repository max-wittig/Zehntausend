package com.spaghettic0der.zehntausend;

import java.util.ArrayList;

/**
 * a round a multiple rolls. E.g. if the player cleans the board a new set of remaining dices
 * needs to be created and a new roll is thrown
 * all rolls of a turn are saved in the roundArrayList
 */
public class Round
{
    private ArrayList<Roll> rollArrayList;

    public Round()
    {
        rollArrayList = new ArrayList<>();
        nextRoll();
    }

    public ArrayList<Roll> getRollArrayList()
    {
        return rollArrayList;
    }

    /**
     * if there is a roll, where there are 0 dices it isn't valid
     *
     * @return
     */
    public boolean isValid()
    {
        for (Roll currentRoll : rollArrayList)
        {
            if (currentRoll.getDrawnDices().isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    public Roll getCurrentRoll()
    {
        return rollArrayList.get(rollArrayList.size() - 1);
    }

    /**
     * loops through all rolls and adds them to a diceArrayList
     * @return diceArrayList
     */
    public ArrayList<Dice> getDrawnDices()
    {
        ArrayList<Dice> dices = new ArrayList<>();

        for (Roll currentRoll : getRollArrayList())
        {
            for (Dice currentDice : currentRoll.getDrawnDices())
            {
                dices.add(currentDice);
            }
        }

        return dices;
    }

    public void nextRoll()
    {
        Roll roll = new Roll();
        rollArrayList.add(roll);
    }

}
