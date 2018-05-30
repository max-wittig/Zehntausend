package com.spaghettic0der.zehntausend.ui;


import com.spaghettic0der.zehntausend.*;
import com.spaghettic0der.zehntausend.ai.AIType;
import com.spaghettic0der.zehntausend.helper.Debug;
import com.spaghettic0der.zehntausend.helper.JsonHelper;
import com.spaghettic0der.zehntausend.extras.Language;
import com.spaghettic0der.zehntausend.extras.Settings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;

public class SettingsUI extends UI
{
    private static boolean selfTrigger = false;
    private final int MAX_AI = 4;
    private AIType currentType = null;

    public SettingsUI(Settings globalSettings, Language language, Main main, JsonHelper jsonHelper, Stage primaryStage)
    {
        super(globalSettings, language, main, jsonHelper, primaryStage);
    }

    @Override
    public void show()
    {
        ArrayList<Settings> settingsArrayList = new ArrayList<>();
        Settings gameSettings = main.getGame().getSettings();

        globalSettings.setSettingsName(language.getGlobalSettings());

        /*
        player has loaded a game, otherwise settings are identical
         */
        if (gameSettings != globalSettings)
        {
            gameSettings.setSettingsName(language.getGameSettings());
            settingsArrayList.add(gameSettings);
        }

        settingsArrayList.add(globalSettings);
        BorderPane borderPane = new BorderPane();
        Scene settingsScene = new Scene(borderPane, globalSettings.getWidth(), globalSettings.getHeight());
        Accordion accordion = new Accordion();
        int minWidth = 200;

        /*
         * This loops through settings because if the player has loaded a game
         * settings of game and globalSettings can be individually changed
         * ui elements are created twice
         */
        for (Settings currentSettings : settingsArrayList)
        {
            TitledPane titledPane = new TitledPane();
            titledPane.setText(currentSettings.getSettingsName());
            boolean isGlobal = (currentSettings.getSettingsName().equals(globalSettings.getSettingsName()));
            if (isGlobal)
            {
                accordion.setExpandedPane(titledPane);
            }
            VBox vBox = new VBox();
            //player count
            HBox playerHBox = new HBox();
            Label playerLabel = new Label(language.getPlayers() + ":");
            playerLabel.setMinWidth(minWidth);
            Slider playerSlider = new Slider(0, 6, currentSettings.getTotalPlayers());
            if (!isGlobal)
            {
                playerSlider.setDisable(true);
            }
            playerSlider.setMajorTickUnit(1);
            playerSlider.setMinorTickCount(0);
            playerSlider.setSnapToTicks(true);
            playerSlider.setShowTickLabels(true);
            playerHBox.getChildren().addAll(playerLabel, playerSlider);
            HBox.setHgrow(playerSlider, Priority.ALWAYS);
            HBox.setMargin(playerLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(playerHBox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(playerHBox);

            //ai Count
            HBox aiHbox = new HBox();
            HBox chosenAIHBox = new HBox();
            chosenAIHBox.setSpacing(1);
            restoreAI(chosenAIHBox, currentSettings);
            Label aiLabel = new Label(language.getAI() + ":");
            aiLabel.setMinWidth(minWidth);
            Button aiAddButton = new Button(language.getAdd());
            currentType = AIType.NORMAL;
            aiAddButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (chosenAIHBox.getChildren().size() <= MAX_AI)
                    {
                        AIButton aiButton = new AIButton(currentType);
                        aiButton.setOnAction(e ->
                        {
                            aiButton.nextType();
                        });
                        chosenAIHBox.getChildren().add(aiButton);
                    }
                }
            });
            Button clearAIButton = new Button(language.getClear());
            clearAIButton.setOnAction(event -> chosenAIHBox.getChildren().clear());
            VBox aiButtonVBox = new VBox(aiAddButton, clearAIButton);
            aiHbox.getChildren().addAll(aiLabel, aiButtonVBox, chosenAIHBox);
            HBox.setMargin(aiLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(aiHbox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(aiHbox);

            //ai delay
            HBox aiDelayHBox = new HBox();
            Label aiDelayLabel = new Label(language.getAI() + " " + language.getDelay() + ":");
            aiDelayLabel.setMinWidth(minWidth);
            Slider aiDelaySlider = new Slider(0, 3000, currentSettings.getAiDelay());
            aiDelaySlider.setMajorTickUnit(500);
            aiDelaySlider.setMinorTickCount(1);
            aiDelaySlider.setSnapToTicks(true);
            aiDelaySlider.setShowTickLabels(true);
            aiDelayHBox.getChildren().addAll(aiDelayLabel, aiDelaySlider);
            HBox.setHgrow(aiDelaySlider, Priority.ALWAYS);
            HBox.setMargin(aiDelayLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(aiDelayHBox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(aiDelayHBox);

            //dice count
            HBox diceHBox = new HBox();
            Label diceLabel = new Label(language.getDices() + ":");
            diceLabel.setMinWidth(minWidth);
            Slider diceSlider = new Slider(4, 10, currentSettings.getTotalDiceNumber());
            diceSlider.setMajorTickUnit(1);
            diceSlider.setMinorTickCount(0);
            diceSlider.setSnapToTicks(true);
            diceSlider.setShowTickLabels(true);
            diceHBox.getChildren().addAll(diceLabel, diceSlider);
            HBox.setHgrow(diceSlider, Priority.ALWAYS);
            HBox.setMargin(diceLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(diceHBox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(diceHBox);

            //win score
            HBox winScoreHBox = new HBox();
            Label winScoreLabel = new Label(language.getWinScore());
            winScoreLabel.setMinWidth(minWidth);
            TextField winScoreTextField = new TextField("" + currentSettings.getMinScoreRequiredToWin());
            winScoreHBox.getChildren().addAll(winScoreLabel, winScoreTextField);
            HBox.setHgrow(winScoreTextField, Priority.ALWAYS);
            HBox.setMargin(winScoreTextField, new Insets(-4, 0, 0, 0));
            HBox.setMargin(winScoreLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(winScoreHBox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(winScoreHBox);

            //minsave Score
            HBox minScoreHBox = new HBox();
            Label minScoreLabel = new Label(language.getMinScoreToSave());
            minScoreLabel.setMinWidth(minWidth);
            TextField minScoreTextField = new TextField("" + currentSettings.getMinScoreRequiredToSaveInRound());
            minScoreHBox.getChildren().addAll(minScoreLabel, minScoreTextField);
            HBox.setHgrow(minScoreTextField, Priority.ALWAYS);
            HBox.setMargin(minScoreTextField, new Insets(-4, 0, 0, 0));
            HBox.setMargin(minScoreLabel, new Insets(0, 20, 0, 20));
            VBox.setMargin(minScoreHBox, new Insets(20, 40, 20, 20));
            vBox.getChildren().add(minScoreHBox);


            //-----------------rules------------------

            //street
            HBox streetHBox = new HBox();
            streetHBox.setPrefWidth(currentSettings.getWidth());
            CheckBox streetCheckBox = new CheckBox();
            streetCheckBox.setMinWidth(minWidth - 20);
            streetCheckBox.setText(language.getStreet());
            streetCheckBox.setSelected(currentSettings.isStreetEnabled());

            TextField streetTextField = new TextField("" + currentSettings.getScoreStreet());
            streetHBox.getChildren().addAll(streetCheckBox, streetTextField);
            vBox.getChildren().add(streetHBox);
            HBox.setMargin(streetCheckBox, new Insets(0, 20, 0, 20));
            HBox.setMargin(streetTextField, new Insets(-4, 20, 0, 20));
            HBox.setHgrow(streetTextField, Priority.ALWAYS);
            VBox.setMargin(streetHBox, new Insets(20, 20, 20, 20));

            if (!streetCheckBox.isSelected())
            {
                streetTextField.setDisable(true);
            }
            else
            {
                streetTextField.setDisable(false);
            }


            streetCheckBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (!streetCheckBox.isSelected())
                    {
                        streetTextField.setDisable(true);
                    }
                    else
                    {
                        streetTextField.setDisable(false);
                    }
                }
            });

            //Three x two
            HBox threeXTwoHBox = new HBox();
            threeXTwoHBox.setPrefWidth(currentSettings.getWidth());
            CheckBox threeXTwoCheckBox = new CheckBox(language.getThreeTimesTwo());
            threeXTwoCheckBox.setMinWidth(minWidth - 20);
            threeXTwoCheckBox.setSelected(currentSettings.isThreeXTwoEnabled());

            TextField threeXTwoTextField = new TextField("" + currentSettings.getScoreThreeXTwo());
            threeXTwoHBox.getChildren().addAll(threeXTwoCheckBox, threeXTwoTextField);
            vBox.getChildren().add(threeXTwoHBox);
            HBox.setMargin(threeXTwoCheckBox, new Insets(0, 20, 0, 20));
            HBox.setMargin(threeXTwoTextField, new Insets(-4, 20, 0, 20));
            HBox.setHgrow(threeXTwoTextField, Priority.ALWAYS);
            VBox.setMargin(threeXTwoHBox, new Insets(20, 20, 20, 20));

            if (!threeXTwoCheckBox.isSelected())
            {
                threeXTwoTextField.setDisable(true);
            }
            else
            {
                threeXTwoTextField.setDisable(false);
            }

            threeXTwoCheckBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (!threeXTwoCheckBox.isSelected())
                    {
                        threeXTwoTextField.setDisable(true);
                    }
                    else
                    {
                        threeXTwoTextField.setDisable(false);
                    }
                }
            });

            //six dices in a row
            HBox sixDicesInARowHBox = new HBox();
            sixDicesInARowHBox.setPrefWidth(currentSettings.getWidth());
            CheckBox sixDicesInARowCheckBox = new CheckBox(language.getSixDicesInARow());
            sixDicesInARowCheckBox.setMinWidth(minWidth - 20);
            sixDicesInARowCheckBox.setSelected(currentSettings.isSixDicesInARowEnabled());

            TextField sixDicesInARowTextField = new TextField("" + currentSettings.getScoreSixDicesInARow());
            sixDicesInARowHBox.getChildren().addAll(sixDicesInARowCheckBox, sixDicesInARowTextField);
            vBox.getChildren().add(sixDicesInARowHBox);
            HBox.setMargin(sixDicesInARowCheckBox, new Insets(0, 20, 0, 20));
            HBox.setMargin(sixDicesInARowTextField, new Insets(-4, 20, 0, 20));
            HBox.setHgrow(sixDicesInARowTextField, Priority.ALWAYS);
            VBox.setMargin(sixDicesInARowHBox, new Insets(20, 20, 20, 20));

            if (!sixDicesInARowCheckBox.isSelected())
            {
                sixDicesInARowTextField.setDisable(true);
            }
            else
            {
                sixDicesInARowTextField.setDisable(false);
            }

            sixDicesInARowCheckBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (!sixDicesInARowCheckBox.isSelected())
                    {
                        sixDicesInARowTextField.setDisable(true);
                    }
                    else
                    {
                        sixDicesInARowTextField.setDisable(false);
                    }
                }
            });

            //pyramid
            HBox pyramidHBox = new HBox();
            pyramidHBox.setPrefWidth(currentSettings.getWidth());
            CheckBox pyramidCheckBox = new CheckBox(language.getPyramid());
            pyramidCheckBox.setMinWidth(minWidth - 20);
            pyramidCheckBox.setSelected(currentSettings.isPyramidEnabled());

            TextField pyramidTextField = new TextField("" + currentSettings.getScorePyramid());
            pyramidHBox.getChildren().addAll(pyramidCheckBox, pyramidTextField);
            vBox.getChildren().add(pyramidHBox);
            HBox.setMargin(pyramidCheckBox, new Insets(0, 20, 0, 20));
            HBox.setMargin(pyramidTextField, new Insets(-4, 20, 0, 20));
            HBox.setHgrow(pyramidTextField, Priority.ALWAYS);
            VBox.setMargin(pyramidHBox, new Insets(20, 20, 20, 20));

            if (!pyramidCheckBox.isSelected())
            {
                pyramidTextField.setDisable(true);
            }
            else
            {
                pyramidTextField.setDisable(false);
            }

            pyramidCheckBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (!pyramidCheckBox.isSelected())
                    {
                        pyramidTextField.setDisable(true);
                    }
                    else
                    {
                        pyramidTextField.setDisable(false);
                    }
                }
            });

            //confirmation
            HBox confirmationHBox = new HBox();
            confirmationHBox.setPrefWidth(currentSettings.getWidth());
            CheckBox confirmationCheckBox = new CheckBox();
            confirmationCheckBox.setMinWidth(minWidth - 20);
            confirmationCheckBox.setText(language.getConfirmationScore());
            confirmationCheckBox.setSelected(currentSettings.isClearAllNeedsConfirmationInNextRound());

            TextField confirmationTextField = new TextField("" + currentSettings.getMinScoreToConfirm());
            confirmationHBox.getChildren().addAll(confirmationCheckBox, confirmationTextField);
            vBox.getChildren().add(confirmationHBox);
            HBox.setMargin(confirmationCheckBox, new Insets(0, 20, 0, 20));
            HBox.setMargin(confirmationTextField, new Insets(-4, 20, 0, 20));
            HBox.setHgrow(confirmationTextField, Priority.ALWAYS);
            VBox.setMargin(confirmationHBox, new Insets(20, 20, 20, 20));

            if (!confirmationCheckBox.isSelected())
            {
                confirmationTextField.setDisable(true);
            }
            else
            {
                confirmationTextField.setDisable(false);
            }

            confirmationCheckBox.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    if (!confirmationCheckBox.isSelected())
                    {
                        confirmationTextField.setDisable(true);
                    }
                    else
                    {
                        confirmationTextField.setDisable(false);
                    }
                }
            });

            //full house
            HBox fullHouseHBox = new HBox();
            CheckBox fullHouseCheckBox = new CheckBox(language.getFullHouse());
            fullHouseCheckBox.setSelected(currentSettings.isFullHouseEnabled());
            fullHouseCheckBox.setDisable(diceSlider.getValue() != 5);
            diceSlider.valueProperty().addListener(new ChangeListener<Number>()
            {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                {
                    fullHouseCheckBox.setDisable(diceSlider.getValue() != 5);
                }
            });

            fullHouseHBox.getChildren().add(fullHouseCheckBox);
            vBox.getChildren().add(fullHouseHBox);
            VBox.setMargin(fullHouseHBox, new Insets(20, 20, 20, 40));

            //game over after first player won
            HBox gameOverAfterFirstPlayerWonHBox = new HBox();
            CheckBox gameOverAfterFirstPlayerWonCheckBox = new CheckBox(language.getGameOverAfterFirstPlayerWon());
            gameOverAfterFirstPlayerWonCheckBox.setSelected(currentSettings.isGameOverAfterFirstPlayerWon());
            gameOverAfterFirstPlayerWonHBox.getChildren().add(gameOverAfterFirstPlayerWonCheckBox);
            vBox.getChildren().add(gameOverAfterFirstPlayerWonHBox);
            VBox.setMargin(gameOverAfterFirstPlayerWonHBox, new Insets(20, 20, 20, 40));

            //dice image shown
            HBox diceImagesShownHBox = new HBox();
            CheckBox diceImagesShownCheckBox = new CheckBox(language.getShowDiceImages());
            diceImagesShownCheckBox.setSelected(currentSettings.isDiceImageShown());
            diceImagesShownHBox.getChildren().add(diceImagesShownCheckBox);
            vBox.getChildren().add(diceImagesShownHBox);
            VBox.setMargin(diceImagesShownHBox, new Insets(20, 20, 20, 40));

            //save and cancel button
            HBox buttonHBox = new HBox();
            buttonHBox.setAlignment(Pos.CENTER);
            Button saveSettingsButton = new Button(language.getSave());
            saveSettingsButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    currentSettings.setTotalPlayers((int) playerSlider.getValue());
                    currentSettings.setTotalDiceNumber((int) diceSlider.getValue());

                    currentSettings.setMinScoreRequiredToWin(Integer.parseInt(winScoreTextField.getText()));
                    currentSettings.setMinScoreRequiredToSaveInRound(Integer.parseInt(minScoreTextField.getText()));

                    currentSettings.setStreetEnabled(streetCheckBox.isSelected());
                    currentSettings.setScoreStreet(Integer.parseInt(streetTextField.getText()));

                    currentSettings.setScoreSixDicesInARow(Integer.parseInt(sixDicesInARowTextField.getText()));
                    currentSettings.setSixDicesInARowEnabled(sixDicesInARowCheckBox.isSelected());

                    currentSettings.setThreeXTwoEnabled(threeXTwoCheckBox.isSelected());
                    currentSettings.setScoreThreeXTwo(Integer.parseInt(threeXTwoTextField.getText()));

                    currentSettings.setPyramidEnabled(pyramidCheckBox.isSelected());
                    currentSettings.setScorePyramid(Integer.parseInt(pyramidTextField.getText()));

                    currentSettings.setClearAllNeedsConfirmationInNextRound(confirmationCheckBox.isSelected());
                    currentSettings.setMinScoreToConfirm(Integer.parseInt(confirmationTextField.getText()));

                    currentSettings.setFullHouseEnabled(fullHouseCheckBox.isSelected());

                    currentSettings.setGameOverAfterFirstPlayerWon(gameOverAfterFirstPlayerWonCheckBox.isSelected());

                    currentSettings.setDiceImageShown(diceImagesShownCheckBox.isSelected());

                    currentSettings.setNumberEasyAI(getNumberEasyAI(chosenAIHBox));
                    currentSettings.setNumberNormalAI(getNumberNormalAI(chosenAIHBox));
                    currentSettings.setNumberHardAI(getNumberHardAI(chosenAIHBox));
                    currentSettings.setAiDelay((int) aiDelaySlider.getValue());

                    Debug.write(Debug.getClassName(this) + " - " + " Settings saved");
                    /*
                    global settings restart the current game, game settings do not
                     */
                    if (currentSettings.getTotalNumberPlayersAndAI() > 0)
                    {
                        if (isGlobal)
                        {
                            jsonHelper.saveSettings(globalSettings);
                            main.nextGame(globalSettings);
                        }
                        else
                        {
                            main.getGame().setSettings(currentSettings);
                        }


                        settingsStage.close();
                    }
                    else
                    {
                        Main.showAlert(null, "No players!");
                    }
                }
            });

            Button cancelSettingsButton = new Button(language.getCancel());
            cancelSettingsButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    settingsStage.close();
                }
            });

            Button restoreDefaultsButton = new Button(language.getRestoreDefault());
            restoreDefaultsButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Debug.write(Debug.getClassName(this) + " - " + Debug.getLineNumber() + " Default settings loaded");
                    Settings tempSettings = jsonHelper.restoreSettings();
                    if (isGlobal)
                    {
                        globalSettings = tempSettings;
                        main.nextGame(globalSettings);
                    }
                    else
                    {
                        main.getGame().setSettings(tempSettings);
                    }
                    settingsStage.close();
                }
            });
            buttonHBox.getChildren().addAll(cancelSettingsButton, saveSettingsButton, restoreDefaultsButton);
            HBox.setMargin(cancelSettingsButton, new Insets(20, 20, 20, 20));
            HBox.setMargin(restoreDefaultsButton, new Insets(20, 20, 20, 20));
            vBox.getChildren().add(buttonHBox);
            ScrollPane scrollPane = new ScrollPane(vBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);

            /*
            Makes scrolling in settings slightly faster
             */
            scrollPane.vvalueProperty().addListener(new ChangeListener<Number>()
            {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                {
                    double increment = 0.256;

                    if (!selfTrigger)
                    {
                        //scroll down
                        if (oldValue.doubleValue() < newValue.doubleValue())
                        {
                            if ((newValue.doubleValue() + increment) < (1.0 - increment))
                            {
                                selfTrigger = true;
                                scrollPane.setVvalue(newValue.doubleValue() + increment);
                            }
                        }
                        else
                        {
                            if ((newValue.doubleValue() - increment) > increment)
                            {
                                selfTrigger = true;
                                scrollPane.setVvalue(newValue.doubleValue() - increment);
                            }
                        }
                    }
                    else
                    {
                        selfTrigger = false;
                    }
                }
            });

            titledPane.setContent(scrollPane);
            accordion.getPanes().add(titledPane);

        }
        borderPane.setCenter(accordion);
        settingsStage = new Stage();
        settingsStage.initOwner(primaryStage);
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.centerOnScreen();
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    private void restoreEasyAI(HBox chosenAIHBox, Settings currentSettings)
    {
        restoreAICount(chosenAIHBox, currentSettings.getNumberEasyAI(), AIType.EASY);
    }

    private void restoreNormalAI(HBox chosenAIHBox, Settings currentSettings)
    {
        restoreAICount(chosenAIHBox, currentSettings.getNumberNormalAI(), AIType.NORMAL);
    }

    private void restoreHardAI(HBox chosenAIHBox, Settings currentSettings)
    {
        restoreAICount(chosenAIHBox, currentSettings.getNumberHardAI(), AIType.HARD);
    }

    private void restoreAI(HBox chosenAIHBox, Settings currentSettings)
    {
        restoreEasyAI(chosenAIHBox, currentSettings);
        restoreNormalAI(chosenAIHBox, currentSettings);
        restoreHardAI(chosenAIHBox, currentSettings);
    }

    private void restoreAICount(HBox chosenAIHBox, int numberAI, AIType currentTypeRestore)
    {
        for (int i = 0; i < numberAI; i++)
        {
            AIButton aiButton = new AIButton(currentTypeRestore);
            aiButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    aiButton.nextType();
                }
            });
            chosenAIHBox.getChildren().add(aiButton);
        }
    }

    private int getNumberEasyAI(HBox chosenAIHBox)
    {
        int number = 0;
        for (Node node : chosenAIHBox.getChildren())
        {
            if (node.getId().equals(AIType.EASY.toString()))
            {
                number++;
            }
        }
        return number;
    }

    private int getNumberNormalAI(HBox chosenAIHBox)
    {
        int number = 0;
        for (Node node : chosenAIHBox.getChildren())
        {
            if (node.getId().equals(AIType.NORMAL.toString()))
            {
                number++;
            }
        }
        return number;
    }

    private int getNumberHardAI(HBox chosenAIHBox)
    {
        int number = 0;
        for (Node node : chosenAIHBox.getChildren())
        {
            if (node.getId().equals(AIType.HARD.toString()))
            {
                number++;
            }
        }
        return number;
    }
}
