package com.spaghettic0der.zehntausend;


public class HardAI extends AI
{

    public HardAI(int playerNumber, Settings settings, Game game)
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

    }
}
