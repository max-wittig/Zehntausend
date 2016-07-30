package com.spaghettic0der.zehntausend;


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
    void drawPossibleDices()
    {
        for (int i = 0; i < remainingDices.size(); i++)
        {
            Dice currentDice = remainingDices.get(i);
            if (currentDice.getDiceNumber() == 1 || currentDice.getDiceNumber() == 5)
            {
                game.moveToDrawnDices(currentDice);
            }
        }

        if (Scoring.minScoreReached(this, settings))
        {

        }
    }
}
