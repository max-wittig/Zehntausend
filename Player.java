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
        getLastTurn().getRoundArrayList().add(round);
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
        Roll roll = new Roll();
        getLastTurn().getLastRound().addToRound(roll);
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

    //from last turn only
    public ArrayList<Dice> getAllDrawnDices()
    {
        ArrayList<Dice> dices = new ArrayList<>();

        for (Round currentRound : getLastTurn().getRoundArrayList())
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

    public void nextTurn()
    {
        Turn turn = new Turn();
        Round round = new Round();
        Roll roll = new Roll();
        round.addToRound(roll);
        turn.addToTurn(round);
        turnArrayList.add(turn);
    }

    //delete everything
    /*public void clearRoundArrayList()
    {
        roundArrayList.clear();
    }*/

    public void removeLastDrawnDiceWithNumber(int number)
    {
        Roll roll = getLastTurn().getLastRound().getLastRoll();
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
        if (getLastTurn().getLastRound().getRollArrayList().size() > 0)
        {
            roll = getLastTurn().getLastRound().getLastRoll();
        }
        else
        {
            roll = new Roll();
            getLastTurn().getLastRound().addToRound(roll);
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
}
