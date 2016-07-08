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

    public boolean isStreet(ArrayList<Dice> dices)
    {
        if (Settings.STREET_ENABLED)
        {
            if (containsDiceNumber(1, dices) && containsDiceNumber(2, dices) &&
                    containsDiceNumber(3, dices) && containsDiceNumber(4, dices) &&
                    containsDiceNumber(5, dices) && containsDiceNumber(6, dices))
            {
                return true;
            }

        }
        else
        {
            return false;
        }
        return false;
    }

    //dices are all thrown in one roll
    public int getScoreFromDicesInRoll(ArrayList<Dice> dices)
    {
        HashMap<Integer, Integer> diceHashMap = getDiceHashMap(dices);
        int score = 0;

        if (isStreet(dices))
        {
            score += Settings.SCORE_STREET;
        }
        else
        {
            //key is number [1,2,3,4,5,6]
            //diceHashMap.get(key) is occurrence of number
            // TODO: 08.07.16 add variables to make this clearer
            for (Integer key : diceHashMap.keySet())
            {
                if (diceHashMap.get(key) > 2)
                {
                    //3 times 1 == 1000, 4 times 1 == 2000 etc...
                    if (key == 1)
                    {
                        //TODO find better math solution for this
                        int sum = key * 1000;
                        for (int i = 1; i < diceHashMap.get(key) - 2; i++)
                        {
                            sum *= 2;
                        }
                        score += sum;
                    }
                    else
                    {
                        int sum = key * 100;
                        for (int i = 1; i < diceHashMap.get(key) - 2; i++)
                        {
                            sum *= 2;
                        }
                        score += sum;
                    }
                }
                else
                {
                    //no more occurrences then 2 --> normal system
                    if (key == 5)
                    {
                        score += 50 * diceHashMap.get(key);
                    }

                    if (key == 1)
                    {
                        score += 100 * diceHashMap.get(key);
                    }
                }
            }
        }
        return score;
    }

    //resets dices currently in roll to zero, so that the player cannot continue
    //if he doesn't draw any dices
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
            //handles scoring
            getCurrentPlayer().addToScore(getScoreFromDicesInRoll(dicesSinceLastRoll));

            if(!containsDiceNumber(2, dicesSinceLastRoll) && !containsDiceNumber(3, dicesSinceLastRoll)
                    && !containsDiceNumber(4, dicesSinceLastRoll) && !containsDiceNumber(6, dicesSinceLastRoll))
            {
                //only 1 or 5
                return true;
            }
            else
            {
                //checks if there are any multiplications of dice (3 times 2 == 200, 3 times 3 == 300 etc...)
                //if so gameState is valid
                return (containsMultiple(dicesSinceLastRoll) || isStreet(dicesSinceLastRoll));
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
