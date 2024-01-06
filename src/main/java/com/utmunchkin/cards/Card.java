package main.java.com.utmunchkin.cards;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.players.Player;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Random;

import static main.java.com.utmunchkin.cards.Card.Monster.getMonsterCombatStrengthMonster;

/**
 * The Card class represents a generic game card.
 */
public class Card extends CardData {

    protected final CardData.CardInfo cardInfo;

    /**
     * Constructs a Card object with the given card name.
     *
     * @param cardName The name of the card.
     */
    public Card(String cardName) {
        String relativePath = "src/main/java/com/utmunchkin/cards/cards_data.csv";
        CardData.loadCardDataFromFile(new File(relativePath));
        this.cardInfo = CardData.getCardInfo(cardName);
    }

    /**
     * Default constructor that creates a Card with random information.
     */
    public Card() {
        this.cardInfo = CardData.getRandomCardInfo();
    }

    /**
     * Checks if the card is a monster card.
     *
     * @return True if the card is a monster card, false otherwise.
     */
    public boolean isMonster() {
        return this instanceof Monster;
    }

    /**
     * Checks if the card is a curse card.
     *
     * @return True if the card is a curse card, false otherwise.
     */
    public boolean isCurse() {
        return this instanceof Curse;
    }

    /**
     * Applies the effect of the card to a player.
     *
     * @param player The player to apply the card effect to.
     */
    public void applyCardEffect(Player player) {
        System.out.println("Applying card effect: " + cardInfo.getCardName());
        String effectFunctionName = cardInfo.getEffectFunctionName();

        try {
            Class<?> effectClass = Class.forName("main.java.com.utmunchkin.cards.CardEffects");
            Method effectMethod = effectClass.getMethod(effectFunctionName, Player.class);
            effectMethod.invoke(null, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the name of the card.
     *
     * @return The name of the card.
     */
    public String getCardName() {
        return cardInfo.getCardName();
    }

    /**
     * Gets the information of the card.
     *
     * @return The information of the card.
     */
    public CardData.CardInfo getInfo() {
        return cardInfo;
    }

    /**
     * Converts the card information to a string.
     *
     * @return A string representation of the card information.
     */
    public String toString() {
        return cardInfo.getCardInfo();
    }

    /**
     * Gets the combat strength of the monster card.
     *
     * @param card The monster card.
     * @return The combat strength of the monster card.
     */
    public int getMonsterCombatStrength(Card card) {
        new Monster(card);
        return getMonsterCombatStrengthMonster();
    }

    /**
     * Gets the levels of the monster card.
     *
     * @return The levels of the monster card.
     */
    public int getLevels() {
        return Monster.getLevelsMonster();
    }

    /**
     * Gets the treasures of the monster card.
     *
     * @return The treasures of the monster card.
     */
    public int getTreasures() {
        return Monster.getTreasuresMonster();
    }

    /**
     * The Monster class represents a monster card.
     */
    public static class Monster extends Card {
        private static int monsterCombatStrength;
        private static int levels;
        private static int treasures;

        /**
         * Constructs a Monster object using a card.
         *
         * @param card The card containing monster information.
         */
        public Monster(Card card) {
            super();
            this.setLevel(card.cardInfo.getLevelBonus());
            this.setMonsterCombatStrength(card.cardInfo.getLevelBonus());
            this.setTreasure((int) (getMonsterCombatStrengthMonster() * Constant.TRASURES_COEFF));
        }

        /**
         * Constructs a Monster object with specified attributes.
         *
         * @param combatStrength The combat strength of the monster.
         * @param levels         The levels of the monster.
         * @param treasures      The treasures of the monster.
         */
        public Monster(int combatStrength, int levels, int treasures) {
            super();
            this.setLevel(combatStrength);
            this.setLevel(levels);
            this.setTreasure(treasures);
        }

        private void setTreasure(int treas) {
            treasures = treas;
        }

        private void setLevel(int lvl) {
            levels = lvl;
        }

        /**
         * Sets the combat strength of the monster.
         *
         * @param combatStrength The combat strength to set.
         */
        public void setMonsterCombatStrength(int combatStrength) {
            monsterCombatStrength = combatStrength;
        }

        /**
         * Gets the combat strength of the monster.
         *
         * @return The combat strength of the monster.
         */
        public static int getMonsterCombatStrengthMonster() {
            return monsterCombatStrength;
        }

        /**
         * Gets the levels of the monster.
         *
         * @return The levels of the monster.
         */
        public static int getLevelsMonster() {
            return levels;
        }

        /**
         * Gets the treasures of the monster.
         *
         * @return The treasures of the monster.
         */
        public static int getTreasuresMonster() {
            return treasures;
        }
    }

    /**
     * The Curse class represents a curse card.
     */
    public static class Curse extends Card {
        int cost;

        /**
         * Constructs a Curse object with the default cost.
         */
        public Curse() {
            this.cost = Constant.DEFAULT_CURSE_COST;
        }

        /**
         * Applies a recurrent curse to a player.
         *
         * @param player The player to apply the curse to.
         */
        public void reccurentCurse(Player player) {
            Random random = new Random();
            int curseType = random.nextInt(2);
            int curseValue = random.nextInt(5) + 1;

            if (curseType == 0) {
                player.addLives(-curseValue);
                System.out.println("The curse has affected the player, making them lose " + curseValue + " lives.");
            } else {
                player.addMoney(-curseValue);
                System.out.println("The curse has affected the player, making them lose " + curseValue + " units of money.");
            }
        }

        /**
         * Applies a curse immediately.
         */
        public void sufferCurseNow() {
            // Implementation for immediate curse effect
        }
    }
}
