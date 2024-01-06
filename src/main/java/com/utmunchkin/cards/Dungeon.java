package main.java.com.utmunchkin.cards;

import java.util.ArrayList;
import java.util.List;

import main.java.com.utmunchkin.Constant;

/**
 * The Dungeon class represents the dungeon deck in the game.
 */
public class Dungeon extends Cards {
    private static ArrayList<Card> deckPile;
    private static ArrayList<Card> discardPile;

    /**
     * Constructs a new Dungeon object with a default deck size.
     */
    public Dungeon() {
        deckPile = constructDeck(CardType.DUNGEON);
        discardPile = new ArrayList<>();
        System.out.println("check1bis");
    }

    /**
     * Constructs a new Dungeon object with a specified deck size.
     *
     * @param sizeOfDeck The size of the dungeon deck.
     */
    public Dungeon(int sizeOfDeck) {
        deckPile = constructDeck(sizeOfDeck);
        discardPile = new ArrayList<>();
    }

    /**
     * Gets the dungeon deck pile.
     *
     * @return The dungeon deck pile.
     */
    public static ArrayList<Card> getDeckPile() {
        return deckPile;
    }

    /**
     * Gets the dungeon discard pile.
     *
     * @return The dungeon discard pile.
     */
    public static ArrayList<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * Sets the dungeon deck pile.
     *
     * @param deckP The new deck pile.
     */
    public void setDeckPile(ArrayList<Card> deckP) {
        deckPile = deckP;
    }

    /**
     * Sets the dungeon discard pile.
     *
     * @param discardP The new discard pile.
     */
    public void setDiscardPile(ArrayList<Card> discardP) {
        discardPile = discardP;
    }

    /**
     * Adds a card to the dungeon deck pile.
     *
     * @param card The card to add.
     */
    public void addToDeck(Card card) {
        deckPile.add(card);
    }

    /**
     * Removes a card from the dungeon deck pile at the specified index.
     *
     * @param i The index of the card to remove.
     * @return The removed card, or null if the index is invalid or the deck is empty.
     */
    public Card removeFromDeck(int i) {
        if (!deckPile.isEmpty() && i >= 0 && i < deckPile.size()) {
            return deckPile.remove(i);
        } else {
            return null; // or throw an exception, depending on your design choice
        }
    }

    /**
     * Adds multiple cards to the dungeon deck pile.
     *
     * @param cards The list of cards to add.
     */
    public void addMultipleToDeck(List<Card> cards) {
        deckPile.addAll(cards);
    }

    /**
     * Removes the first card from the dungeon deck pile.
     *
     * @return The removed card, or null if the deck is empty.
     */
    public Card removeFirstFromDeck() {
        if (!deckPile.isEmpty()) {
            return deckPile.remove(0);
        } else {
            return null; // or throw an exception, depending on your design choice
        }
    }

    /**
     * Draws a card from the dungeon deck pile.
     *
     * @return The drawn card, or null if the deck is empty.
     */
    public static Card draw() {
        if (!deckPile.isEmpty()) {
            return deckPile.remove(0);
        } else {
            return null; // or throw an exception, depending on your design choice
        }
    }

    public static void discard(Card card){
        discardPile.add(card);
    }
}
