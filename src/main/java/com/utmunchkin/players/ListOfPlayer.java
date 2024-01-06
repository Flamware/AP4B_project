package main.java.com.utmunchkin.players;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import main.java.com.utmunchkin.Constant;

/**
 * The ListOfPlayer class represents a list of players in the game.
 */
public class ListOfPlayer implements Serializable{

    private List<Player> players;

    // Constructor
    public ListOfPlayer() {
        this.players = new ArrayList<>(Constant.INITIAL_CAPACITY);
    }

    /**
     * Gets the size of the list of players.
     *
     * @return The size of the list.
     */
    public int getSize() {
        return players.size();
    }

    /**
     * Gets a player based on their index.
     *
     * @param index The index of the player.
     * @return The player at the specified index.
     */
    public Player getPlayer(int index) {
        validateIndex(index);
        return players.get(index);
    }

    /**
     * Gets a player based on their name.
     *
     * @param name The name of the player.
     * @return The player with the specified name.
     */
    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Adds a player to the list.
     *
     * @param player The player to be added.
     */
    public void addPlayer(Player player) {
        players.add(player);
        System.out.println("Player: " + player.getName() + " List: " + this.players);
    }

    // Methods for resetting aspects of players

    /**
     * Resets all players using a specified action resetter.
     *
     * @param actionResetter The action resetter to be applied to each player.
     */
    private void resetAspect(ActionResetter actionResetter) {
        for (Player player : players) {
            actionResetter.reset(player);
        }
    }

    /**
     * Resets all players.
     */
    public void reset() {
        resetAspect(Player::reset);
    }

    /**
     * Resets the scores of all players.
     */
    public void resetScore() {
        resetAspect(Player::resetScore);
    }

    /**
     * Resets the turns of all players.
     */
    public void resetTurn() {
        resetAspect(Player::resetTurn);
    }

    // Methods for adding score and turns to specific players

    /**
     * Adds a score to a specific player.
     *
     * @param index The index of the player.
     * @param score The score to be added.
     */
    public void addScore(int index, int score) {
        validateIndex(index);
        players.get(index).addScore(score);
    }

    /**
     * Adds a turn to a specific player.
     *
     * @param index The index of the player.
     */
    public void addTurn(int index) {
        validateIndex(index);
        players.get(index).addTurn();
    }

    /**
     * Returns a string representation of all players.
     *
     * @return A string containing information about all players.
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Player player : players) {
            result.append(player.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Gets a copy of the list of players.
     *
     * @return A list containing all players.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(this.players);
    }

    /**
     * Validates an index to ensure it is in the correct range.
     *
     * @param index The index to be validated.
     */
    private void validateIndex(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
    }

    // Functional interface for resetting an action on a player
    private interface ActionResetter {
        void reset(Player player);
    }

    public boolean removePlayerByName(String name) {
        Player playerToRemove = null;

        // Find the player with the specified name
        for (Player player : players) {
            if (player.getName().equals(name)) {
                playerToRemove = player;
                break;
            }
        }

        // Remove the player if found
        if (playerToRemove != null) {
            players.remove(playerToRemove);
            System.out.println("Player removed: " + name);
            return true;
        } else {
            System.out.println("Player not found: " + name);
            return false;
        }
    }
}
