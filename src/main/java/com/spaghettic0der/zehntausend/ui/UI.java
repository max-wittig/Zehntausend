package com.spaghettic0der.zehntausend.ui;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.helper.JsonHelper;
import com.spaghettic0der.zehntausend.extras.Language;
import com.spaghettic0der.zehntausend.extras.Settings;
import javafx.stage.Stage;

public abstract class UI
{
    protected Settings globalSettings;
    protected Language language;
    protected Main main;
    protected JsonHelper jsonHelper;
    protected Stage settingsStage;
    protected Stage primaryStage;

    public UI(Settings globalSettings, Language language, Main main, JsonHelper jsonHelper, Stage primaryStage)
    {

        this.globalSettings = globalSettings;
        this.language = language;
        this.main = main;
        this.jsonHelper = jsonHelper;
        this.primaryStage = primaryStage;
    }

    public abstract void show();
}
