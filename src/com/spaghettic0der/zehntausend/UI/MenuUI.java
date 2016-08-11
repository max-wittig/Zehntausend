package com.spaghettic0der.zehntausend.UI;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.Extras.JsonHelper;
import com.spaghettic0der.zehntausend.Extras.Language;
import com.spaghettic0der.zehntausend.GameLogic.Dice;
import com.spaghettic0der.zehntausend.GameLogic.Game;
import com.spaghettic0der.zehntausend.Extras.Settings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class MenuUI extends UI
{
    private BorderPane root;

    public MenuUI(Game game, Settings globalSettings, Language language, Main main, JsonHelper jsonHelper, Stage primaryStage, BorderPane root)
    {
        super(game, globalSettings, language, main, jsonHelper, primaryStage);
        this.root = root;
    }


    /**
     * builds a arraylist based on what the user typed in the cheat box for creating
     * dices
     * e.g. userInput: 1 5 2 2 2 3
     * returnValue => [1,5,2,2,2,3] (as arrayList)
     *
     * @param result
     * @return
     */
    private ArrayList<Integer> textInputDialogResultToArrayList(String result)
    {
        System.out.println(result);
        String[] diceNumbersStringArray = result.toString().split(" ");
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (String numberString : diceNumbersStringArray)
        {
            numbers.add(Integer.parseInt(numberString));
        }
        return numbers;
    }

    /**
     * is called by the change language dialog
     */
    private void applySettings()
    {
        jsonHelper.saveSettings(globalSettings);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setContentText(language.getApplicationNeedsToBeRestarted());
        alert.show();
    }

    @Override
    public void show()
    {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu gameMenu = new Menu(language.getGame());

        MenuItem newGameItem = new MenuItem(language.getNewString());
        newGameItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                main.nextGame(globalSettings);
            }
        });
        MenuItem settingsItem = new MenuItem(language.getSettings());
        settingsItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                main.initSettingsStage(primaryStage);
            }
        });
        MenuItem loadItem = new MenuItem(language.getLoad());
        loadItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Game gameToLoad = jsonHelper.loadGame();
                if (gameToLoad != null)
                {
                    game = gameToLoad;
                    main.updateUI();
                    main.rebuildListView();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(language.getError());
                    alert.setContentText(language.getCouldNotLoadSave());
                    alert.show();
                }
            }
        });
        MenuItem saveItem = new MenuItem(language.getSave());
        saveItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                /*if (game.getSettings().getTotalAI() <= 0)
                {
                    jsonHelper.saveGame(game);
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("You cannot save games with AI players currently :(");
                    alert.setHeaderText(null);
                    alert.show();
                }*/
            }
        });

        MenuItem quitItem = new MenuItem(language.getQuit());
        quitItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Platform.exit();
            }
        });
        gameMenu.getItems().addAll(newGameItem, settingsItem, loadItem, saveItem, quitItem);

        Menu cheatMenu = new Menu("Cheat");
        cheatMenu.setVisible(false);
        MenuItem remainingDicesMenuItem = new MenuItem("Remaining Dices");
        remainingDicesMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                TextInputDialog textInputDialog = new TextInputDialog();
                Optional<String> result = textInputDialog.showAndWait();
                if (result.isPresent())
                {
                    game.getCurrentPlayer().addDebugDices(textInputDialogResultToArrayList(result.get()));
                    main.updateUI();
                }
            }
        });

        MenuItem showDebugScoreMenuItem = new MenuItem("Show Debug Score");
        showDebugScoreMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                main.getScoreLabel().setVisible(true);
            }
        });

        MenuItem instaWin = new MenuItem("InstaWin");
        instaWin.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                ArrayList<Dice> drawnDices = game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices();
                drawnDices.clear();
                for (int i = 0; i < game.getSettings().getTotalDiceNumber(); i++)
                {
                    Dice dice = new Dice();
                    dice.setDiceNumber(1);
                    drawnDices.add(dice);
                }
                main.updateScoreOfPlayersInListView();
                game.nextPlayer();
                main.updateUI();
            }
        });

        MenuItem clearScoreItem = new MenuItem("Clear Score");
        clearScoreItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game.getCurrentPlayer().getTurnArrayList().clear();
                game.getCurrentPlayer().nextTurn();
            }
        });

        cheatMenu.getItems().addAll(remainingDicesMenuItem, instaWin, clearScoreItem, showDebugScoreMenuItem);

        Menu languageMenu = new Menu(language.getLanguage());
        MenuItem deItem = new MenuItem("de");
        deItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                globalSettings.setSelectedLanguage(deItem.getText());
                applySettings();
            }
        });
        MenuItem engItem = new MenuItem("eng");
        engItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                globalSettings.setSelectedLanguage(engItem.getText());
                applySettings();
            }
        });
        languageMenu.getItems().addAll(deItem, engItem);

        Menu aboutMenu = new Menu(language.getAbout());
        MenuItem infoItem = new MenuItem(language.getZehntausend());
        infoItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(language.getAbout() + " " + language.getZehntausend() + "\n" + language.getVersion() + ": " + Main.VERSION_NUMBER);
                alert.setContentText(language.getAboutContentText());
                alert.show();

            }
        });

        aboutMenu.getItems().add(infoItem);

        menuBar.getMenus().addAll(gameMenu, languageMenu, cheatMenu, aboutMenu);
        StringBuilder cheatWaiter = new StringBuilder();
        root.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                String cheatWord = "cheat";
                if (cheatWaiter.toString().equals(cheatWord))
                {
                    cheatWaiter.delete(0, cheatWaiter.length());
                    cheatMenu.setVisible(true);
                }
                else if (!cheatMenu.isVisible() && cheatWaiter.length() >= cheatWord.length())
                {
                    cheatWaiter.delete(0, cheatWaiter.length());
                }
                else if (!cheatMenu.isVisible())
                {
                    cheatWaiter.append(event.getText());
                }

            }
        });
        root.setTop(menuBar);
    }
}
