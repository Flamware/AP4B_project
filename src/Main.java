import main.java.com.utmunchkin.Rules;
import main.java.com.utmunchkin.Interface.Interact;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import java.util.List;


/**
 * The main class that contains the main method to start the Munchkin game.
 */
public class Main {

    //rules represents the rules of the game (turn management ...)
    private static Rules rules;

    

    /**
     * The entry point of the program.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        MainMenu.play();
    }

    /**
     * The main menu class responsible for displaying the game menu.
     */
    public static class MainMenu {

        private static ListOfPlayer list;

        /**
         * Displays the main menu and handles button actions.
         */
        public static void play() {
            initializePlayers();
            startGame();
        }

        /**
         * Initializes the list of players for the game.
         */
        private static void initializePlayers() {
            rules = new Rules();
            list = new ListOfPlayer();

            // Get the number of players
            int numberOfPlayers = Interact.showPlayerNumberDialog();

            // Get player names
            List<String> playerNames = Interact.showPlayerNameDialog(numberOfPlayers);

            // Add players to the list
            for (int i = 0; i < playerNames.size(); i++) {
                list.addPlayer(new Player(playerNames.get(i), i + 1));
            }

            rules.setTurn(list);

            System.out.println(list.getPlayers());
        }

        /**
         * Starts the Munchkin game.
         */
        private static void startGame() {
            int firstPlayerIndex = rules.getFirstPlayerIndex();
            Play game = new Play(list, firstPlayerIndex);
            game.gameProcess();
        }
    }
}
