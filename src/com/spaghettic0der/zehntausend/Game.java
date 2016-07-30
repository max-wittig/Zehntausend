package com.spaghettic0der.zehntausend;

import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

public class Game
{
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private Settings settings;
    private boolean isGameOver = false;
    private Main main;

    public Game(Settings settings, Main main)
    {
        this.settings = settings;
        this.main = main;
        players = new ArrayList<>();
        initPlayers();
    }

    public Main getMain()
    {
        return main;
    }

    /**
     * initialized all players. Adds objects to arrayList
     * called by constructor
     */
    private void initPlayers()
    {
        for (int i = 0; i < settings.getTotalPlayers() - settings.getTotalAI(); i++)
        {
            Player player = new Player(i, settings);
            player.setPlayerType(PlayerType.Human);
            players.add(player);
        }

        for (int i = settings.getTotalPlayers() - settings.getTotalAI(); i < settings.getTotalAI() + settings.getTotalPlayers() - settings.getTotalAI(); i++)
        {
            AI ai = new EasyAI(i, settings, this);
            ai.setPlayerType(PlayerType.COMPUTER);
            players.add(ai);
        }
    }


    /**
     * moves dice that the player clicked on from on arraylist to the other and
     * updates the UI afterwards --> everything redrawn
     * @param dice
     */
    public void moveToDrawnDices(Dice dice)
    {
        ArrayList<Dice> remainingDices = getCurrentPlayer().getRemainingDices();
        remainingDices.remove(dice);
        getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().add(dice);

        main.updateUI();
    }

    /**
     * moves dice that the player clicked on from on arraylist to the other and
     * updates the UI afterwards --> everything redrawn
     *
     * @param dice
     */
    public void moveToRemainingDices(Dice dice)
    {
        ArrayList<Dice> drawnDices = getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices();
        if (drawnDices.size() > 0)
        {
            if (drawnDices.contains(dice))
            {
                getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().removeDice(dice);
                getCurrentPlayer().getRemainingDices().add(dice);

            }
        }

        main.updateUI();
    }


    /** returns boolean, based on rules if game is valid
    * is called when roll is called
    * */
    public boolean isValidState(State state)
    {
        if (!isGameOver && !getCurrentPlayer().isAI())
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
                    boolean valid =
                            (Scoring.containsMultiple(dicesSinceLastRoll)
                            || Scoring.isStreet(dicesSinceLastRoll, settings.isStreetEnabled(), settings.getTotalDiceNumber())
                            || Scoring.isSixDicesInARow(dicesSinceLastRoll, settings.isSixDicesInARowEnabled(), settings.getTotalDiceNumber())
                            || Scoring.isThreeTimesTwo(dicesSinceLastRoll, settings.isThreeXTwoEnabled())
                            || Scoring.isFullHouse(dicesSinceLastRoll, settings.isFullHouseEnabled(), settings.getTotalDiceNumber())
                                    || Scoring.isPyramid(dicesSinceLastRoll, settings.isPyramidEnabled())

                    );


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

    /**
     * was used to save the score everytime the player completed it's turn. No longer needed
     */
    @Deprecated
    public void saveScore()
    {
        //always clear --> if not fullfiled score is gone!
        int numberOfDicesInLastRoll = getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().size();
        if (numberOfDicesInLastRoll > 0 && Scoring.minScoreReached(getCurrentPlayer(), settings))
        {
            //getCurrentPlayer().addToScore(Scoring.getScoreFromAllDicesInRound(getCurrentPlayer().getCurrentTurn().getRoundArrayList(), true, settings));
            //getCurrentPlayer().setScore(Scoring.getScoreFromAllDices(getCurrentPlayer().getTurnArrayList(), settings, true, true, getCurrentPlayer().getCurrentTurn().getCurrentRound()));
        }
    }

    /**
     * sets next player and shows win dialog incase a player already won
     * is called when player pressed the next button
     * sets win rank
     * sets next turn, BEFORE player is changed
     */
    public void nextPlayer()
    {
        if (!isGameOver)
        {
            if (getCurrentPlayer().hasWon())
            {
                if (getCurrentPlayer().getWinRank() == -1)
                {
                    if (getNumberOfWinners() < 1)
                    {
                        if (settings.isGameOverAfterFirstPlayerWon())
                        {
                            Main.showGameOverDialog(Main.language.getGameOverAlertHeader(),
                                    Main.language.getGameOverAlertContent() + " " + getCurrentPlayer().getPlayerName());
                        }
                        else
                        {
                            Main.showWinAlert(getCurrentPlayer().getPlayerName(), Main.language.getWinAlertHeaderText(),
                                    Main.language.getWinAlertContentText());
                        }
                    }

                    getCurrentPlayer().setWinRank(getNumberOfWinners() + 1);
                    if (settings.isGameOverAfterFirstPlayerWon())
                    {
                        isGameOver = true;
                        return;
                    }
                }
            }

            //re-roll dices for next turn
            getCurrentPlayer().initDice();

            //set next turn
            getCurrentPlayer().nextTurn();
            setNextPlayerNumber();

            moveAI();
        }
        else
        {
            if (!settings.isGameOverAfterFirstPlayerWon())
                Main.showGameOverDialog(Main.language.getGameOverAlertHeader(), getWinString());
        }
    }

    private void moveAI()
    {
        if (getCurrentPlayer().isAI())
        {
            ((AI) getCurrentPlayer()).draw();
        }
    }

    /**  next player number automatically and returns void, when it found next player
     * with winRank = -1 which means the player is still in the game and hasn't won
     */
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
        Main.showGameOverDialog(Main.language.getWinAlertHeaderText(), getWinString());

    }

    /**gets a string which contains all winners in order
     * 1 : Player 3
     * 2 : Player 1
     * 3 : Player 2
     * */
    private String getWinString()
    {
        HashMap<Integer, Player> winnersHashMap = getWinners();
        StringBuilder winStringBuilder = new StringBuilder();
        for (Integer key : winnersHashMap.keySet())
        {
            if (winnersHashMap.get(key) != null)
            {
                winStringBuilder.append(key + ". " + Main.language.getGameOverPlace() + " : " + winnersHashMap.get(key).getPlayerName() + "\n");
            }
        }
        return winStringBuilder.toString();
    }

    /**
     * gets numbe of winners, based on the players with a winRank != -1
     * @return
     */
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

    /**
     * returns rank of player -> player object in Hashmap
     */
    private HashMap<Integer, Player> getWinners()
    {
        HashMap<Integer, Player> winPlayersSorted = new HashMap<>();
        for (Player currentPlayer : players)
        {
            winPlayersSorted.put(currentPlayer.getWinRank(), currentPlayer);
        }
        return winPlayersSorted;
    }

    /**
     * needed for preparing the listView --> fill with empty cells on load
     * if game loaded empty HBoxes need to be created.
     */
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

    /**
     * settings are set if a game was loaded, but settings were changed after the fact
     * also settings of players are updated
     * @param settings
     */
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

}
