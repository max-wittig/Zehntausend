package com.spaghettic0der.zehntausend;


public abstract class AI extends Player
{
    protected transient Game game;

    public AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerName = Main.language.getAI() + " " + (playerNumber + 1);
        this.game = game;
    }

    public void draw()
    {
        drawPossibleDices();
        // nextPlayer();
    }

    abstract boolean drawIsPossible();

    abstract void drawPossibleDices();

    protected void nextPlayer()
    {
        game.getMain().updateScoreOfPlayersInListView();
        game.nextPlayer();
    }


}
