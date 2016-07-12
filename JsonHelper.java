package com.spaghettic0der;


import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonHelper
{
    private Gson gson;

    public JsonHelper()
    {
        gson = new Gson();
    }

    public void saveGameState(Game game)
    {
        String json = gson.toJson(game);
        try
        {
            File file = new File("game.json");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(json);
            out.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    public Game loadGameState()
    {
        try
        {
            String json = new String(Files.readAllBytes(Paths.get("game.json")));
            Game game = gson.fromJson(json, Game.class);
            return game;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        return null;

    }
}
