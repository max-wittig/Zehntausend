package com.spaghettic0der;


import java.util.ArrayList;


public class Player
{
    private ArrayList<Dice> remainingDices;
    private int playerNumber;
    private int score = 0;
    private String playerName = null;
    private ArrayList<Turn> turnArrayList;

    public Player(int playerNumber)
    {
        turnArrayList = new ArrayList<>();
        remainingDices = new ArrayList<>();
        initDice();
        this.playerNumber = playerNumber;
    }

    public void initDice()
    {
        remainingDices.clear();
        for (int i = 0; i < Settings.TOTAL_DICE_NUMBER; i++)
        {
            Dice dice = new Dice();
            dice.roll();
            remainingDices.add(dice);
        }
    }


    public Turn getLastTurn()
    {
        Turn turn;
        if (turnArrayList.size() > 0)
        {
            turn = turnArrayList.get(turnArrayList.size() - 1);
        }
        else
        {
            turn = new Turn();
            turnArrayList.add(turn);
        }
        return turn;
    }

    public void rollDice()
    {
        if (!remainingDices.isEmpty())
        {
            for (int i = 0; i < remainingDices.size(); i++)
            {
                remainingDices.get(i).roll();
            }
            Roll roll = new Roll();
            getLastTurn().getLastRound().addToRound(roll);
        }
        else
        {
            Round round = new Round();
            Roll roll = new Roll();
            round.addToRound(roll);
            getLastTurn().addToTurn(round);
            initDice();
            //getLastTurn().getLastRound().addToRound(roll);
            //clears dices on board and re_init them again
            //incase you finish the roll

        }
    }

    public ArrayList<Dice> getRemainingDices()
    {
        return remainingDices;
    }


    public void nextTurn()
    {
        Turn turn = new Turn();
        Round round = new Round();
        turn.addToTurn(round);
        turnArrayList.add(turn);
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
}
