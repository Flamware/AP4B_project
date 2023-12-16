package main.java.com.utmunchkin.cards;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import main.java.com.utmunchkin.cards.CardData.CardInfo;
import main.java.com.utmunchkin.cards.CardData.CardType;
import main.java.com.utmunchkin.cards.CardData.SubType;

public class CardData {

    public enum CardType {
        /*RACE, CLASS, OBJECT, */TREASURE, DUNGEON
    }

    public enum SubType {
        ARMOR, WEAPON, POTION, HEADGEAR, FOOTGEAR, ONE_HAND, TWO_HANDS, SPECIAL, USABLE, CARRIABLE,
        BIG, SMALL, VALUABLE, JEWELRY, CONSUMABLE, ENCHANTED, LIMITED_USE, MAGICAL, RARE, COMMON,
        TRAP, PUZZLE, HAUNT, ENEMY, BOSS, DEADLY, UNDEAD, MYSTERIOUS, CURSED, TREACHEROUS, FEROCIOUS,
        DREADFUL, ANCIENT, VENOMOUS, EVIL, HAUNTED, DARK, MYTHICAL, MONSTER, CURSE
    }

    public enum MonstersType {
        ENEMY, BOSS, DEADLY, UNDEAD, MYSTERIOUS, CURSED, TREACHEROUS, FEROCIOUS,
        DREADFUL, ANCIENT, VENOMOUS, EVIL, HAUNTED, DARK, MYTHICAL, MONSTER
    }

    public enum TreasuresTypes {
        ARMOR, WEAPON, POTION, HEADGEAR, FOOTGEAR, ONE_HAND, TWO_HANDS, SPECIAL, USABLE, CARRIABLE,
        BIG, SMALL, VALUABLE, JEWELRY, CONSUMABLE, ENCHANTED, LIMITED_USE, MAGICAL, RARE, COMMON
    }

    public enum DungeonsTypes {
        TRAP, PUZZLE, HAUNT
    }

    // Map to store card data
    private static Map<String, CardInfo> cardInfoMap = new HashMap<>();
    private static Map<String, CardInfo> availableCards = new HashMap<>();

    static {
        loadCardDataFromFile(new File("src/main/java/com/utmunchkin/cards/cards_data.csv"));
        resetAvailableCards();
    }

    private static void processLine(String line) {
        String[] data = line.split(",");
        if (data.length == 7) {
            try {
                String cardName = data[0].trim();
                String description = data[1].trim();
                int levelBonus = Integer.parseInt(data[2].trim());
                boolean isCursed = Boolean.parseBoolean(data[3].trim());
                CardType cardType = CardType.valueOf(data[4].trim());
                SubType subType = SubType.valueOf(data[5].trim());
                String effectFunctionName = data[6].trim();

                CardInfo cardInfo = new CardInfo(cardName, description, levelBonus, isCursed, cardType, subType, effectFunctionName);
                cardInfoMap.put(cardName, cardInfo);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid data format: " + line);
            }
        } else {
            System.out.println("Invalid data format: " + line);
        }
    }

    public static void loadCardDataFromFile(File file) {
        System.out.println("Loading card data from file: " + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.lines().forEach(CardData::processLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Card data loaded successfully!");
    }


    public static CardInfo getRandomCardInfo() {
        if (availableCards.isEmpty()) {
            // All cards have been picked, reset the available cards
            resetAvailableCards();
        }

        // Pick a random card from the available cards
        String randomCardName = availableCards.keySet().stream().findAny().orElse(null);

        if (randomCardName != null) {
            // Remove the picked card from available cards
            availableCards.remove(randomCardName);

            // Return the picked card
            return cardInfoMap.get(randomCardName);
        } else {
            // No available cards (this should not happen with proper handling)
            return null;
        }
    }
    public static CardInfo getCardInfo(String cardName) {
        return cardInfoMap.get(cardName);
    }

    public static class CardInfo {
        private final String cardName;
        private final String description;
        private final int levelBonus;
        private final boolean isCursed;
        private final CardType cardType;
        private final SubType subType;
        private final String effectFunctionName;

        public CardInfo(String cardName, String description, int levelBonus, boolean isCursed, CardType cardType, SubType subType, String effectFunctionName) {
            this.cardName = cardName;
            this.description = description;
            this.levelBonus = levelBonus;
            this.isCursed = isCursed;
            this.cardType = cardType;
            this.subType = subType;
            this.effectFunctionName = effectFunctionName;
        }

        public String getCardName() {
            return cardName;
        }

        public String getDescription() {
            return description;
        }

        public int getLevelBonus() {
            return levelBonus;
        }

        public boolean isCursed() {
            return isCursed;
        }

        public CardType getCardType() {
            return cardType;
        }

        public SubType getSubType() {
            return subType;
        }

        public String getEffectFunctionName() {
            return effectFunctionName;
        }
        public String getCardInfo() {
            return "CardInfo{" +
                    "cardName='" + cardName + '\'' +
                    ", description='" + description + '\'' +
                    ", levelBonus=" + levelBonus +
                    ", isCursed=" + isCursed +
                    ", cardType=" + cardType +
                    ", subType=" + subType +
                    ", effectFunctionName='" + effectFunctionName + '\'' +
                    '}';
        }
    }
    private static void resetAvailableCards() {
        // Initialize the available cards map with all cards
        availableCards = new HashMap<>(cardInfoMap);
    }
}
