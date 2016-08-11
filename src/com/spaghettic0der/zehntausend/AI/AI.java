package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.GameLogic.*;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AI extends Player
{
    protected transient Game game;
    protected int playerNumber;

    public AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerNumber = playerNumber;
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

    public String getPlayerName()
    {
        return Main.language.getAI() + " " + (playerNumber + 1);
    }

    protected abstract AIType getAiType();

    protected void updateAndWait()
    {

        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                game.getMain().updateUI();
            }
        });

        try
        {
            Thread.sleep(settings.getAiDelay());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void draw()
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                drawPossibleDices();
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        nextPlayer();
                    }
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    abstract boolean drawIsPossible();

    abstract void drawPossibleDices();

    private void nextPlayer()
    {
        game.getMain().updateScoreOfPlayersInListView();
        game.nextPlayer();
        game.getMain().updateUI();
    }


}
