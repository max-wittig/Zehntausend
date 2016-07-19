package com.spaghettic0der.zehntausend;

import java.util.*;

public class Game
{
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private Settings settings;
    private boolean isGameOver = false;

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

    /* returns boolean, based on rules if game is valid
    * is called when roll is called
    * */
    public boolean isValidState(State state)
    {
        if (!isGameOver)
        {
            //in case scoreInRound < 300 -> State.Next is just not gonna save this round for the player. No additional points
            //but we still need to check so we allow State.Next to go into the method
            if (getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size() > 0 || state == State.NEXT)
            {
                ArrayList<Dice> dicesSinceLastRoll = getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices();

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
        else
        {
            //game over
            return false;
        }
    }

    //saves score to player.addToScore
    public void saveScore()
    {
        //always clear --> if not fullfiled score is gone!
        int numberOfDicesInLastRoll = getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size();
        if (numberOfDicesInLastRoll > 0 && minScoreReached())
        {
            getCurrentPlayer().addToScore(Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getCurrentTurn().getRoundArrayList(), true, settings));
        }
    }

    //cycles through players, but doesn't save the score --> has to be done with saveScore();
    public void nextPlayer()
    {
        if (!isGameOver)
        {
            if (getCurrentPlayer().hasWon())
            {
                if (getCurrentPlayer().getWinRank() == -1)
                {
                    //only show a dialog box to the first winner
                    if (getNumberOfWinners() < 1)
                        Main.showWinAlert(getCurrentPlayer().getPlayerName());

                    if (settings.isGameOverAfterFirstPlayerWon())
                    {
                        isGameOver = true;
                    }
                    getCurrentPlayer().setWinRank(getNumberOfWinners() + 1);
                }
            }

            //re-roll dices for next turn
            getCurrentPlayer().initDice();

            //set next turn
            getCurrentPlayer().nextTurn();
            setNextPlayerNumber();

        }
        else
        {
            if (!settings.isGameOverAfterFirstPlayerWon())
                Main.showGameOverDialog(getWinString());
        }
    }

    //sets next player number automatically and returns, when it found next player
    //with winRank = -1 which means the player is still in the game and hasn't won
    private void setNextPlayerNumber()
    {
        //search from currentPlayerNumber to end of array
        for (int i = currentPlayerNumber + 1; i < players.size(); i++)
        {
            if (players.get(i).getWinRank() == -1)
            {
                currentPlayerNumber = players.get(i).getPlayerNumber();
                return;
            }
        }

        //if nothing found search from beginning to end again
        for (Player currentPlayer : players)
        {
            if (currentPlayer.getWinRank() == -1)
            {
                currentPlayerNumber = currentPlayer.getPlayerNumber();
                return;
            }
        }

        //if still nothing set game to over!
        isGameOver = true;
        Main.showGameOverDialog(getWinString());

    }

    //gets a string which contains all winners in order
    // 1 : Player 3
    // 2 : Player 1
    // 3 : Player 2
    private String getWinString()
    {
        HashMap<Integer, Player> winnersHashMap = getWinners();
        StringBuilder winStringBuilder = new StringBuilder();
        for (Integer key : winnersHashMap.keySet())
        {
            if (winnersHashMap.get(key) != null)
            {
                winStringBuilder.append(key + ". place : " + winnersHashMap.get(key).getPlayerName() + "\n");
            }
        }
        return winStringBuilder.toString();
    }

    private int getNumberOfWinners()
    {
        int numberOfWinners = 0;
        for (Player player : players)
        {
            if (player.getWinRank() != -1)
            {
                numberOfWinners++;
            }
        }
        return numberOfWinners;
    }

    //returns rank of player -> player object in Hashmap
    private HashMap<Integer, Player> getWinners()
    {
        HashMap<Integer, Player> winPlayersSorted = new HashMap<>();
        for (Player currentPlayer : players)
        {
            winPlayersSorted.put(currentPlayer.getWinRank(), currentPlayer);
        }
        return winPlayersSorted;
    }

    //needed for preparing the listView --> fill with empty cells on load
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
        for (Player player : players)
        {
            player.setSettings(settings);
        }
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
