package com.spaghettic0der;


import com.google.gson.Gson;
import jdk.nashorn.internal.ir.debug.JSONWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;

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
}
