package com.spaghettic0der.zehntausend.AI;

import com.spaghettic0der.zehntausend.GameLogic.Dice;
import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

import java.util.ArrayList;

public class NormalAI extends AI
{
    //AI rolls dices again, even though score >= minScore
    private float diceRollRisk = 0.2f;

    private float drawOnlyOne = 0.5f;

    //AI rolls dices again, after it found multiple dices in the same roll
    private float rollAfterYouDrawnMultiple = 0.7f;

    //if all dices have been drawn. Take the risk to re-roll all 6?
    private float reRollAfterYouDrawnAllDices = 1f;

    private int diceNumberWhereItMakesSenseToRiskRerolling = 3;

    private boolean stopRollingIfWinScoreReached = true;


    public NormalAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
        //noRisk();
    }

    private void noRisk()
    {
        diceNumberWhereItMakesSenseToRiskRerolling = 0;
        drawOnlyOne = 0;
        rollAfterYouDrawnMultiple = 0;
        reRollAfterYouDrawnAllDices = 0;
    }

    @Override
    protected AIType getAiType()
    {
        return AIType.NORMAL;
    }

    @Override
    public String getPlayerName()
    {
        return Main.language.getAI() + " " + Main.language.getNormal() + " " + (playerNumber + 1);
    }

    @Override
    boolean drawIsPossible()
    {
        return (Scoring.containsMultiple(remainingDices) || containsOneOrFive(remainingDices));
    }

    @Override
    void drawPossibleDices()
    {
        while (drawIsPossible())
        {
            if (winScoreReached())
                return;

            drawMultiple();
            draw5And1();
            if (checkForCancelLoop())
            {
                break;
            }
        }

    }

    private boolean checkForCancelLoop()
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

    private boolean winScoreReached()
    {
        return (hasWon() && stopRollingIfWinScoreReached);
    }

    private void draw5And1()
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

    private void drawMultiple()
    {
        if (Scoring.containsMultiple(remainingDices))
        {
            ArrayList<Dice> multipleDicesArrayList = getMultipleDices(remainingDices);
            for (int i = 0; i < multipleDicesArrayList.size(); i++)
            {
                Dice dice = multipleDicesArrayList.get(i);
                if (remainingDices.contains(dice))
                {
                    game.moveToDrawnDices(dice);
                    updateAndWait();
                }
            }

            //reroll if found multiple dices
            if ((random.nextFloat() < rollAfterYouDrawnMultiple) && remainingDices.size() >= diceNumberWhereItMakesSenseToRiskRerolling)
            {
                rollDice();
                drawPossibleDices();
            }
        }
    }
}
