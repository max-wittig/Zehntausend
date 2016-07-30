package com.spaghettic0der.zehntausend;

public class AI extends Player
{
    public AI(int playerNumber, Settings settings)
    {
        super(playerNumber, settings);
        this.playerName = Main.language.getAI() + " " + (playerNumber + 1);
    }


}
