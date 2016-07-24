package com.spaghettic0der.zehntausend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class Main extends Application
{
    public static Language language;
    private final int textButtonWidth = 150;
    private final int textButtonHeight = 50;
    private final int diceButtonSize = 50;
    private final int buttonFontSize = 20;
    private final String versionNumber = "1.0.0";
    private Game game;
    private BorderPane root;
    private HBox remainingDiceHBox;
    private HBox drawnDiceHBox;
    private Label currentPlayerLabel;
    private Label scoreLabel;
    private Label scoreInRoundLabel;
    private Stage settingsStage;
    private Scene mainScene;
    private JsonHelper jsonHelper;
    private Settings globalSettings;
    private VBox centerVBox;
    private ObservableList<HBox> observableList;
    private Label testLabel;
    private Label needsToBeConfirmedLabel;
    private ArrayList<Image> imageArrayList;

    public Main()
    {
        imageArrayList = new ArrayList<>();

        jsonHelper = new JsonHelper();
        globalSettings = jsonHelper.loadSettings();
        if (globalSettings == null)
            globalSettings = new Settings();

        language = jsonHelper.loadLanguage("language_" + globalSettings.getSelectedLanguage());
        if (language == null)
            language = new Language();

        initDiceImages();
    }

    public static void main(String[] args) throws Exception
    {
        Application.launch(args);
    }

    public static void showWinAlert(String playerName, String headerText, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText + " " + playerName);
        alert.show();
    }

    public static void showGameOverDialog(String headerText, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

    private void initDiceImages()
    {
        for (int i = 1; i <= 6; i++)
        {
            Image image = new Image(getClass().getResourceAsStream("res/" + i + ".png"));
            imageArrayList.add(image);
        }
    }

    private void updateUI()
    {
        remainingDiceHBox.getChildren().clear();
        drawnDiceHBox.getChildren().clear();
        createRemainingDiceButtons();
        createDrawnDiceButtons();
        currentPlayerLabel.setText(language.getCurrentPlayer() + ": " + (game.getCurrentPlayer().getPlayerNumber() + 1));
        scoreLabel.setText(language.getScore() + ": " + (Scoring.getScoreFromAllDices(game.getCurrentPlayer().getTurnArrayList(), game.getSettings(), false, true, game.getCurrentPlayer().getCurrentTurn().getCurrentRound())));
        scoreInRoundLabel.setText(language.getScoreInRound() + ": " + Scoring.getScoreFromAllDicesInRound(game.getCurrentPlayer().getCurrentTurn().getRoundArrayList(), false, game.getSettings()));
        if (!game.getCurrentPlayer().getCurrentTurn().isValid(game.getSettings()) && game.isValidState(State.ROLL))
        {
            needsToBeConfirmedLabel.setText(language.getScoreNeedsToBeConfirmed());
        }
        else
        {
            needsToBeConfirmedLabel.setText("");
        }

    }

    private void createDrawnDiceButtons()
    {
        final ArrayList<Dice> dices = game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getDrawnDices();

        for (int i = 0; i < dices.size(); i++)
        {
            Dice currentDice = dices.get(i);
            Button diceButton = new Button();
            diceButton.setPrefWidth(diceButtonSize);
            diceButton.setPrefHeight(diceButtonSize);
            HBox.setMargin(diceButton, new Insets(0, 2, 0, 2));
            diceButton.setFont(new Font(buttonFontSize));
            if (game.getSettings().isDiceImageShown())
            {
                String diceImageLocation = "res/" + dices.get(i).getDiceNumber() + ".png";
                setDiceImage(diceButton, currentDice.getDiceNumber());
            }
            else
            {
                diceButton.setText("" + dices.get(i).getDiceNumber());
            }

            //checks if dices are in last roll -> if so you can still move them back --> yellowgreen color
            if (game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().contains(dices.get(i)))
            {
                diceButton.setStyle("-fx-background-color: #d3ffd5; -fx-border-color: black");
            }
            else
            {
                diceButton.setStyle("-fx-background-color: #ffe9e6; -fx-border-color: black");
            }

            diceButton.setId("" + i);
            diceButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    moveToRemainingDices(currentDice);
                }
            });
            drawnDiceHBox.getChildren().add(diceButton);
        }
    }

    private void setDiceImage(Button diceButton, int diceNumber)
    {
        ImageView imageView = new ImageView(imageArrayList.get(diceNumber - 1));
        imageView.setFitWidth(diceButtonSize - 20);
        imageView.setFitHeight(diceButtonSize - 20);
        diceButton.setGraphic(imageView);
    }

    private void createRemainingDiceButtons()
    {
        final ArrayList<Dice> dices = game.getCurrentPlayer().getRemainingDices();
        for (int i = 0; i < game.getCurrentPlayer().getRemainingDices().size(); i++)
        {
            Dice currentDice = dices.get(i);
            Button diceButton = new Button();
            diceButton.setPrefWidth(diceButtonSize);
            diceButton.setPrefHeight(diceButtonSize);
            HBox.setMargin(diceButton, new Insets(0, 2, 0, 2));
            diceButton.setStyle("-fx-background-color: aliceblue; -fx-border-color: black");
            diceButton.setFont(new Font(buttonFontSize));
            diceButton.setId("" + i);
            if (game.getSettings().isDiceImageShown())
            {
                String diceImageLocation = "res/" + dices.get(i).getDiceNumber() + ".png";
                setDiceImage(diceButton, currentDice.getDiceNumber());
            }
            else
            {
                diceButton.setText("" + game.getCurrentPlayer().getRemainingDices().get(i).getDiceNumber());
            }
            diceButton.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    moveToDrawnDices(currentDice);
                }
            });
            remainingDiceHBox.getChildren().add(diceButton);
        }
    }

    private void moveToDrawnDices(Dice dice)
    {
        ArrayList<Dice> remainingDices = game.getCurrentPlayer().getRemainingDices();
        remainingDices.remove(dice);
        game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices().add(dice);
        updateUI();
    }

    private void moveToRemainingDices(Dice dice)
    {
        ArrayList<Dice> drawnDices = game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().getDrawnDices();
        if (drawnDices.size() > 0)
        {
            if (drawnDices.contains(dice))
            {
                game.getCurrentPlayer().getCurrentTurn().getCurrentRound().getCurrentRoll().removeDice(dice);
                game.getCurrentPlayer().getRemainingDices().add(dice);
                updateUI();
            }
        }
    }

    private void showInvalidMoveAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(language.getInvalidMove());
        alert.setContentText(language.getThatsNotAllowed());
        alert.show();
    }

    private void initSettingsStage(Stage primaryStage)
    {
        ArrayList<Settings> settingsArrayList = new ArrayList<>();
        Settings gameSettings = game.getSettings();

        globalSettings.setSettingsName(language.getGlobalSettings());

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
            Slider playerSlider = new Slider(2, 6, currentSettings.getTotalPlayers());
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

            //rules

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
            //six dices in a row
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

                    if (isGlobal)
                    {
                        jsonHelper.saveSettings(globalSettings);
                        nextGame(globalSettings);
                    }
                    else
                    {
                        game.setSettings(currentSettings);
                    }
                    settingsStage.close();
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
            buttonHBox.getChildren().addAll(cancelSettingsButton, saveSettingsButton);
            HBox.setMargin(cancelSettingsButton, new Insets(20, 20, 20, 20));
            vBox.getChildren().add(buttonHBox);
            ScrollPane scrollPane = new ScrollPane(vBox);
            scrollPane.setFitToWidth(true);
            scrollPane.setPannable(true);

            titledPane.setContent(scrollPane);
            accordion.getPanes().add(titledPane);

        }
        borderPane.setCenter(accordion);


        settingsStage = new Stage();
        settingsStage.initOwner(primaryStage);
        settingsStage.centerOnScreen();
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    private void nextGame(Settings settings)
    {
        game = new Game(settings);
        updateUI();
        clearScoreListAddPlayers();
    }

    private void initMenu(Stage primaryStage)
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
                    nextGame(globalSettings);
                }
            });
        MenuItem settingsItem = new MenuItem(language.getSettings());
        settingsItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    initSettingsStage(primaryStage);
                }
            });
        MenuItem loadItem = new MenuItem(language.getLoad());
        loadItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Game gameToLoad = jsonHelper.loadGameState();
                    if (gameToLoad != null)
                    {
                        game = gameToLoad;
                        updateUI();
                        rebuildListView();
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
                    jsonHelper.saveGame(game);
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
                        updateUI();
                    }
                }
            });

        MenuItem instaWin = new MenuItem("InstaWin");
        instaWin.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game.getCurrentPlayer().setScore(game.getSettings().getMinScoreRequiredToWin());
                updateUI();
            }
        });

        cheatMenu.getItems().addAll(remainingDicesMenuItem, instaWin);

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
                    alert.setHeaderText(language.getAbout() + " " + language.getZehntausend() + "\n" + language.getVersion() + ": " + versionNumber);
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
                else if (cheatWaiter.length() >= cheatWord.length())
                {
                    cheatWaiter.delete(0, cheatWaiter.length());
                }
                else
                {
                    cheatWaiter.append(event.getText());
                }

            }
        });
        root.setTop(menuBar);
    }

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

    private void applySettings()
    {
        jsonHelper.saveSettings(globalSettings);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("");
        alert.setContentText(language.getApplicationNeedsToBeRestarted());
        alert.show();
    }

    private void clearScoreListAddPlayers()
    {
        observableList.clear();
        addPlayersToListView();
    }

    private void addPlayersToListView()
    {
        HBox hBox = new HBox();
        hBox.setPrefWidth(globalSettings.getWidth() - 20);
        for (Player player : game.getPlayers())
        {
            Label label = new Label(player.getPlayerName());
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(hBox.getPrefWidth() / game.getPlayers().size());
            hBox.getChildren().add(label);
            HBox.setHgrow(label, Priority.ALWAYS);
        }

        observableList.add(hBox);
    }

    private void rebuildListView()
    {
        clearScoreListAddPlayers();

        for (int i = 0; i < game.getLongestTurnArrayList().size() - 1; i++)
        {
            createEmptyLabelsInListView();
        }

        HashMap<Integer, Integer> playerScoreHashMap = new HashMap<>();
        for (int i = 1; i < observableList.size(); i++)
        {
            HBox currentHBox = observableList.get(i);
            for (int j = 0; j < game.getPlayers().size(); j++)
            {
                if (playerScoreHashMap.get(j) == null)
                {
                    playerScoreHashMap.put(j, 0);
                }

                ArrayList<Turn> turnArrayList = game.getPlayers().get(j).getTurnArrayList();
                if (turnArrayList != null && turnArrayList.size() > i)
                {
                    Turn currentTurn = turnArrayList.get(i - 1);
                    if (currentTurn != null)
                    {
                        ArrayList<Round> roundArrayList = currentTurn.getRoundArrayList();
                        if (roundArrayList != null)
                        {
                            playerScoreHashMap.put(j, playerScoreHashMap.get(j) + Scoring.getScoreFromAllDicesInRound(roundArrayList, true, globalSettings));

                            Label label = (Label) currentHBox.getChildren().get(j);
                            if (label != null)
                            {
                                label.setText("" + playerScoreHashMap.get(j));
                                if (playerScoreHashMap.get(j) >= game.getSettings().getMinScoreRequiredToWin())
                                {
                                    label.setText(language.getWonWith() + " " + playerScoreHashMap.get(j));
                                    label.setStyle("-fx-underline: true");
                                }
                                else
                                {
                                    label.setText("" + playerScoreHashMap.get(j));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private void applyScoreToPlayersInListView()
    {
        HBox hBox = observableList.get(observableList.size() - 1);
        Label label = (Label) hBox.getChildren().get(game.getCurrentPlayer().getPlayerNumber());
        if (game.getCurrentPlayer().hasWon())
        {
            label.setText(language.getWonWith() + " " + game.getCurrentPlayer().getScore());
            label.setStyle("-fx-underline: true");
        }
        else
        {
            label.setText("" + game.getCurrentPlayer().getScore());
        }
    }

    //always updates current player score --> after that we switch player
    private void updateScoreOfPlayersInListView()
    {
        if (observableList.size() > 1)
        {
            if (game.getCurrentPlayer().getTurnArrayList().size() >= observableList.size())
            {
                createEmptyLabelsInListView();
                applyScoreToPlayersInListView();
            }
            else
            {
                applyScoreToPlayersInListView();
            }
        }
        else
        {
            createEmptyLabelsInListView();
            applyScoreToPlayersInListView();
        }
    }

    //creates empty labels for each player
    private void createEmptyLabelsInListView()
    {
        HBox hBox = new HBox();
        hBox.setPrefWidth(globalSettings.getWidth() - 20);
        for (int i = 0; i < game.getPlayers().size(); i++)
        {
            Label label = new Label("");
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(hBox.getPrefWidth() / game.getPlayers().size());
            hBox.getChildren().add(label);
            HBox.setHgrow(label, Priority.ALWAYS);
        }
        observableList.add(hBox);
    }

    private void initListView()
    {
        observableList = FXCollections.observableArrayList();
        ListView<HBox> listView = new ListView<>(observableList);
        listView.setMaxHeight(globalSettings.getHeight() / 3);
        root.setBottom(listView);
    }

    private void initUI(Stage primaryStage)
    {
        root = new BorderPane();
        centerVBox = new VBox();
        root.setCenter(centerVBox);
        initMenu(primaryStage);
        remainingDiceHBox = new HBox();
        remainingDiceHBox.setMinHeight(diceButtonSize);
        remainingDiceHBox.setAlignment(Pos.CENTER);
        drawnDiceHBox = new HBox();
        drawnDiceHBox.setMinHeight(diceButtonSize);
        drawnDiceHBox.setAlignment(Pos.CENTER);
        Button rollButton = new Button(language.getRoll());
        rollButton.setPrefWidth(textButtonWidth);
        rollButton.setPrefHeight(textButtonHeight);
        rollButton.setFont(new Font(buttonFontSize));
        rollButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.isValidState(State.ROLL))
                {
                    game.getCurrentPlayer().rollDice();
                    updateUI();
                }
                else
                {
                    showInvalidMoveAlert();
                }
            }
        });
        Button nextButton = new Button(language.getNext());

        nextButton.setPrefHeight(textButtonHeight);
        nextButton.setPrefWidth(textButtonWidth);
        nextButton.setFont(new Font(buttonFontSize));
        nextButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.isValidState(State.NEXT))
                {
                    game.saveScore();
                    updateScoreOfPlayersInListView();
                    game.nextPlayer();
                    updateUI();
                }
                else
                {
                    showInvalidMoveAlert();
                }
            }
        });

        currentPlayerLabel = new Label(language.getCurrentPlayer() + ": 0");
        currentPlayerLabel.setFont(new Font(buttonFontSize));
        scoreLabel = new Label(language.getScore() + ": 0");
        testLabel = new Label("");
        needsToBeConfirmedLabel = new Label();
        needsToBeConfirmedLabel.setFont(new Font(buttonFontSize));
        scoreLabel.setFont(new Font(buttonFontSize));
        scoreInRoundLabel = new Label(language.getScoreInRound() + ": 0");
        scoreInRoundLabel.setFont(new Font(buttonFontSize));
        VBox.setMargin(rollButton, new Insets(-50, 0, 10, 0));
        VBox buttonBox = new VBox(rollButton, nextButton);
        buttonBox.setPadding(new Insets(0, 0, 20, 0));
        buttonBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().add(buttonBox);
        createRemainingDiceButtons();
        centerVBox.getChildren().add(remainingDiceHBox);
        centerVBox.getChildren().add(drawnDiceHBox);
        VBox.setMargin(drawnDiceHBox, new Insets(5, 0, 20, 0));

        centerVBox.getChildren().add(currentPlayerLabel);
        centerVBox.getChildren().add(scoreLabel);
        centerVBox.getChildren().add(scoreInRoundLabel);
        centerVBox.getChildren().add(testLabel);
        centerVBox.getChildren().add(needsToBeConfirmedLabel);

        centerVBox.setAlignment(Pos.CENTER);
        mainScene = new Scene(root, game.getSettings().getWidth(), game.getSettings().getHeight());
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(globalSettings.getMinWidth());
        primaryStage.setMinHeight(globalSettings.getMinHeight());
        primaryStage.setResizable(false);
        initListView();
        primaryStage.show();

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        game = new Game(globalSettings);
        initUI(primaryStage);
        addPlayersToListView();
        //updateUI();
    }
}
