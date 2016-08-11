package com.spaghettic0der.zehntausend.AI;

import com.spaghettic0der.zehntausend.GameLogic.Dice;
import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

import java.util.ArrayList;

public class NormalAI extends AI
{
    public NormalAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
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
            }

            //can't use foreach here, because of "JavaFX Application Thread" java.util.ConcurrentModificationException
            for (int i = 0; i < remainingDices.size(); i++)
            {
                Dice dice = remainingDices.get(i);
                if (remainingDices.contains(dice))
                {
                    if (dice.getDiceNumber() == 5 || dice.getDiceNumber() == 1)
                    {
                        game.moveToDrawnDices(dice);
                        updateAndWait();
                    }
                }
            }

            if (Scoring.minScoreReached(this, settings))
            {
                if (remainingDices.size() <= 0)
                {
                    rollDice();
                    updateAndWait();
                }
                else
                {
                    break;
                }
            }
            else
            {
                rollDice();
                updateAndWait();
            }
        }

    }
}
