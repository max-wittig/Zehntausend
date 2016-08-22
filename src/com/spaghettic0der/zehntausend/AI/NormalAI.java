package com.spaghettic0der.zehntausend.AI;

import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.GameLogic.Scoring;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

public class NormalAI extends AI
{

    public NormalAI(int playerNumber, Settings settings, Game game)
    {
        super(playerNumber, settings, game);
        //noRisk();
    }

    @Override
    protected AIType getAiType()
    {
        return AIType.NORMAL;
    }

    @Override
    public String getPlayerName()
    {
        return Main.language.getAI() + " " + Main.language.getNormal() + " " + (playerNumber + 1);
    }

    @Override
    boolean drawIsPossible()
    {
        return (Scoring.containsMultiple(remainingDices) || containsOneOrFive(remainingDices));
    }


    @Override
    protected void drawDices()
    {
        drawStreet();
        drawMultiple(rollAfterYouDrawnMultiple, diceNumberWhereItMakesSenseToRiskRerolling);
        draw5And1(drawOnlyOne);

    }


}
