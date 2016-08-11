package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

public class HardAI extends AI
{

    public HardAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
    }

    @Override
    public String getPlayerName()
    {
        return Main.language.getAI() + " " + Main.language.getHard() + " " + (playerNumber + 1);
    }

    @Override
    protected AIType getAiType()
    {
        return AIType.HARD;
    }

    @Override
    boolean drawIsPossible()
    {
        return (Scoring.containsMultiple(remainingDices) || containsOneOrFive(remainingDices));
    }

    @Override
    void drawPossibleDices()
    {

    }
}
