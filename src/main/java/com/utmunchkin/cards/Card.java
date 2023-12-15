package main.java.com.utmunchkin.cards;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.players.Player;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Random;

public class Card extends CardData {

    protected final CardData.CardInfo cardInfo;

    public Card(String cardName) {
        // Load card data using a relative path
        String relativePath = "src/main/java/com/utmunchkin/cards/cards_data.csv";
        CardData.loadCardDataFromFile(new File(relativePath));
        // Get card info from CardData
        this.cardInfo = CardData.getCardInfo(cardName);
    }
    //default constructor
    public Card() {
        this.cardInfo = CardData.getRandomCardInfo();
    }

    public void applyCardEffect(Player player) {
        // Implement specific behavior for each card type
        // Use cardInfo to access attributes like levelBonus, isCursed, etc.
        System.out.println("Applying card effect: " + cardInfo.getCardName());

        // Get the function name from the card info
        String effectFunctionName = cardInfo.getEffectFunctionName();

        // Call the corresponding function using reflection
        try {
            Class<?> effectClass = Class.forName("main.java.com.utmunchkin.cards.CardEffects");
            Method effectMethod = effectClass.getMethod(effectFunctionName, Player.class);
            effectMethod.invoke(null, player);  // Assuming the methods are static
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCardName() {
        return cardInfo.getCardName();
    }

    public CardData.CardInfo getInfo() {
        return cardInfo;
    }
    public String toString() {
        return cardInfo.getCardInfo();
    }
    public int getMonsterCombatStrength() {
        return Monster.getMonsterCombatStrengthMonster();
    }
    public int getLevels() {
        return Monster.getLevelsMonster();
    }
    public int getTreasures() {
        return Monster.getTreasuresMonster();
    }

    public static class Monster extends Card {
        private static int monsterCombatStrength;
        private static int levels;
        private static int treasures;
    
    
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
    
        private void setTreasure(int treas) {
            treasures = treas;
        }
    
        private void setLevel(int lvl) {
            levels = lvl;
        }
    
        public void setMonsterCombatStrength(int combatStrength) {
            monsterCombatStrength = combatStrength;
        }

        public static int getMonsterCombatStrengthMonster() {
            return monsterCombatStrength;
        }
    
        public static int getLevelsMonster() {
            return levels;
        }
    
        public static int getTreasuresMonster() {
            return treasures;
        }
    }

    public static class Curse extends Card{
        int cost;

        public Curse() {
            this.cost = Constant.DEFAULT_CURSE_COST;
        }  

        public void reccurentCurse(Player player){
            Random random = new Random();
            // Exemple d'effet pour les sorts de type reccurentCurse
            int curseType = random.nextInt(2); // Choix aléatoire entre affecter les vies (0) ou l'argent (1)
            int curseValue = random.nextInt(5) + 1; // Valeur aléatoire entre 1 et 5

            if (curseType == 0) {
                // Affecter les vies
                player.addLives(-curseValue);
                System.out.println("Le sort a maudit le joueur en lui faisant perdre " + curseValue + " vies.");
            } else {
                // Affecter l'argent
                player.addMoney(-curseValue);
                System.out.println("Le sort a maudit le joueur en lui faisant perdre " + curseValue + " unités d'argent.");
            }
        }

        public void sufferCurseNow(){

        }
    }

}
