package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.GameLogic.Dice;
import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

public class EasyAI extends AI
{
    public EasyAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
    }

    @Override
    public String getPlayerName()
    {
        return Main.language.getAI() + " " + Main.language.getEasy() + " " + (playerNumber + 1);
    }

    @Override
    protected AIType getAiType()
    {
        return AIType.EASY;
    }

    @Override
    public void draw()
    {
        super.draw();
    }

    @Override
    boolean drawIsPossible()
    {
        if (containsOneOrFive(remainingDices))
        {
            return true;
        }
        return false;
    }

    @Override
    protected void drawDices()
    {

    }
}
