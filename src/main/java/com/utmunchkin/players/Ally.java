package main.java.com.utmunchkin.players;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Interface.Interact;
import main.java.com.utmunchkin.Interface.MapPanel;
import main.java.com.utmunchkin.gameplay.Play;

/**
 * The Ally class represents allies and their interactions in the game.
 */
public class Ally {

    private static boolean isHelpRequested = false;
    private static final Object helpRequestedLock = new Object();
    private static MapPanel map;
    private ListOfPlayer listPlayers;
    private static int alliesForce;
    private static List<Player> listOfAllies = new ArrayList<>();

    /**
     * Sets the flag indicating whether help is requested.
     *
     * @param b The value to set for help requested.
     */
    public static void setHelpRequested(boolean b) {
        isHelpRequested = b;
    }

    /**
     * Gets the status of help requested flag.
     *
     * @return True if help is requested, false otherwise.
     */
    public static boolean getHelpRequested() {
        return isHelpRequested;
    }

    /**
     * Constructs an Ally object with the specified list of players.
     *
     * @param listOfPlayer The list of players.
     */
    public Ally(ListOfPlayer listOfPlayer) {
        listOfAllies = new ArrayList<Player>();
        listPlayers = listOfPlayer;
    }

    /**
     * Waits for help by synchronizing on the helpRequestedLock object.
     */
    public static void waitForHelp() {
        synchronized (helpRequestedLock) {
            try {
                helpRequestedLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Requests help from another player.
     *
     * @param helpingPlayer The player offering help.
     */
    public static void requestHelp(Player helpingPlayer) {
        map = Play.getMap();
        Player requestingPlayer = Play.getCurrentPlayer();

        if (!listOfAllies.contains(helpingPlayer) && requestingPlayer != helpingPlayer) {

            listOfAllies.add(helpingPlayer);
            System.out.println(map + "Allies: " + listOfAllies + " Ally added: " + helpingPlayer.getName());

            int moneyToPay = Constant.PRICE_FOR_REQUESTING_HELP;

            // Check if help has already been requested and charge 5 money per player from the second one
            /*if (Ally.getHelpRequested()) {
                moneyToPay = 5;
            }*/

            // Check if the player has enough money to request help
            if (requestingPlayer.getMoney() >= moneyToPay) {
                // Display a message indicating the request for help
                Interact.showMessageDialog(requestingPlayer.getName() + " requests help from " + helpingPlayer.getName());

                if (Interact.yesOrNoDialog("Do you want to help " + requestingPlayer.getName() + ", " + helpingPlayer.getName()).equals("Yes")) {
                    // Update the money of the player requesting help
                    requestingPlayer.addMoney(-moneyToPay);

                    // Update the money of players helping (except the first one)
                    if (moneyToPay > 0) {
                        helpingPlayer.addMoney(moneyToPay);
                    }

                    map.performGameAction('A', 0, 4);
                    map.setCellColor(0, 4, Color.BLUE);
                } else {
                    listOfAllies.remove(helpingPlayer);
                }

                Board.updatePlayerStatsPanel(requestingPlayer);
                String answer = Interact.yesOrNoDialog("Do you want to ask another player for help (for " + moneyToPay + " money)?");

                // Set the help requested flag
                Ally.setHelpRequested(true);

                if (!answer.equals("Yes")) {
                    // Apply help
                    applyHelp(requestingPlayer);

                    // Notify the waiting thread
                    synchronized (helpRequestedLock) {
                        helpRequestedLock.notify();
                    }
                }
            } else {
                // Display a message if the player doesn't have enough money to request help
                Interact.showMessageDialog(requestingPlayer.getName() + " doesn't have enough money to request help.");

                // Apply help
                applyHelp(requestingPlayer);

                // Notify the waiting thread
                synchronized (helpRequestedLock) {
                    helpRequestedLock.notify();
                }
            }

        } else if (requestingPlayer == helpingPlayer) {
            if (Interact.yesOrNoDialog("Go alone?").equals("Yes")) {
                // Set the help requested flag
                Ally.setHelpRequested(true);

                // Notify the waiting thread
                synchronized (helpRequestedLock) {
                    helpRequestedLock.notify();
                }
            }

        } else {
            Interact.showMessageDialog("You have already requested help from this player!");
            if (Interact.yesOrNoDialog("Open the dungeon door?").equals("Yes")) {
                Ally.setHelpRequested(true);

                // Notify the waiting thread
                synchronized (helpRequestedLock) {
                    helpRequestedLock.notify();
                }
            }
        }

    }

    /**
     * Applies the help provided by allies to the requesting player.
     *
     * @param requestingPlayer The player requesting help.
     */
    private static void applyHelp(Player requestingPlayer) {

        for (Player p : listOfAllies) {
            requestingPlayer.setAttackForce(requestingPlayer.getAttackForce() + p.getAttackForce());
            alliesForce += p.getAttackForce();
        }
    }

    /**
     * Resets the list of allies.
     */
    public static void resetListOfAllies() {
        listOfAllies.clear();
    }

    /**
     * Gets the total force of all allies.
     *
     * @return The total force of all allies.
     */
    public static int getAlliesForce() {
        return alliesForce;
    }

    /**
     * Sets the total force of all allies.
     *
     * @param value The value to set for the total force of all allies.
     */
    public static void setAlliesForce(int value) {
        alliesForce = value;
    }

    /**
     * Cleans up the map after help has been provided.
     */
    public static void cleanMap() {
        for (int i = 0; i < 5; i++) {
            map.performGameAction('Z', i, 4);
            map.setCellColor(i, 4, MapPanel.VOID_CELL_COLOR);
        }
    }
}
