package main.java.com.utmunchkin;

import java.awt.Color;

import javax.swing.JTextField;

/**
 * Constants used in the Munchkin game.
 */
public class Constant {

    // Use constants for the size of the hand
    public static  int MAX_HAND_SIZE = 5;

    // Other constants...
    public static  int INITIAL_CAPACITY = 6;
    public static  int NUMBER_OF_CARDS = 168;
    public static  int MAX_INITIAL_PLAYERS_LIVES = 3;
    public static  int MAX_INITIAL_PLAYERS_MONEY = 10;
    public static  int MAX_INITIAL_PLAYERS_ATTACK = 3;
    public static  double OBJECTS_COST_COEFF = 2;
    public static  int MAX_INITIAL_PLAYERS_DEFENSE = 0;
    public static  int DEFAULT_CURSE_COST = 0;
    public static  int DICE_SIDES = 6;

    public static  int NUMBER_OF_CARDS_FOR_EACH_TYPE = 4;
    public static  double SELL_COST_COEFF = 1.5;


    // Define color variables for Swing elements
    public static Color PLAYER_STATS_BACKGROUND_COLOR = Color.BLACK;
    public static Color PANEL_BORDER_COLOR = Color.WHITE;
    public static Color INFO_TEXT_AREA_BACKGROUND_COLOR = Color.DARK_GRAY;
    public static Color INFO_TEXT_AREA_TEXT_COLOR = Color.WHITE;
    public static Color DECK_PANEL_BACKGROUND_COLOR = Color.BLACK;
    public static Color SHOP_PANEL_BACKGROUND_COLOR = Color.BLACK;
    public static  Color PLAYER_HAND_PANEL_BACKGROUND_COLOR = Color.BLACK;
    public static  Color DRAW_BUTTON_BACKGROUND_COLOR = Color.RED;
    public static  Color DRAW_BUTTON_BACKGROUND_COLOR2 = Color.GREEN;
    public static  Color DRAW_BUTTON_FOREGROUND_COLOR = Color.WHITE;
    public static  Color PLAYER_FRAME_BACKGROUND_COLOR = Color.LIGHT_GRAY;

    public static  Color BORDER_COLOR_1 = Color.BLACK;
    public static  Color BORDER_COLOR_2 = Color.GREEN;

    public static  Color BACKGROUND_COLOR_1= Color.LIGHT_GRAY;
    public static  Color BACKGROUND_COLOR_2 = Color.WHITE;

    public static  Color CARD_BUTTON_BACKGROUND_COLOR = Color.black;
    public static  Color CARD_BUTTON_FOREGROUND_COLOR = Color.white;
    public static  Color CARD_BUTTON_BORDER_COLOR = Color.white;


    public static  Color MAP_GRID_VOID_CELL_COLOR = Color.BLACK;
    public static  Color MAP_PANEL_BACKGROUND_COLOR = Color.DARK_GRAY;


    public static  int INTERACT_WAIT_INTERVAL = 100;

    public static  int SHOP_MAX_ITEMS = 5;

    

    private Constant() {
        // Private constructor to prevent instantiation
    }


    // Add these fields to the Constant class
    public static final JTextField MAX_HAND_SIZE_TEXT_FIELD = new JTextField(Integer.toString(MAX_HAND_SIZE));
    public static final JTextField MAX_INITIAL_PLAYERS_LIVES_TEXT_FIELD = new JTextField(Integer.toString(MAX_INITIAL_PLAYERS_LIVES));
    public static final JTextField MAX_INITIAL_PLAYERS_MONEY_TEXT_FIELD = new JTextField(Integer.toString(MAX_INITIAL_PLAYERS_MONEY));

    public static final boolean FEATURE_1 = false;

    public static final boolean FEATURE_2 = false;

    public static String MUSIC_EXPLORATION = "src/main/java/com/utmunchkin/Interface/music/Legends.wav";
    public static String MUSIC_FIGHT = "src/main/java/com/utmunchkin/Interface/music/Pixel_Reloaded.wav";
    public static String MUSIC_SHOP = "src/main/java/com/utmunchkin/Interface/music/Pulsion_Reloaded.wav";
    public static String MUSIC_CHARITY = "src/main/java/com/utmunchkin/Interface/music/Pixel.wavs";

}
