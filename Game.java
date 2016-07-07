package com.spaghettic0der;


import java.util.ArrayList;
import java.util.HashMap;

class Game
{
    //totalPlayer count that players this game
    private int totalPlayers = 3;   //TODO remove Hardcode
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private int roundNumber = 0;
    private int numberOfDicesDrawnSinceLastRoll = 0;

    private void initPlayers()
    {
        for(int i=0; i < totalPlayers; i++)
        {
            Player player = new Player(i);
            players.add(player);
        }
    }

    private boolean containsDiceNumber(int number, ArrayList<Dice> dices)
    {
        for(Dice currentDice : dices)
        {
            if(currentDice.getDiceNumber() == number)
            {
                return true;
            }
        }
        return false;
    }

    private HashMap<Integer, Integer> getDiceHashMap(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = new HashMap<>();
        for(Dice currentDice : dices)
        {
            int diceNumber = currentDice.getDiceNumber();
            diceHashMap.putIfAbsent(diceNumber, 0);
            diceHashMap.put(diceNumber, diceHashMap.get(diceNumber) + 1);
        }
        return diceHashMap;
    }

    private boolean containsMultiple(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        System.out.println(diceHashMap.keySet());
        System.out.println(diceHashMap.values());
        for(Integer key : diceHashMap.keySet())
        {
            if(key != 5 && key != 1)
            {
                if(diceHashMap.get(key) < 3)
                {
                    return false;
                }
            }
        }

        return true;
    }

    //dices are all thrown in one roll
    public int getScoreFromDicesInRoll(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        int score = 0;
        System.out.println(diceHashMap.keySet());
        System.out.println(diceHashMap.values());
        for(Integer key : diceHashMap.keySet())
        {
            if(diceHashMap.get(key) > 2)
            {
                //min 3 dices
                if(diceHashMap.get(key) == 3)
                {
                    score += key * 100;
                }
                else
                {
                    score += (key * 100) * (diceHashMap.get(key)-2);
                }
            }
            else
            {
                if(key == 5)
                {
                    score =+ 50;
                }

                if(key == 1)
                {
                    score =+ 100;
                }
            }
        }
        return score;
    }

    public void resetNumberOfDicesDrawnSinceLastRoll()
    {
        numberOfDicesDrawnSinceLastRoll = 0;
    }

    /* returns boolean, based on rules if game is valid
    * is called when roll is called
    * */
    public boolean isValidState()
    {
        if(numberOfDicesDrawnSinceLastRoll > 0)
        {
            ArrayList<Dice> dicesSinceLastRoll = new ArrayList<>();
            for(Dice currentDice : getCurrentPlayer().getDrawnDices())
            {
                if(currentDice.canDiceBeDrawnThisRound())
                {
                    dicesSinceLastRoll.add(currentDice);
                }
            }
            getCurrentPlayer().addToScore(getScoreFromDicesInRoll(dicesSinceLastRoll));

            if(!containsDiceNumber(2, dicesSinceLastRoll) && !containsDiceNumber(3, dicesSinceLastRoll)
                    && !containsDiceNumber(4, dicesSinceLastRoll) && !containsDiceNumber(6, dicesSinceLastRoll))
            {
                //only 1 or 5
                return true;
            }
            else
            {
                return containsMultiple(dicesSinceLastRoll);
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

    public Game()
    {
        players = new ArrayList<>();
        initPlayers();
    }


    public void nextPlayer()
    {
        getCurrentPlayer().clearDices();
        if(currentPlayerNumber < totalPlayers-1)
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
