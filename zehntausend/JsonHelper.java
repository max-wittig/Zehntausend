package com.spaghettic0der.zehntausend;


import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;


public class JsonHelper
{
    private Gson gson;

    public JsonHelper()
    {
        gson = new Gson();
    }

    public void saveSettings(Settings settings)
    {
        saveJSON(settings, "settings.json");
    }

    public Settings loadSettings()
    {
        return gson.fromJson(loadJSON("settings.json"), Settings.class);
    }

    private String loadJSON(String filename)
    {
        try
        {
            String json = new String(Files.readAllBytes(Paths.get(filename)));
            return json;
        }
        catch (Exception e)
        {
            System.out.println(e);
            Settings settings = new Settings();
            saveSettings(settings);
            System.out.println("Wrote default settings to disk");
        }

        return null;
    }

    private void saveJSON(Object object, String filename)
    {
        String json = gson.toJson(object);
        try
        {
            File file = new File(filename);
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(json);
            out.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void saveGame(Game game)
    {
        saveJSON(game, "game.json");
    }

    public Game loadGameState()
    {
        return gson.fromJson(loadJSON("game.json"), Game.class);

    }
}
