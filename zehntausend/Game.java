package com.spaghettic0der.zehntausend;


import javafx.scene.control.Alert;

import java.util.*;

public class Game
{
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private Settings settings;

    public Game(Settings settings)
    {
        this.settings = settings;
        players = new ArrayList<>();
        initPlayers();
    }

    private void initPlayers()
    {
        for (int i = 0; i < settings.getTotalPlayers(); i++)
        {
            Player player = new Player(i, settings);
            players.add(player);
        }
    }
    //resets dices currently in roll to zero, so that the player cannot continue
    //if he doesn't draw any dices
    public boolean minScoreReached()
    {
        if (Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getCurrentTurn().getRoundArrayList(), true, settings) >= settings.getMinScoreRequiredToSaveInRound())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean winScoreReached()
    {
        if (getCurrentPlayer().getScore() + Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getCurrentTurn().getRoundArrayList(), true, settings) >= settings.getMinScoreRequiredToWin() && isValidState(State.WIN))
            return true;
        else
            return false;
    }


    /* returns boolean, based on rules if game is valid
    * is called when roll is called
    * */
    public boolean isValidState(State state)
    {
        //in case scoreInRound < 300 -> State.Next is just not gonna save this round for the player. No additional points
        //but we still need to check so we allow State.Next to go into the method
        if (getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size() > 0 || state == State.NEXT)
        {
            ArrayList<Dice> dicesSinceLastRoll = new ArrayList<>();
            for (Dice currentDice : getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices())
            {
                dicesSinceLastRoll.add(currentDice);
            }

            if (!Scoring.containsDiceNumber(2, dicesSinceLastRoll) && !Scoring.containsDiceNumber(3, dicesSinceLastRoll)
                    && !Scoring.containsDiceNumber(4, dicesSinceLastRoll) && !Scoring.containsDiceNumber(6, dicesSinceLastRoll))
            {
                //only 1 or 5
                return true;
            }
            else
            {
                //checks if there are any multiplications of dice (3 times 2 == 200, 3 times 3 == 300 etc...)
                //if so gameState is valid
                boolean valid = (Scoring.containsMultiple(dicesSinceLastRoll)
                        || Scoring.isStreet(dicesSinceLastRoll, settings.isStreetEnabled())
                        || Scoring.isSixDicesInARow(dicesSinceLastRoll, settings.isSixDicesInARowEnabled(), settings.getTotalDiceNumber())
                        || Scoring.isThreeTimesTwo(dicesSinceLastRoll, settings.isThreeXTwoEnabled()));
                return valid;
            }
        }
        else
        {
            return false;
        }
    }

    public Player getPreviousPlayer()
    {
        if (currentPlayerNumber > 0)
            return players.get(currentPlayerNumber - 1);
        else
        {
            return players.get(settings.getTotalPlayers() - 1);
        }
    }

    //cycles through players
    public void nextPlayer()
    {
        //always clear --> if not fullfiled score is gone!
        int numberOfDicesInLastRoll = getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size();
        if (numberOfDicesInLastRoll > 0 && !winScoreReached() && minScoreReached())
        {
            getCurrentPlayer().addToScore(Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getCurrentTurn().getRoundArrayList(), true, settings));
        }

        if (winScoreReached())
        {
            Main.showWinAlert(currentPlayerNumber + 1);
        }
        else
        {
            getCurrentPlayer().initDice();
            getCurrentPlayer().nextTurn();

            if (currentPlayerNumber < settings.getTotalPlayers() - 1)
            {
                currentPlayerNumber++;
            }
            else
            {
                currentPlayerNumber = 0;
            }
        }
    }

    public ArrayList<Turn> getLongestTurnArrayList()
    {
        int length = 0;
        ArrayList<Turn> turnArrayList = null;
        for (Player currentPlayer : players)
        {
            if (currentPlayer.getTurnArrayList().size() > length)
            {
                length = currentPlayer.getTurnArrayList().size();
                turnArrayList = currentPlayer.getTurnArrayList();
            }
        }
        return turnArrayList;
    }

    public Player getCurrentPlayer()
    {
        return players.get(currentPlayerNumber);
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    public ArrayList<Player> getPlayers()
    {
        return players;
    }

    public int getCurrentPlayerNumber()
    {
        return currentPlayerNumber;
    }
}
