package com.spaghettic0der.zehntausend;


import java.util.ArrayList;

/**
 * saves the drawn dices in an arrayList
 * roll is all drawn dices on the screen at once
 */
public class Roll
{

    private ArrayList<Dice> drawnDices;
    private boolean isConfirmingRoll = false;

    public Roll()
    {
        drawnDices = new ArrayList<>();
    }

    /**
     * if all dices were rolled in the roll, it needs confirmation
     *
     * @param diceNumber total number of dices in the game
     * @return
     */
    public boolean needsConfirmation(int diceNumber)
    {
        if (getDrawnDices().size() >= diceNumber)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public ArrayList<Dice> getDrawnDices()
    {
        return drawnDices;
    }

    /**
     * now the object is removed instead of removing the number
     * @param number
     */
    @Deprecated
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

    /**
     * removes dice from roll
     * @param dice
     */
    public void removeDice(Dice dice)
    {
        drawnDices.remove(dice);
    }
}
