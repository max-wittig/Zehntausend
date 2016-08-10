package com.spaghettic0der.zehntausend;

import com.spaghettic0der.zehntausend.Extras.Debug;
import com.spaghettic0der.zehntausend.Extras.JsonHelper;
import com.spaghettic0der.zehntausend.Extras.Language;
import com.spaghettic0der.zehntausend.GameLogic.*;
import com.spaghettic0der.zehntausend.UI.CustomListView;
import com.spaghettic0der.zehntausend.UI.MenuUI;
import com.spaghettic0der.zehntausend.UI.SettingsUI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application
{
    public static final String VERSION_NUMBER = "1.0.0";
    public static Language language;
    private static boolean selfTrigger = false;
    private final int textButtonWidth = 150;
    private final int textButtonHeight = 50;
    private final int diceButtonSize = 50;
    private final int buttonFontSize = 20;
    private Game game;
    private BorderPane root;
    private HBox remainingDiceHBox;
    private HBox drawnDiceHBox;
    private Label currentPlayerLabel;
    private Label scoreLabel;
    private Label scoreInRoundLabel;
    private Scene mainScene;
    private JsonHelper jsonHelper;
    private Settings globalSettings;
    private VBox centerVBox;
    private ObservableList<HBox> observableList;
    private Label testLabel;
    private Label needsToBeConfirmedLabel;
    private ArrayList<Image> imageArrayList;
    private Main main;
    private CustomListView<HBox> listView;

    public Main()
    {
        main = this;
        imageArrayList = new ArrayList<>();

        jsonHelper = new JsonHelper();
        globalSettings = jsonHelper.loadSettings();
        if (globalSettings == null)
            globalSettings = new Settings();

        //loads language from json in jar package files
        language = jsonHelper.loadLanguage("language_" + globalSettings.getSelectedLanguage());
        if (language == null)
            language = new Language();

        initDiceImages();
    }

    public static void main(String[] args) throws Exception
    {
        Application.launch(args);
    }

    /**
     * called by game, when player won
     *
     * @param playerName
     * @param headerText
     * @param contentText
     */
    public static void showWinAlert(String playerName, String headerText, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText + " " + playerName);
        alert.show();
    }

    /**
     * shows game over dialog with
     * 1. place, 2. place and 3. place
     * @param headerText
     * @param contentText
     */
    public static void showGameOverDialog(String headerText, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.show();
    }

    /**
     * inits the dice images, so that they don't need to be loaded every time their accessed
     * dice images shown settings can be ajusted
     */
    private void initDiceImages()
    {
        for (int i = 1; i <= 6; i++)
        {
            Image image = new Image(getClass().getResourceAsStream("res/" + i + ".png"));
            imageArrayList.add(image);
        }
    }

    /**
     * is called everytime a player pressed roll or next
     * refreshes labels and recreated whole UI
     */
    public void updateUI()
    {
        remainingDiceHBox.getChildren().clear();
        drawnDiceHBox.getChildren().clear();
        createRemainingDiceButtons();
        createDrawnDiceButtons();
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerLabel.setText(language.getCurrentPlayer() + ": " + (game.getCurrentPlayer().getPlayerNumber() + 1));
        scoreLabel.setText(language.getScore() + ": " + (Scoring.getScoreFromAllDices(game.getCurrentPlayer().getTurnArrayList(), game.getSettings(), true, true, game.getCurrentPlayer().getCurrentTurn().getCurrentRound())));
        scoreInRoundLabel.setText(language.getScoreInRound() + ": " + Scoring.getScoreFromAllDicesInRound(game.getCurrentPlayer().getCurrentTurn().getRoundArrayList(), false, game.getSettings()));
        if (currentPlayer.getCurrentTurn().getCurrentRound().getCurrentRoll()
                .needsConfirmation(game.getSettings().getTotalDiceNumber())
                && !currentPlayer.getCurrentTurn().isValid(game.getSettings())
                && game.isValidState(State.ROLL))
        {
            needsToBeConfirmedLabel.setText(language.getScoreNeedsToBeConfirmed());
        }
        else
        {
            needsToBeConfirmedLabel.setText("");
        }
        listView.setHScrollBarEnabled(false);
        Debug.write(Debug.getClassName(this) + " - " + Debug.getLineNumber() + " UI updated");
    }

    /**
     * gets drawn dice buttons from current player and draws them on the screen again
     * is called everytime something in the UI changes
     */
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
                    game.moveToRemainingDices(currentDice);
                    updateUI();
                }
            });
            drawnDiceHBox.getChildren().add(diceButton);
        }
    }

    /**
     * corresponds to show dice images settings
     * is called by both createRemaining and createDrawnDices, if setting is enabled
     * @param diceButton
     * @param diceNumber
     */
    private void setDiceImage(Button diceButton, int diceNumber)
    {
        ImageView imageView = new ImageView(imageArrayList.get(diceNumber - 1));
        imageView.setFitWidth(diceButtonSize - 20);
        imageView.setFitHeight(diceButtonSize - 20);
        diceButton.setGraphic(imageView);
    }

    /**
     * gets RemainingDiceButtons from current player and draws them on the screen again
     * is called everytime something in the UI changes
     */
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
                    game.moveToDrawnDices(currentDice);
                    updateUI();
                }
            });
            remainingDiceHBox.getChildren().add(diceButton);
        }
    }


    /**
     * if player pressed the roll button and move is not possible
     * alert is shown to inform the user of that
     */
    private void showInvalidMoveAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(language.getInvalidMove());
        alert.setContentText(language.getThatsNotAllowed());
        alert.show();
    }

    /**
     * is called everytime the settings stage is opened via the menuBar
     * @param primaryStage
     */
    public void initSettingsStage(Stage primaryStage)
    {
        new SettingsUI(game, globalSettings, language, this, jsonHelper, primaryStage).show();
    }

    public void nextGame(Settings settings)
    {
        Debug.write(Debug.getClassName(this) + " - " + Debug.getLineNumber() + " Next game starting...");
        game = new Game(settings, this);
        updateUI();
        clearScoreListAddPlayers();
    }

    /**
     * inits menuBar on top
     * called by initUI();
     * @param primaryStage
     */
    private void initMenu(Stage primaryStage)
    {
        new MenuUI(game, globalSettings, language, main, jsonHelper, primaryStage, root).show();
    }

    /**
     * clears score list and re-created players
     * is called when game is loaded from save or when new game is pressed
     */
    private void clearScoreListAddPlayers()
    {
        observableList.clear();
        addPlayersToListView();
    }

    /**
     * called by addPlayersToListView
     * readdeds all the player Labels in the listview, which are inside a HBox
     */
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

    /**
     * recreated the whole listView, incase the player loads a saved game
     */
    public void rebuildListView()
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
                            if (currentTurn.isValid(game.getSettings()))
                                playerScoreHashMap.put(j, playerScoreHashMap.get(j) + Scoring.getScoreFromAllDicesInRound(roundArrayList, true, globalSettings));

                            Label label = (Label) currentHBox.getChildren().get(j);
                            if (label != null)
                            {
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
        listView.setHScrollBarEnabled(false);
    }

    /**
     * is called by updateScoreOfPlayersInListView
     * gets the current Hbox and sets the label of the player with it's score
     */
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

    /**
     * if there a enough HBoxes in the observable list it just applys the score of the player
     * otherwise it calles createEmptyLabelsInListView() and applies the score after that
     */
    public void updateScoreOfPlayersInListView()
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

    /**
     * creates a new HBox and puts empty labels in for each player
     */
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

    /**
     * is called by initUI and initialized the listView
     * calls addPlayersToListView
     */
    private void initListView()
    {
        observableList = FXCollections.observableArrayList();
        listView = new CustomListView<>(observableList);
        listView.setSelectable(false);
        listView.setHScrollBarEnabled(false);
        listView.setAutoScrollEnabled(true);
        listView.setMaxHeight(globalSettings.getHeight() / 3);
        root.setBottom(listView);
        addPlayersToListView();
    }

    /**
     * builds whole UI and calls subfunctions e.g. initMenu, initListView
     * @param primaryStage stage
     */
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

        currentPlayerLabel = new Label(language.getCurrentPlayer() + ": 1");
        currentPlayerLabel.setFont(new Font(buttonFontSize));
        scoreLabel = new Label(language.getScore() + ": 0");
        scoreLabel.setVisible(false);
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

    public Label getScoreLabel()
    {
        return scoreLabel;
    }

    /**
     * starts the game
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        game = new Game(globalSettings, this);
        initUI(primaryStage);
    }
}
