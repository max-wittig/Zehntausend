package com.spaghettic0der.zehntausend;


import javafx.concurrent.Task;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class EasyAI extends AI
{
    public EasyAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
    }

    @Override
    public void draw()
    {
        super.draw();
    }

    @Override
    boolean drawIsPossible()
    {
        if (AIHelper.containsOneOrFive(remainingDices))
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
            for (int i = 0; i < remainingDices.size(); i++)
            {
                Dice currentDice = remainingDices.get(i);
                if (currentDice.getDiceNumber() == 1 || currentDice.getDiceNumber() == 5)
                {
                    //insert delay here
                    game.moveToDrawnDices(currentDice);
                    game.getMain().updateUI();
                }
            }

            if (!Scoring.minScoreReached(this, settings))
                game.getCurrentPlayer().rollDice();
            game.getMain().updateUI();
        }
    }
}
