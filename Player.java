package com.spaghettic0der;


import java.util.ArrayList;
import java.util.Arrays;

public class Player
{
    private ArrayList<Dice> remainingDices;
    private ArrayList<Roll> rollArrayList;
    private int playerNumber;
    private int score = 0;
    private String playerName = null;

    public Player(int playerNumber)
    {
        rollArrayList = new ArrayList<>();
        remainingDices = new ArrayList<>();
        initDice();
        this.playerNumber = playerNumber;
    }

    private void initDice()
    {
        for (int i = 0; i < Settings.TOTAL_DICE_NUMBER; i++)
        {
            Dice dice = new Dice(i);
            dice.roll();
            remainingDices.add(dice);
        }
    }

    public void clearDices()
    {
        Roll roll = new Roll();
        rollArrayList.add(roll);
        remainingDices.clear();
        initDice();
    }

    public void setLastDrawnDicesToCantBeDrawn()
    {
        for (Dice currentDice : getLastDrawnDices())
        {
            currentDice.setDiceDrawnThisRound(false);
        }
    }

    public void rollDice()
    {
        setLastDrawnDicesToCantBeDrawn();
        Roll roll = new Roll();
        rollArrayList.add(roll);
        if (!remainingDices.isEmpty())
        {
            for (int i = 0; i < remainingDices.size(); i++)
            {
                remainingDices.get(i).roll();
            }
        }
        else
        {
            //clears dices on board and re_init them again
            //incase you finish the roll
            clearDices();
        }
    }

    public ArrayList<Dice> getRemainingDices()
    {
        return remainingDices;
    }

    public ArrayList<Dice> getAllDrawnDices()
    {
        ArrayList<Dice> dices = new ArrayList<>();
        for (Roll currentRoll : rollArrayList)
        {
            for (Dice currentDice : currentRoll.getDrawnDices())
            {
                dices.add(currentDice);
            }
        }
        return dices;
    }

    public void clearRollArrayList()
    {
        rollArrayList.clear();
    }

    public void removeLastDrawnDiceWithNumber(int number)
    {
        Roll roll = rollArrayList.get(rollArrayList.size() - 1);
        for (Dice toRemove : roll.getDrawnDices())
        {
            if (toRemove.getDiceNumber() == number)
            {
                rollArrayList.get(rollArrayList.size() - 1).getDrawnDices().remove(toRemove);
                break;
            }
        }
    }

    public ArrayList<Dice> getLastDrawnDices()
    {
        Roll roll;
        if (rollArrayList.size() > 0)
        {
            roll = rollArrayList.get(rollArrayList.size() - 1);
        }
        else
        {
            roll = new Roll();
            rollArrayList.add(roll);
        }
        return roll.getDrawnDices();
    }

    public int getPlayerNumber()
    {
        return playerNumber;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public void addToScore(int number)
    {
        if (number >= Settings.MIN_SCORE_REQUIRED_TO_SAVE_IN_ROUND)
            score += number;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public ArrayList<Roll> getRollArrayList()
    {
        return rollArrayList;
    }
}
