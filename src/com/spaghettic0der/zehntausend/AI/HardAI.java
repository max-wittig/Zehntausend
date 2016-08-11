package com.spaghettic0der.zehntausend.AI;


import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;

public class HardAI extends AI
{

    public HardAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
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
