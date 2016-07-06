package com.spaghettic0der;


import java.util.ArrayList;

class Game
{
    //totalPlayer count that players this game
    private int totalPlayers = 3;   //TODO remove Hardcode
    //contains all player objects
    private ArrayList<Player> players;
    //shows which players turn it is currently
    private int currentPlayerNumber = 0;
    private int roundNumber = 0;

    private void initPlayers()
    {
        for(int i=0; i < totalPlayers; i++)
        {
            Player player = new Player(i);
            players.add(player);
        }
    }

    public Game()
    {
        players = new ArrayList<>();
        initPlayers();
    }

    public void nextPlayer()
    {
        if(currentPlayerNumber <= totalPlayers)
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
