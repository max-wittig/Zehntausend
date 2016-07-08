package com.spaghettic0der;


import java.util.ArrayList;
import java.util.Arrays;

public class Player
{
    private ArrayList<Dice> remainingDices;
    private ArrayList<Dice> drawnDices;
    private int playerNumber;
    private int score = 0;
    private String playerName = null;

    public Player(int playerNumber)
    {
        remainingDices = new ArrayList<>();
        drawnDices = new ArrayList<>();
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
        drawnDices.clear();
        remainingDices.clear();
        initDice();
    }

    public void rollDice()
    {
        if (!remainingDices.isEmpty())
        {
            for (int i = 0; i < remainingDices.size(); i++)
            {
                remainingDices.get(i).roll();
            }

            for (int i = 0; i < drawnDices.size(); i++)
            {
                drawnDices.get(i).setDiceDrawnThisRound(false);
            }
        }
        else
        {
            //clears dices on board and re_init them again
            //incase you finish the roll
            clearDices();
        }

        // FIXME: 08.07.16 Debug
        remainingDices.clear();
        ArrayList<Dice> debugStreetArrayList = new ArrayList<>();
        for (int i = 1; i <= 6; i++)
        {
            Dice dice = new Dice(i);
            dice.setDiceNumber(i);
            debugStreetArrayList.add(dice);

        }

        remainingDices = debugStreetArrayList;
    }

    public ArrayList<Dice> getRemainingDices()
    {
        return remainingDices;
    }

    public ArrayList<Dice> getDrawnDices()
    {
        return drawnDices;
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
        score += number;
    }

    public String getPlayerName()
    {
        return playerName;
    }
}
