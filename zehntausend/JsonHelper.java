package com.spaghettic0der.zehntausend;


import com.google.gson.Gson;
import sun.misc.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
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
        Settings settings = gson.fromJson(loadJSON("settings.json"), Settings.class);
        if (settings == null)
        {
            settings = new Settings();
            saveSettings(settings);
            System.out.println("Wrote default settings to disk");
            return settings;
        }
        return settings;
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
        try
        {
            return gson.fromJson(loadJSON("game.json"), Game.class);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    public Language loadLanguage(String languageName)
    {
        try
        {
            InputStream in = getClass().getResourceAsStream("/language/" + languageName + ".json");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String file = "";
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                file += line;
            }
            Language language = gson.fromJson(file, Language.class);
            return language;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public void saveLanguage(Language language)
    {
        saveJSON(language, "language.json");
    }
}
