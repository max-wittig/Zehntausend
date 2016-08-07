package com.spaghettic0der.zehntausend;


import java.util.ArrayList;
import java.util.HashMap;

public abstract class AI extends Player
{
    protected transient Game game;

    AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerName = Main.language.getAI() + " " + (playerNumber + 1);
        this.game = game;
    }

    /**
     * checks if one or five is in this arrayList
     *
     * @param dices arrayList
     * @return (1 or 5) ? true : false
     */
    static boolean containsOneOrFive(ArrayList<Dice> dices)
    {
        if (Scoring.containsDiceNumber(1, dices) || Scoring.containsDiceNumber(5, dices))
        {
            return true;
        }
        return false;
    }

    /**
     * helper method for get multiple dices
     *
     * @param number diceNumber
     * @param dices  arrayList with dices
     * @param toAdd  arrayList with existing dices, where dice is gonna be added. Needed to check if dice is already in
     * @return Dice, which is not already in toAdd
     */
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
    static ArrayList<Dice> getMultipleDices(ArrayList<Dice> dices)
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

    public void draw()
    {
        drawPossibleDices();
        nextPlayer();
    }

    abstract boolean drawIsPossible();

    abstract void drawPossibleDices();

    private void nextPlayer()
    {
        game.getMain().updateScoreOfPlayersInListView();
        game.nextPlayer();
    }


}
