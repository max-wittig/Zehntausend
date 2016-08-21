package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.GameLogic.*;
import com.spaghettic0der.zehntausend.Helper.Debug;
import javafx.application.Platform;
import javafx.scene.control.ScrollBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class AI extends Player
{
    protected transient Game game;
    protected Random random;
    //AI rolls dices again, even though score >= minScore
    protected float diceRollRisk = 0.2f;

    protected float drawOnlyOne = 0.5f;

    //AI rolls dices again, after it found multiple dices in the same roll
    protected float rollAfterYouDrawnMultiple = 0.7f;

    //if all dices have been drawn. Take the risk to re-roll all 6?
    protected float reRollAfterYouDrawnAllDices = 1f;

    protected int diceNumberWhereItMakesSenseToRiskRerolling = 3;
    protected boolean stopRollingIfWinScoreReached = true;

    //sees the street 100% of the time
    protected float drawStreet = 1f;

    public AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerNumber = playerNumber;
        this.game = game;
        random = new Random();
    }

    /**
     * checks if one or five is in this arrayList
     *
     * @param dices arrayList
     * @return (1 or 5) ? true : false
     */
    protected boolean containsOneOrFive(ArrayList<Dice> dices)
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
    private Dice getDiceWithNumber(int number, ArrayList<Dice> dices, ArrayList<Dice> toAdd)
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
    protected ArrayList<Dice> getMultipleDices(ArrayList<Dice> dices)
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

    @Override
    public void rollDice()
    {
        if (canRollDice())
            super.rollDice();
    }

    protected boolean canRollDice()
    {
        if (getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size() > 0)
            return true;
        else
            return false;
    }

    @Override
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

    private void nextPlayer()
    {
        game.getMain().updateScoreOfPlayersInListView();
        game.nextPlayer();
        game.getMain().updateUI();
    }

    protected void draw5And1(float drawOnlyOne)
    {
        //can't use foreach here, because of "JavaFX Application Thread" java.util.ConcurrentModificationException
        for (int i = 0; i < remainingDices.size(); i++)
        {
            Dice dice = remainingDices.get(i);
            if (remainingDices.contains(dice))
            {
                if (dice.getDiceNumber() == 1 || dice.getDiceNumber() == 5)
                {
                    game.moveToDrawnDices(dice);
                    updateAndWait();
                    if ((random.nextFloat() < drawOnlyOne) && getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size() > 1)
                    {
                        rollDice();
                        updateAndWait();
                        return;
                    }
                }
            }
        }
    }

    protected boolean cancelLoop()
    {
        if (Scoring.minScoreReached(this, settings) && canRollDice())
        {
            if (remainingDices.size() <= 0 && random.nextFloat() < reRollAfterYouDrawnAllDices)
            {
                rollDice();
                updateAndWait();
            }
            else
            {
                if ((random.nextFloat() < diceRollRisk && remainingDices.size() >= diceNumberWhereItMakesSenseToRiskRerolling))
                {
                    rollDice();
                    updateAndWait();
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            rollDice();
            updateAndWait();
        }

        return false;
    }

    protected abstract void drawDices();


    protected void drawPossibleDices()
    {
        while (drawIsPossible())
        {
            if (winScoreReached(stopRollingIfWinScoreReached))
                return;

            drawDices();
            if (cancelLoop())
            {
                break;
            }
        }

    }

    private ArrayList<Dice> getStreetDices()
    {
        ArrayList<Dice> streetDices = new ArrayList<>();
        ArrayList<Dice> sortedDices = Scoring.getSortedDices(remainingDices);
        int startDiceNumber = Scoring.getLowestDiceNumber(remainingDices);
        for (int i = 0; i < sortedDices.size(); i++)
        {
            Dice dice = sortedDices.get(i);
            if (sortedDices.contains(dice) && sortedDices.get(i).getDiceNumber() == startDiceNumber)
            {
                streetDices.add(dice);
                startDiceNumber++;
            }
        }
        return streetDices;
    }

    protected void drawMultiple(float rollAfterYouDrawnMultiple, int diceNumberWhereItMakesSenseToRiskRerolling)
    {
        if (Scoring.containsMultiple(remainingDices))
        {
            ArrayList<Dice> multipleDicesArrayList = getMultipleDices(remainingDices);
            drawDicesAndWait(multipleDicesArrayList);

            //reroll if found multiple dices
            if ((random.nextFloat() < rollAfterYouDrawnMultiple) && remainingDices.size() >= diceNumberWhereItMakesSenseToRiskRerolling)
            {
                rollDice();
                drawPossibleDices();
            }
        }
    }

    protected boolean winScoreReached(boolean stopRollingIfWinScoreReached)
    {
        return (hasWon() && stopRollingIfWinScoreReached);
    }

    private void drawDicesAndWait(ArrayList<Dice> dices)
    {
        for (int i = 0; i < dices.size(); i++)
        {
            Dice dice = dices.get(i);
            if (remainingDices.contains(dice))
            {
                game.moveToDrawnDices(dice);
                updateAndWait();
            }
        }
    }

    protected void drawStreet()
    {
        //remaining dices contains street and the AI 'sees' the street (drawStreet)
        if (Scoring.isStreet(remainingDices, settings.isStreetEnabled(), settings.getTotalDiceNumber())
                && random.nextFloat() < drawStreet)
        {
            ArrayList<Dice> streetDices = getStreetDices();
            drawDicesAndWait(streetDices);
        }
    }


}
