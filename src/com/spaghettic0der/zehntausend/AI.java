package com.spaghettic0der.zehntausend;


import java.util.ArrayList;

public abstract class AI extends Player
{
    protected Game game;

    public AI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings);
        this.playerName = Main.language.getAI() + " " + (playerNumber + 1);
        this.game = game;
    }

    public void draw()
    {
        drawPossibleDices();
    }

    abstract void drawPossibleDices();



}
