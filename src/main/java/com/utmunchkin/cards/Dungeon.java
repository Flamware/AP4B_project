package main.java.com.utmunchkin.cards;

import java.util.ArrayList;
import java.util.List;

import main.java.com.utmunchkin.Constant;

public class Dungeon extends Cards {
    private ArrayList<Card> deckPile;
    private ArrayList<Card> discardPile;

    public Dungeon() {
        deckPile = constructDeck(Constant.NUMBER_OF_CARDS / 2 , CardType.DUNGEON);
        discardPile = new ArrayList<>();
    }

    public Dungeon(int sizeOfDeck) {
        deckPile = constructDeck(sizeOfDeck);
        discardPile = new ArrayList<>();
    }

    // Getters
    public ArrayList<Card> getDeckPile() {
        return deckPile;
    }

    public ArrayList<Card> getDiscardPile() {
        return discardPile;
    }

    // Setters
    public void setDeckPile(ArrayList<Card> deckPile) {
        this.deckPile = deckPile;
    }

    public void setDiscardPile(ArrayList<Card> discardPile) {
        this.discardPile = discardPile;
    }

    public void addToDeck(Card card) {
        deckPile.add(card);
    }

    public Card removeFromDeck(int i) {
        if (!deckPile.isEmpty()) {
            return deckPile.remove(i);
        } else {
            return null; // or throw an exception, depending on your design choice
        }
    }

    public void addMultipleToDeck(List<Card> cards) {
        deckPile.addAll(cards);
    }

    public Card removeFirstFromDeck() {
        if (!deckPile.isEmpty()) {
            return deckPile.remove(0);
        } else {
            return null; // or throw an exception, depending on your design choice
        }
    }

    public static class Monster extends Dungeon {
        private int monsterCombatStrength;
        private int levels;
        private int treasures;
    

        public Monster() {
            super();
            // Set other attributes if needed
            this.setLevel(this.cardInfo.getLevelBonus());
            this.setMonsterCombatStrength(this.cardInfo.getLevelBonus());
            this.setTreasure(this.getMonsterCombatStrength() % 2);
        }

        public Monster(int combatStrength, int levels, int treasures) {
            super();
            // Set other attributes if needed
            this.setLevel(combatStrength);
            this.setLevel(levels);
            this.setTreasure(treasures);
        }
    
        private void setTreasure(int treasures) {
            this.treasures = treasures;
        }

        private void setLevel(int levels) {
            this.levels = levels;
        }

        public int getMonsterCombatStrength() {
            return monsterCombatStrength;
        }
    
        public void setMonsterCombatStrength(int combatStrength) {
            this.monsterCombatStrength = combatStrength;
        }

        public int getLevels() {
            return this.levels;
        }

        public int getTreasures() {
            return this.treasures;
        }
    }
    

    public static class Curse extends Dungeon{
        public Curse() {

        }  
    }
}
