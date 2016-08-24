package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.GameLogic.*;
import com.spaghettic0der.zehntausend.Helper.Debug;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class AI extends Player
{
    protected transient Game game;
    protected transient Random random;


    //draws only one dice and rerolls e.g. 1 5 2 3 6 --> draws only 1 and rerolls 5 other dices
    protected float stopDrawing5And1 = 0.7f;  //noRisk --> 0

    //AI rolls dices again, after it found multiple dices in the same roll
    //protected float rollAfterYouDrawnMultiple = 0f;

    //if all dices have been drawn. Take the risk to re-roll all 6?
    protected float reRollAfterYouDrawnAllDices = 1f;

    protected int diceNumberWhereToRiskRolling = 3;
    //AI rolls dices again, even though score >= minScore. Dependent on diceNumberWhereToRiskRolling
    protected float stopRollingIfWinScoreReached = 0.5f;

    protected Thread thread;
    //sees the street 100% of the time
    protected float drawStreet = 1f;

    public AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerNumber = playerNumber;
        this.game = game;
        random = new Random();
    }

    protected void noRisk()
    {
        stopDrawing5And1 = 0;
        stopRollingIfWinScoreReached = 1f;
        //rollAfterYouDrawnMultiple = 0;
        reRollAfterYouDrawnAllDices = 0;
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
        thread = new Thread(new Runnable()
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

    protected void draw5And1()
    {
        //can't use foreach here, because of "JavaFX Application Thread" java.util.ConcurrentModificationException
        for (int i = 0; i < remainingDices.size(); i++)
        {
            Dice dice = remainingDices.get(i);
            if (dice != null && remainingDices.contains(dice))
            {
                if (dice.getDiceNumber() == 1 || dice.getDiceNumber() == 5)
                {
                    game.moveToDrawnDices(dice);
                    updateAndWait();
                    if ((random.nextFloat() < stopDrawing5And1)
                            && remainingDices.size() >= diceNumberWhereToRiskRolling
                            && getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size() > 1)
                    {
                        rollDice();
                        updateAndWait();
                    }

                }
            }
        }
    }

    protected boolean cancelLoop()
    {
        if (Scoring.minScoreReached(this, settings))
        {
            if (remainingDices.size() <= 0 && random.nextFloat() < reRollAfterYouDrawnAllDices)
            {
                //whats it doing when all dices are gone from remaining
                rollDice();
                updateAndWait();
            }
            else
            {
                if ((remainingDices.size() >= diceNumberWhereToRiskRolling) && random.nextFloat() < stopDrawing5And1)
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
            if (canRollDice())
            {
                rollDice();
                updateAndWait();
            }
        }

        return false;
    }

    protected abstract void drawDices();


    protected void drawPossibleDices()
    {
        while (drawIsPossible())
        {
            if (hasWon() && random.nextFloat() < stopRollingIfWinScoreReached)
                return;

            drawDices();
            if (cancelLoop())
            {
                return;
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

    public void stopThread()
    {
        if (thread != null && thread.isAlive())
            thread.stop();
    }

    protected void drawMultiple()
    {
        if (Scoring.containsMultiple(remainingDices))
        {
            ArrayList<Dice> multipleDicesArrayList = getMultipleDices(remainingDices);
            drawDicesAndWait(multipleDicesArrayList);

        }
    }


    private void drawDicesAndWait(ArrayList<Dice> dices)
    {
        for (int i = 0; i < dices.size(); i++)
        {
            Dice dice = dices.get(i);
            if (dice != null && remainingDices.contains(dice))
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