package main.java.com.utmunchkin.utils;

import java.io.FileWriter;
import java.io.IOException;

public class GenerateCSV {

    private static final String[] TREASURE_CARD_NAMES = {
            "Sword of Power", "Dragon's Breath Potion", "Shield of Protection", "Gem of Fortune", "Ring of Wealth",
            "Golden Crown", "Enchanted Amulet", "Mystic Scroll", "Elixir of Luck", "Diamond Dagger",
            "Crystal Shield", "Jeweled Crown", "Ancient Relic", "Emerald Ring", "Lucky Charm", "Divine Pendant",
            "Silver Saber", "Pearl Necklace", "Blessed Gauntlet", "Ruby Amulet",
            // Add more treasure card names
    };

    private static final String[] TREASURE_DESCRIPTIONS = {
            "A legendary sword with immense power", "A potion that breathes fire", "A sturdy shield for protection",
            "A magical gem that brings fortune", "A ring that enhances wealth",
            "A regal golden crown with mystical properties", "A magical amulet with enchanting powers",
            "A mysterious scroll with powerful spells", "An elixir that boosts luck", "A dagger with a diamond edge",
            "A shield made of crystal for strong defense", "A crown adorned with jewels", "An ancient mystical relic",
            "A ring embedded with emerald", "A charm that attracts good luck", "A pendant with divine aura",
            "A silver saber known for its strength", "A necklace adorned with pearls", "A gauntlet with a blessed touch",
            "An amulet with a ruby",
            // Add more treasure descriptions
    };

    private static final String[] TREASURE_TYPES = {"TREASURE"};
    private static final String[] TREASURE_SUBTYPES = {
            "WEAPON", "POTION", "ARMOR", "VALUABLE", "ACCESSORY",
            "HEADGEAR", "FOOTGEAR", "ONE_HAND", "TWO_HANDS", "SPECIAL",
            "USABLE", "CARRIABLE", "BIG", "SMALL", "JEWELRY",
            "ENCHANTED", "LIMITED_USE", "MAGICAL", "RARE", "COMMON",
            // Add more treasure subtypes
    };

    private static final String[] DUNGEON_CARD_NAMES = {
            "Ferocious Dragon", "Goblin Horde", "Dark Sorcerer", "Haunted Ghost", "Mysterious Sphinx",
            "Treacherous Trap", "Enchanted Mirror", "Cursed Witch", "Vampire Lord", "Labyrinth of Shadows",
            "Thundering Minotaur", "Dreadful Demon", "Chaos Cultists", "Skeletal Warriors", "Poisonous Serpent",
            "Ancient Guardian", "Lurking Shadows", "Venomous Spider", "Lost Souls", "Evil Warlock",
            // Add more dungeon card names
    };

    private static final String[] DUNGEON_DESCRIPTIONS = {
            "A terrifying dragon with immense power", "A horde of goblins causing chaos", "A dark sorcerer with malevolent spells",
            "A haunted and vengeful ghost", "A mysterious sphinx that speaks in riddles",
            "A treacherous trap waiting to deceive", "An enchanted mirror with mystical powers",
            "A cursed witch casting dark spells", "A powerful vampire lord", "A perilous labyrinth full of shadows",
            "A thundering minotaur with great strength", "A dreadful demon with terrifying presence",
            "Chaos-worshipping cultists", "Undead skeletal warriors", "A poisonous serpent",
            "An ancient guardian protecting its domain", "Lurking shadows waiting to strike",
            "A venomous spider with deadly bite", "Lost and tormented souls", "An evil warlock's curse",
            // Add more dungeon descriptions
    };

    private static final String[] DUNGEON_TYPES = {"DUNGEON"};
    private static final String[] DUNGEON_SUBTYPES = {
            "MONSTER", "TRAP", "PUZZLE", "HAUNT", "ENEMY",
            "BOSS", "DEADLY", "UNDEAD", "MAGICAL", "MYSTERIOUS",
            "CURSED", "TREACHEROUS", "FEROCIOUS", "DREADFUL", "ANCIENT",
            "VENOMOUS", "EVIL", "HAUNTED", "DARK", "MYTHICAL",
            // Add more dungeon subtypes
    };

    private static final String[] EFFECT_FUNCTIONS = {"applyEffect"};

    public static void main(String[] args) {
        generateCSV("src/main/java/com/utmunchkin/cards/cards_data.csv", 168);
    }

    private static void generateCSV(String fileName, int numberOfEntries) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < numberOfEntries; i++) {
                String cardName;
                String description;
                String cardType;
                String subtype;

                if (i % 2 == 0) {
                    // Treasure card
                    cardName = TREASURE_CARD_NAMES[i % TREASURE_CARD_NAMES.length];
                    description = TREASURE_DESCRIPTIONS[i % TREASURE_DESCRIPTIONS.length];
                    cardType = TREASURE_TYPES[0];
                    subtype = TREASURE_SUBTYPES[i % TREASURE_SUBTYPES.length];
                } else {
                    // Dungeon card
                    cardName = DUNGEON_CARD_NAMES[i % DUNGEON_CARD_NAMES.length];
                    description = DUNGEON_DESCRIPTIONS[i % DUNGEON_DESCRIPTIONS.length];
                    cardType = DUNGEON_TYPES[0];
                    subtype = DUNGEON_SUBTYPES[i % DUNGEON_SUBTYPES.length];
                }

                int combatStrength = (i % 10) + 1; // Example: Capped at 10
                String effectFunction = EFFECT_FUNCTIONS[0];

                String entry = String.format("%s,%s,%d,%b,%s,%s,%s\n",
                        cardName, description, combatStrength, i % 2 == 1, cardType, subtype, effectFunction);

                writer.write(entry);
            }
            System.out.println("CSV file generated successfully at: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
