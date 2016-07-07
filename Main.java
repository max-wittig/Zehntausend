package com.spaghettic0der;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application
{
    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    private final int NUMBER_OF_DICE = 6;
    private Game game;
    private VBox root;
    private HBox remainingDiceHBox;
    private HBox drawnDiceHBox;

    private void updateUI()
    {
        remainingDiceHBox.getChildren().clear();
        drawnDiceHBox.getChildren().clear();
        createRemainingDiceButtons();
        createDrawnDiceButtons();
    }

    private void createDrawnDiceButtons()
    {
        for(int i=0; i < game.getCurrentPlayer().getDrawnDices().size(); i++)
        {

            Button dice = new Button("" +game.getCurrentPlayer().getDrawnDices().get(i).getDiceNumber());
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
        remainingDices.remove(diceId);
        game.getCurrentPlayer().getDrawnDices().add(dice);
        updateUI();
    }

    private void moveToRemainingDices(int diceId)
    {
        ArrayList<Dice> drawnDices = game.getCurrentPlayer().getDrawnDices();
        Dice dice = drawnDices.get(diceId);
        drawnDices.remove(diceId);
        game.getCurrentPlayer().getRemainingDices().add(dice);
        updateUI();
    }

    private void initUI(Stage primaryStage)
    {
        root = new VBox();
        remainingDiceHBox = new HBox();
        drawnDiceHBox = new HBox();
        Button rollButton = new Button("ROLL");
        rollButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                game.getCurrentPlayer().rollDice();
                updateUI();
            }
        });
        Button nextButton = new Button("NEXT");
        root.getChildren().add(rollButton);
        root.getChildren().add(nextButton);

        createRemainingDiceButtons();

        root.getChildren().add(remainingDiceHBox);
        root.getChildren().add(drawnDiceHBox);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        game = new Game();
        initUI(primaryStage);
    }
}
