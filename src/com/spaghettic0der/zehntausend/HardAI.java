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
        return (Scoring.containsMultiple(remainingDices) || containsOneOrFive(remainingDices));
    }

    @Override
    void drawPossibleDices()
    {

    }
}
