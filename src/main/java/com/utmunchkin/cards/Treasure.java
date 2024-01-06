package main.java.com.utmunchkin.cards;

import java.util.ArrayList;
import java.util.List;

/**
 * The Treasure class represents the treasure deck in the game.
 */
public class Treasure extends Cards {
    private static ArrayList<Card> deckPile;
    private static ArrayList<Card> discardPile;

    /**
     * Constructs a new Treasure object with a default deck size.
     */
    public Treasure() {
        System.out.println("check1t");
        deckPile = constructDeck(CardType.TREASURE);
        System.out.println("check1te");
        discardPile = new ArrayList<>();
        System.out.println("check1ter");
    }

    /**
     * Constructs a new Treasure object with a specified deck size.
     *
     * @param sizeOfDeck The size of the treasure deck.
     */
    public Treasure(int sizeOfDeck) {
        deckPile = constructDeck(sizeOfDeck);
        discardPile = new ArrayList<>();
    }

    /**
     * Gets the treasure deck pile.
     *
     * @return The treasure deck pile.
     */
    public static ArrayList<Card> getDeckPile() {
        return deckPile;
    }

    /**
     * Gets the treasure discard pile.
     *
     * @return The treasure discard pile.
     */
    public static ArrayList<Card> getDiscardPile() {
        return discardPile;
    }

    /**
     * Sets the treasure deck pile.
     *
     * @param deckP The new deck pile.
     */
    public static void setDeckPile(ArrayList<Card> deckP) {
        deckPile = deckP;
    }

    /**
     * Sets the treasure discard pile.
     *
     * @param discardP The new discard pile.
     */
    public static void setDiscardPile(ArrayList<Card> discardP) {
        discardPile = discardP;
    }

    /**
     * Adds a card to the treasure deck pile.
     *
     * @param card The card to add.
     */
    public void addToDeck(Card card) {
        deckPile.add(card);
    }

    /**
     * Removes a card from the treasure deck pile at the specified index.
     *
     * @param i The index of the card to remove.
     * @return The removed card, or null if the index is invalid or the deck is empty.
     */
    public Card removeFromDeck(int i) {
        if (deckPile.isEmpty() || i < 0 || i >= deckPile.size()) {
            deckPile.addAll(discardPile);
            discardPile.clear();
            shuffleCards(deckPile);
        }
        return deckPile.remove(i);
    }

    /**
     * Adds multiple cards to the treasure deck pile.
     *
     * @param cards The list of cards to add.
     */
    public void addMultipleToDeck(List<Card> cards) {
        deckPile.addAll(cards);
    }

    /**
     * Removes the first card from the treasure deck pile.
     *
     * @return The removed card, or null if the deck is empty.
     */
    public Card removeFirstFromDeck() {
        if (deckPile.isEmpty()) {
            deckPile.addAll(discardPile);
            discardPile.clear();
            shuffleCards(deckPile);
        }
        return deckPile.removeFirst();
    }

    /**
     * Draws a card from the treasure deck pile.
     *
     * @return The drawn card, or null if the deck is empty.
     */
    public static Card draw() {
        if (deckPile.isEmpty()) {
            deckPile.addAll(discardPile);
            discardPile.clear();
        }
        return deckPile.removeFirst();
    }
}
