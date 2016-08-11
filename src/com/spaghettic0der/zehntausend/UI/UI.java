package com.spaghettic0der.zehntausend.UI;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.Helper.JsonHelper;
import com.spaghettic0der.zehntausend.Extras.Language;
import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.Extras.Settings;
import javafx.stage.Stage;

public abstract class UI
{
    protected Game game;
    protected Settings globalSettings;
    protected Language language;
    protected Main main;
    protected JsonHelper jsonHelper;
    protected Stage settingsStage;
    protected Stage primaryStage;

    public UI(Game game, Settings globalSettings, Language language, Main main, JsonHelper jsonHelper, Stage primaryStage)
    {
        this.game = game;
        this.globalSettings = globalSettings;
        this.language = language;
        this.main = main;
        this.jsonHelper = jsonHelper;
        this.primaryStage = primaryStage;
    }

    public abstract void show();
}
