package main.java.com.utmunchkin.players;

import java.util.ArrayList;
import java.util.List;

import main.java.com.utmunchkin.Constant;

public class ListOfPlayer extends Constant {
    private List<Player> players;

    // Constructeur
    public ListOfPlayer() {
        this.players = new ArrayList<>(INITIAL_CAPACITY);
    }

    // Obtient la taille de la liste des joueurs
    public int getSize() {
        return players.size();
    }

    // Obtient un joueur à partir de son index
    public Player getPlayer(int index) {
        validateIndex(index);
        return players.get(index);
    }

    // Obtient un joueur à partir de son nom
    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    // Ajoute un joueur à la liste
    public void addPlayer(Player player) {
        players.add(player);
    }

    // Réinitialise tous les joueurs
    private void resetAspect(ActionResetter actionResetter) {
        for (Player player : players) {
            actionResetter.reset(player);
        }
    }

    // Réinitialise tous les joueurs
    public void reset() {
        resetAspect(Player::reset);
    }

    // Réinitialise tous les scores des joueurs
    public void resetScore() {
        resetAspect(Player::resetScore);
    }

    // Réinitialise tous les tours des joueurs
    public void resetTurn() {
        resetAspect(Player::resetTurn);
    }

    // Ajoute un score à un joueur spécifique
    public void addScore(int index, int score) {
        validateIndex(index);
        players.get(index).addScore(score);
    }

    // Ajoute un tour à un joueur spécifique
    public void addTurn(int index) {
        validateIndex(index);
        players.get(index).addTurn();
    }

    // Retourne une représentation sous forme de chaîne de tous les joueurs
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Player player : players) {
            result.append(player.toString()).append("\n");
        }
        return result.toString();
    }

    // Obtient une copie de la liste des joueurs
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    // Valide un index pour s'assurer qu'il est dans la plage correcte
    private void validateIndex(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
    }

    // Interface fonctionnelle pour réinitialiser une action sur un joueur
    private interface ActionResetter {
        void reset(Player player);
    }
}
