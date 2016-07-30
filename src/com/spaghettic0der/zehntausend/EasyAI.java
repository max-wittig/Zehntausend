package com.spaghettic0der.zehntausend;


import javafx.application.Platform;

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
                try
                {
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                game.getMain().updateUI();
                                Thread.sleep(100);
                                game.moveToDrawnDices(currentDice);
                                game.getMain().updateUI();

                            }
                            catch (Exception e)
                            {

                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }

            }
        }

        if (Scoring.minScoreReached(this, settings))
        {

        }
    }
}
