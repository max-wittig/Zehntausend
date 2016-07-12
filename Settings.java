package com.spaghettic0der;


public class Settings
{
    public static int WIDTH = 640;
    public static int HEIGHT = 480;

    public static int TOTAL_DICE_NUMBER = 6;
    public static int TOTAL_PLAYERS = 3;

    public static boolean STREET_ENABLED = true;
    public static int SCORE_STREET = 1500;

    public static int MIN_SCORE_REQUIRED_TO_SAVE_IN_ROUND = 300;
    public static int MIN_SCORE_REQUIRED_TO_WIN = 5000;


    public static boolean THREE_X_TWO_ENABLED = true; //2x3 -> 2,2,4,4,6,6 --> 1000 points
    public static int SCORE_THREE_X_TWO = 1000;

    public static boolean SIX_DICES_IN_A_ROW_ENABLED = true;
    public static int SCORE_SIX_DICES_IN_A_ROW = MIN_SCORE_REQUIRED_TO_WIN; //can be ajusted
}
