package com.spaghettic0der;


import javafx.scene.control.Alert;

import java.util.*;

class Game
{
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;

    public Game()
    {
        players = new ArrayList<>();
        initPlayers();
    }

    private void initPlayers()
    {
        for (int i = 0; i < Settings.TOTAL_PLAYERS; i++)
        {
            Player player = new Player(i);
            players.add(player);
        }
    }

    //resets dices currently in roll to zero, so that the player cannot continue
    //if he doesn't draw any dices
    public boolean minScoreReached()
    {
        if (Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getLastTurn().getRoundArrayList()) >= Settings.MIN_SCORE_REQUIRED_TO_SAVE_IN_ROUND)
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
        if (Scoring.getScoreFromAllDices(getCurrentPlayer().getTurnArrayList()) >= Settings.MIN_SCORE_REQUIRED_TO_WIN && isValidState(State.WIN))
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
        if (getCurrentPlayer().getLastTurn().getLastRound().getLastRoll().getDrawnDices().size() > 0 || state == State.NEXT)
        {
            ArrayList<Dice> dicesSinceLastRoll = new ArrayList<>();
            for (Dice currentDice : getCurrentPlayer().getLastTurn().getLastRound().getLastRoll().getDrawnDices())
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
                        || Scoring.isStreet(dicesSinceLastRoll)
                        || Scoring.isSixDicesInARow(dicesSinceLastRoll)
                        || Scoring.isThreeTimesTwo(dicesSinceLastRoll));
                return valid;
            }
        }
        else
        {
            return false;
        }
    }

    public void showWinAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You won!");
        alert.setContentText("Congrats Player " + (getCurrentPlayer().getPlayerNumber() + 1));
        alert.show();
    }

    //cycles through players
    public void nextPlayer()
    {
        //always clear --> if not fullfiled score is gone!
        int numberOfDicesInLastRoll = getCurrentPlayer().getLastTurn().getLastRound().getLastRoll().getDrawnDices().size();
        if (numberOfDicesInLastRoll > 0 && !winScoreReached() && minScoreReached())
        {
            getCurrentPlayer().addToScore(Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getLastTurn().getRoundArrayList()));
        }

        if (winScoreReached())
        {
            showWinAlert();
        }
        else
        {
            getCurrentPlayer().initDice();
            getCurrentPlayer().nextTurn();
            getCurrentPlayer().getLastTurn().nextRound();

            if (currentPlayerNumber < Settings.TOTAL_PLAYERS - 1)
            {
                currentPlayerNumber++;
            }
            else
            {
                currentPlayerNumber = 0;
            }
        }
    }

    public Player getCurrentPlayer()
    {
        return players.get(currentPlayerNumber);
    }

}
