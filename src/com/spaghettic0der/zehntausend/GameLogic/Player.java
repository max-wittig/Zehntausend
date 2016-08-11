package com.spaghettic0der.zehntausend.GameLogic;

import com.spaghettic0der.zehntausend.Extras.Debug;
import com.spaghettic0der.zehntausend.Extras.Settings;
import com.spaghettic0der.zehntausend.Main;

import java.util.ArrayList;

/**
 * saved all turns, which contains all the rounds, which contain all the rolls, which contain
 * all the dices that were drawn
 */
public class Player
{
    protected String playerName = null;
    protected ArrayList<Dice> remainingDices;
    protected ArrayList<Turn> turnArrayList;
    protected Settings settings;
    private int playerNumber;
    private int winRank = -1;
    private PlayerType playerType;

    public Player(int playerNumber, Settings settings)
    {
        this.settings = settings;
        playerName = Main.language.getPlayer() + " " + (playerNumber + 1);
        turnArrayList = new ArrayList<>();
        remainingDices = new ArrayList<>();
        initDice();
        this.playerNumber = playerNumber;
        nextTurn();
    }

    /**
     * initalized remainingDices
     */
    public void initDice()
    {
        remainingDices.clear();
        for (int i = 0; i < settings.getTotalDiceNumber(); i++)
        {
            Dice dice = new Dice();
            dice.roll();
            remainingDices.add(dice);
        }

        Debug.write(Debug.getClassName(this) + " - " + Debug.getLineNumber() + " Remaining Dices added for " + playerName + ": "
                + Debug.diceArrayListToString(remainingDices));
        //addDebugDices(new int[]{1, 1, 5, 3, 3, 3});
    }

    public void addDebugDices(ArrayList<Integer> diceNumbers)
    {
        remainingDices.clear();
        for (Integer i : diceNumbers)
        {
            Dice dice = new Dice();
            dice.setDiceNumber(i);
            remainingDices.add(dice);
        }
    }

    public ArrayList<Dice> getDebugDices(int[] diceNumbers)
    {
        ArrayList<Dice> dices = new ArrayList<>();
        for (Integer diceNumber : diceNumbers)
        {
            Dice dice = new Dice();
            dice.setDiceNumber(diceNumber);
            dices.add(dice);
        }

        return dices;
    }

    public void addDebugDices(int[] diceNumbers)
    {
        remainingDices.clear();
        for (Integer diceNumber : diceNumbers)
        {
            Dice dice = new Dice();
            dice.setDiceNumber(diceNumber);
            remainingDices.add(dice);
        }
    }

    public PlayerType getPlayerType()
    {
        return playerType;
    }

    public void setPlayerType(PlayerType playerType)
    {
        this.playerType = playerType;
    }

    public Turn getCurrentTurn()
    {
        return turnArrayList.get(turnArrayList.size() - 1);
    }

    /**
     * rolls dices and starts a new round, if you clear the board
     */
    public void rollDice()
    {
        Debug.write(Debug.getClassName(this) + " - " + Debug.getLineNumber() + " Dices rolled for " + playerName + " ");
        if (!remainingDices.isEmpty())
        {
            for (int i = 0; i < remainingDices.size(); i++)
            {
                remainingDices.get(i).roll();
            }
            getCurrentTurn().getCurrentRound().nextRoll();
        }
        else
        {
            getCurrentTurn().nextRound();
            initDice();
            //getCurrentTurn().getCurrentRound().addToRound(roll);
            //clears dices on board and re_init them again
            //incase you finish the roll
        }
    }

    public boolean hasWon()
    {
        if (getScore() >= settings.getMinScoreRequiredToWin())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public ArrayList<Dice> getRemainingDices()
    {
        return remainingDices;
    }

    public void nextTurn()
    {
        Turn turn = new Turn();
        turnArrayList.add(turn);
    }

    public int getPlayerNumber()
    {
        return playerNumber;
    }

    public int getScore()
    {
        return Scoring.getScoreFromAllDices(turnArrayList, settings, true, true, getCurrentTurn().getCurrentRound());
    }


    public String getPlayerName()
    {
        return playerName;
    }

    public ArrayList<Turn> getTurnArrayList()
    {
        return turnArrayList;
    }

    public int getWinRank()
    {
        return winRank;
    }

    public void setWinRank(int winRank)
    {
        this.winRank = winRank;
    }

    public boolean isAI()
    {
        if (PlayerType.AI == playerType)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }
}
