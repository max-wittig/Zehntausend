package com.spaghettic0der;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application
{
    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    private Game game;
    private BorderPane root;
    private HBox remainingDiceHBox;
    private HBox drawnDiceHBox;
    private Label currentPlayerLabel;
    private Label scoreLabel;
    private Label scoreInRoundLabel;

    public static void main(String[] args)
    {
        Application.launch(args);
    }

    private void updateUI()
    {
        remainingDiceHBox.getChildren().clear();
        drawnDiceHBox.getChildren().clear();
        createRemainingDiceButtons();
        createDrawnDiceButtons();
        currentPlayerLabel.setText("Current Player: " + (game.getCurrentPlayer().getPlayerNumber() + 1));
        scoreLabel.setText("Score: " + (game.getCurrentPlayer().getScore() + Scoring.getScoreFromAllDicesInRound(game.getCurrentPlayer().getRoundArrayList())));
        scoreInRoundLabel.setText("Score in Round: " + Scoring.getScoreFromAllDicesInRound(game.getCurrentPlayer().getRoundArrayList()));
    }

    private void createDrawnDiceButtons()
    {
        for (int i = 0; i < game.getCurrentPlayer().getDrawnDicesFromLastRound().size(); i++)
        {
            Button dice = new Button("" + game.getCurrentPlayer().getDrawnDicesFromLastRound().get(i).getDiceNumber());
            dice.setId(""+i);
            dice.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    moveToRemainingDices(Integer.parseInt(dice.getId()));
                }
            });
            drawnDiceHBox.getChildren().add(dice);
        }
    }

    private void createRemainingDiceButtons()
    {
        for(int i=0; i < game.getCurrentPlayer().getRemainingDices().size(); i++)
        {
            Button dice = new Button("" +game.getCurrentPlayer().getRemainingDices().get(i).getDiceNumber());
            dice.setId(""+i);
            dice.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    moveToDrawnDices(Integer.parseInt(dice.getId()));
                }
            });
            remainingDiceHBox.getChildren().add(dice);
        }
    }

    private void moveToDrawnDices(int diceId)
    {
        ArrayList<Dice> remainingDices = game.getCurrentPlayer().getRemainingDices();
        Dice dice = remainingDices.get(diceId);
        game.increaseNumberDrawnSinceLastRoll();
        remainingDices.remove(diceId);
        game.getCurrentPlayer().getLastDrawnDices().add(dice);
        updateUI();
    }

    private void moveToRemainingDices(int diceId)
    {
        ArrayList<Dice> drawnDices = game.getCurrentPlayer().getAllDrawnDices();
        Dice dice = drawnDices.get(diceId);
        if(dice.canDiceBeDrawnThisRound())
        {
            game.decreaseNumberDrawnSinceLastRoll();
            game.getCurrentPlayer().removeLastDrawnDiceWithNumber(dice.getDiceNumber());
            game.getCurrentPlayer().getRemainingDices().add(dice);
            updateUI();
        }
    }

    private void showInvalidMoveAlert()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Invalid Move!");
        alert.setContentText("That's not allowed");
        alert.show();
    }

    private void initMenu(Stage primaryStage)
    {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu gameMenu = new Menu("Game");

        MenuItem newGameItem = new MenuItem("New");
        newGameItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game = new Game();
                updateUI();
            }
        });
        MenuItem settingsItem = new MenuItem("Settings");
        MenuItem loadItem = new MenuItem("Load");
        MenuItem saveItem = new MenuItem("Save");
        gameMenu.getItems().addAll(newGameItem, settingsItem, loadItem, saveItem);

        Menu aboutMenu = new Menu("About");
        MenuItem infoItem = new MenuItem("Zehntausend");
        infoItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("About");
                alert.setContentText("Made by spaghettic0der in 2016\nMade possible with the help of Deadlocker");
                alert.show();
            }
        });
        aboutMenu.getItems().add(infoItem);

        menuBar.getMenus().addAll(gameMenu, aboutMenu);
        root.setTop(menuBar);
    }

    private void initUI(Stage primaryStage)
    {
        root = new BorderPane();
        VBox vBox = new VBox();
        root.setCenter(vBox);
        initMenu(primaryStage);
        remainingDiceHBox = new HBox();
        remainingDiceHBox.setAlignment(Pos.CENTER);
        drawnDiceHBox = new HBox();
        drawnDiceHBox.setAlignment(Pos.CENTER);
        Button rollButton = new Button("ROLL");
        rollButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.isValidState(State.ROLL))
                {
                    game.resetNumberOfDicesDrawnSinceLastRoll();
                    game.getCurrentPlayer().rollDice();
                    updateUI();
                }
                else
                {
                    showInvalidMoveAlert();
                }
            }
        });
        Button nextButton = new Button("NEXT");
        nextButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                if (game.isValidState(State.NEXT))
                {
                    game.nextPlayer();
                    updateUI();
                }
                else
                {
                    showInvalidMoveAlert();
                }
            }
        });

        currentPlayerLabel = new Label("Current Player: 0");
        scoreLabel = new Label("Score: 0");
        scoreInRoundLabel = new Label("Score in Round: 0");
        vBox.getChildren().add(rollButton);
        vBox.getChildren().add(nextButton);
        createRemainingDiceButtons();
        vBox.getChildren().add(remainingDiceHBox);
        vBox.getChildren().add(drawnDiceHBox);
        vBox.getChildren().add(currentPlayerLabel);
        vBox.getChildren().add(scoreLabel);
        vBox.getChildren().add(scoreInRoundLabel);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        game = new Game();
        initUI(primaryStage);
        game.getCurrentPlayer().rollDice();
        updateUI();
    }
}
