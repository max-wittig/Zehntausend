package com.spaghettic0der;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class Main extends Application
{
    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    private final int NUMBER_OF_DICE = 6;
    private Game game;
    private FlowPane root;

    private void updateUI()
    {
        for(int i=0; i < game.getCurrentPlayer().getRemainingDices().size(); i++)
        {
            ((Button)root.lookup(String.valueOf(game.getCurrentPlayer().getRemainingDices().get(i).getDiceID()))).setText("Test");
        }
    }

    private void initUI(Stage primaryStage)
    {
        root = new FlowPane();

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

        for(int i=0; i < NUMBER_OF_DICE; i++)
        {
            Button dice = new Button(String.valueOf(i));
            dice.setId("Dice"+i);
            root.getChildren().add(dice);
        }

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        initUI(primaryStage);

        game = new Game();
    }
}
