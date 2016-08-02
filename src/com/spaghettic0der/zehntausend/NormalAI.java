package com.spaghettic0der.zehntausend;

import java.util.ArrayList;

public class NormalAI extends AI
{
    public NormalAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
    }

    @Override
    boolean drawIsPossible()
    {
        if (Scoring.containsMultiple(remainingDices) || AIHelper.containsOneOrFive(remainingDices))
        {
            return true;
        }
        return false;
    }

    @Override
    void drawPossibleDices()
    {
        while (!Scoring.minScoreReached(this, settings) && drawIsPossible())
        {
            if (Scoring.containsMultiple(remainingDices))
            {
                ArrayList<Dice> multipleDicesArrayList = AIHelper.getMultipleDices(remainingDices);
                for (int i = 0; i < multipleDicesArrayList.size(); i++)
                {
                    Dice dice = multipleDicesArrayList.get(i);
                    if (remainingDices.contains(dice))
                        game.moveToDrawnDices(dice);
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
                    }
                }
            }

            if (drawIsPossible() && remainingDices.size() > 0)
            {
                rollDice();
            }

            game.getMain().updateUI();

        }
    }
}
