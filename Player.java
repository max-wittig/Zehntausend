package com.spaghettic0der;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Player
{
    private ArrayList<Dice> remainingDices;
    private int playerNumber;
    private int score = 0;
    private String playerName = null;
    private ArrayList<Round> roundArrayList;

    public Player(int playerNumber)
    {
        roundArrayList = new ArrayList<>();
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
        Round round = new Round();
        Roll roll = new Roll();
        round.addToRound(roll);
        roundArrayList.add(round);
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

    public void setLastDrawnDicesToCanBeDrawn()
    {
        for (Dice currentDice : getLastDrawnDices())
        {
            currentDice.setDiceDrawnThisRound(true);
        }
    }

    private Round getLastRound()
    {
        Round round;
        if (roundArrayList.size() > 0)
        {
            round = roundArrayList.get(roundArrayList.size() - 1);
        }
        else
        {
            round = new Round();
            roundArrayList.add(round);
        }
        return round;
    }

    public void rollDice()
    {

        Roll roll = new Roll();
        getLastRound().addToRound(roll);
        if (!remainingDices.isEmpty())
        {
            for (int i = 0; i < remainingDices.size(); i++)
            {
                remainingDices.get(i).roll();
            }
            setLastDrawnDicesToCantBeDrawn();
        }
        else
        {
            setLastDrawnDicesToCanBeDrawn();
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
        for (Round currentRound : roundArrayList)
        {
            for (Roll currentRoll : currentRound.getRollArrayList())
            {
                for (Dice currentDice : currentRoll.getDrawnDices())
                {
                    dices.add(currentDice);
                }
            }
        }
        return dices;
    }

    //delete everything
    public void clearRoundArrayList()
    {
        roundArrayList.clear();
    }

    public void removeLastDrawnDiceWithNumber(int number)
    {
        Roll roll = getLastRound().getLastRoll();
        for (Dice toRemove : roll.getDrawnDices())
        {
            if (toRemove.getDiceNumber() == number)
            {
                roll.getDrawnDices().remove(toRemove);
                System.out.println("Removed: " + toRemove.getDiceNumber());
                break;
            }
        }
    }

    public ArrayList<Dice> getLastDrawnDices()
    {
        Roll roll;
        if (getLastRound().getRollArrayList().size() > 0)
        {
            roll = getLastRound().getLastRoll();
        }
        else
        {
            roll = new Roll();
            getLastRound().addToRound(roll);
        }
        return roll.getDrawnDices();
    }

    public ArrayList<Dice> getDrawnDicesFromLastRound()
    {
        ArrayList<Dice> dices = new ArrayList<>();

        for (Roll currentRoll : getLastRound().getRollArrayList())
        {
            for (Dice currentDice : currentRoll.getDrawnDices())
            {
                dices.add(currentDice);
            }
        }

        return dices;
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

    public ArrayList<Round> getRoundArrayList()
    {
        return roundArrayList;
    }

    public String getPlayerName()
    {
        return playerName;
    }
}
