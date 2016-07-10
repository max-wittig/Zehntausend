package com.spaghettic0der;


import java.util.*;

class Game
{
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private int roundNumber = 0;
    private int numberOfDicesDrawnSinceLastRoll = 0;

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
    public void resetNumberOfDicesDrawnSinceLastRoll()
    {
        numberOfDicesDrawnSinceLastRoll = 0;
    }

    public boolean minScoreReached()
    {
        if (Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getRoundArrayList()) >= Settings.MIN_SCORE_REQUIRED_TO_SAVE_IN_ROUND)
        {
            System.out.println("Min Score reached!");
            return true;
        }
        else
        {
            return false;
        }
    }


    /* returns boolean, based on rules if game is valid
    * is called when roll is called
    * */
    public boolean isValidState(State state)
    {
        //in case scoreInRound < 300 -> State.Next is just not gonna save this round for the player. No additional points
        //but we still need to check so we allow State.Next to go into the method
        if (numberOfDicesDrawnSinceLastRoll > 0 || state == State.NEXT)
        {
            ArrayList<Dice> dicesSinceLastRoll = new ArrayList<>();
            for (Dice currentDice : getCurrentPlayer().getLastDrawnDices())
            {
                if(currentDice.canDiceBeDrawnThisRound())
                {
                    dicesSinceLastRoll.add(currentDice);
                }
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
                boolean valid = (Scoring.containsMultiple(dicesSinceLastRoll) || Scoring.isStreet(dicesSinceLastRoll));
                return valid;
            }
        }
        else
        {
            return false;
        }
    }

    public void increaseNumberDrawnSinceLastRoll()
    {
        numberOfDicesDrawnSinceLastRoll++;
    }

    public void decreaseNumberDrawnSinceLastRoll()
    {
        numberOfDicesDrawnSinceLastRoll--;
    }

    //cycles through players
    public void nextPlayer()
    {
        if (minScoreReached() && numberOfDicesDrawnSinceLastRoll > 0)
            getCurrentPlayer().addToScore(Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getRoundArrayList()));
        getCurrentPlayer().clearRoundArrayList();
        getCurrentPlayer().clearDices();
        if (currentPlayerNumber < Settings.TOTAL_PLAYERS - 1)
        {
            currentPlayerNumber++;
        }
        else
        {
            currentPlayerNumber = 0;
            nextRound();
        }
    }

    private void nextRound()
    {
        roundNumber++;
    }

    public Player getCurrentPlayer()
    {
        return players.get(currentPlayerNumber);
    }

}
