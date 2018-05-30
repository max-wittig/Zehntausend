package com.maxwittig.zehntausend.ai;


import com.maxwittig.zehntausend.Main;
import com.maxwittig.zehntausend.extras.Settings;
import com.maxwittig.zehntausend.gamelogic.Game;
import com.maxwittig.zehntausend.gamelogic.Scoring;
import com.maxwittig.zehntausend.Main;
import com.maxwittig.zehntausend.extras.Settings;
import com.maxwittig.zehntausend.gamelogic.Game;
import com.maxwittig.zehntausend.gamelogic.Scoring;

public class HardAI extends AI {


    public HardAI(int playerNumber, Settings settings, Game game) {
        super(playerNumber, settings, game);
    }

    @Override
    public String getPlayerName() {
        return Main.language.getAI() + " " + Main.language.getHard() + " " + (playerNumber + 1);
    }

    @Override
    protected AIType getAiType() {
        return AIType.HARD;
    }

    @Override
    boolean drawIsPossible() {
        return
                (Scoring.containsMultiple(remainingDices)
                        || containsOneOrFive(remainingDices)
                        || Scoring.isStreet(remainingDices, settings.isStreetEnabled(), settings.getTotalDiceNumber())

                );
    }

    @Override
    protected void drawDices() {
        drawStreet();
        drawMultiple(rollAfterYouDrawnMultiple, diceNumberWhereItMakesSenseToRiskRerolling);
        draw5And1(drawOnlyOne);
    }


}
