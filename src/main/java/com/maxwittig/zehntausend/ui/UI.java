package com.maxwittig.zehntausend.ui;


import com.maxwittig.zehntausend.Main;
import com.maxwittig.zehntausend.extras.Settings;
import com.maxwittig.zehntausend.Main;
import com.maxwittig.zehntausend.extras.Language;
import com.maxwittig.zehntausend.extras.Settings;
import com.maxwittig.zehntausend.helper.JsonHelper;
import javafx.stage.Stage;

public abstract class UI {
    protected Settings globalSettings;
    protected Language language;
    protected Main main;
    protected JsonHelper jsonHelper;
    protected Stage settingsStage;
    protected Stage primaryStage;

    public UI(Settings globalSettings, Language language, Main main, JsonHelper jsonHelper, Stage primaryStage) {

        this.globalSettings = globalSettings;
        this.language = language;
        this.main = main;
        this.jsonHelper = jsonHelper;
        this.primaryStage = primaryStage;
    }

    public abstract void show();
}
