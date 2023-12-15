package main.java.com.utmunchkin.cards;

import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import java.util.Random;

import javax.swing.JOptionPane;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.cards.CardData;
import main.java.com.utmunchkin.cards.CardData.CardType;
import main.java.com.utmunchkin.cards.CardData.TreasureType;

public class CardEffects {
    private static final Random random = new Random();
    private static ListOfPlayer listOfPlayer;

    private CardEffects() {
        // Private constructor to prevent instantiation, as this class contains only static methods
    }

    public static void applyEffect(Card card, Player player, String effectFunction) {
        int typeNb;
        TreasureType type;
        type = card.getInfo().getTreasureType();

        switch (card.getInfo().getCardType()) {
            case TREASURE:
                if(type == TreasureType.POTION || type == TreasureType.USABLE || type == TreasureType.LIMITED_USE || type == TreasureType.CONSUMABLE){
                    typeNb = 1; //Consommables
                }else if(type == TreasureType.VALUABLE || type == TreasureType.JEWELRY){
                    typeNb = 2; //Valuable Objects
                }else if(type == TreasureType.WEAPON || type == TreasureType.ONE_HAND || type == TreasureType.TWO_HANDS || type == TreasureType.SPECIAL){
                    typeNb = 3; //Offensive Objects
                }else if(type == TreasureType.ARMOR || type == TreasureType.HEADGEAR || type == TreasureType.FOOTGEAR){
                    typeNb = 4; //Defensive Objects
                }else{
                    typeNb = 0;
                    System.out.println("NOT KNOWN TYPE !");
                }
                
                switch (typeNb) {
                    case 1:
                        player.addLives(card.getInfo().getLevelBonus());
                        break;
                    case 2:
                        player.addMoney(card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF);
                        break;
                    case 3:
                        player.takeGameObjectBonus(card);
                        break;
                    case 4:
                        player.setDefense(card);
                        break;
                
                    default:
                        break;
                }
                break;
            
            case DUNGEON:
                // Exemple d'effet pour les cartes de type DUNGEON
                player.addLives(-2); // Réduit la vie du joueur de 2
                break;

            case CURSE:
                // Exemple d'effet pour les cartes de type CURSE
                int response = JOptionPane.showConfirmDialog(null, "Vous êtes maudit ! Voulez-vous perdre une vie pour lever la malédiction?", "Malédiction", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    player.addLives(-1); // Le joueur perd une vie pour lever la malédiction
                }else{
                    player.setCurse(true);
                }
                break;

            case MONSTER:
                type = card.getInfo().getTreasureType();
                
                if(type == TreasureType.ENEMY || type == TreasureType.DEADLY || type == TreasureType.UNDEAD || type == TreasureType.MYSTERIOUS 
                || type == TreasureType.FEROCIOUS || type == TreasureType.DREADFUL){
                    typeNb = 11; //Monstres communs
                }else if(type == TreasureType.TREACHEROUS || type == TreasureType.VENOMOUS){
                    typeNb = 12; //Monstres traîtres
                }else if(type == TreasureType.BOSS || type == TreasureType.ANCIENT || type == TreasureType.MYTHICAL || type == TreasureType.EVIL){
                    typeNb = 13; //Boss
                }else if(type == TreasureType.CURSED || type == TreasureType.DARK || type == TreasureType.HAUNTED){
                    typeNb = 14; //Monstres maudits
                }else{
                    typeNb = 10;
                    System.out.println("Type inconnu !");
                }

                switch (typeNb) {
                    case 11:
                        // Exemple d'effet pour les monstres communs
                        player.addLives(-1); // Le joueur perd une vie en combattant un monstre commun
                        break;
                    case 12:
                        // Exemple d'effet pour les monstres traîtres
                        int lostLives = random.nextInt(3) + 1; // Perdre entre 1 et 3 vies
                        int lostMoney = random.nextInt(10) + 1; // Perdre entre 1 et 10 unités d'argent
                        player.addLives(-lostLives);
                        player.addMoney(-lostMoney);
                        break;
                    case 13:
                        // Exemple d'effet pour les boss
                        int bossPower = card.getInfo().getLevelBonus() * 2; // Coefficient arbitraire
                        if (player.getAttackForce() < bossPower) {
                            int damage = random.nextInt(6) + 5; // Perdre entre 5 et 10 vies en cas de défaite contre le boss
                            player.addLives(-damage);
                        }
                        break;
                    case 14:
                        // Exemple d'effet pour les monstres maudits
                        int randomPlayerIndex = random.nextInt(listOfPlayer.getSize()); // Sélectionne un joueur au hasard
                        Player randomPlayer = listOfPlayer.getPlayer(randomPlayerIndex);
                        int lostLivesOrMoney = random.nextInt(2); // Choix aléatoire entre perdre des vies (0) ou de l'argent (1)
                        
                        if (lostLivesOrMoney == 0) {
                            int livesLost = random.nextInt(3) + 1; // Perdre entre 1 et 3 vies
                            randomPlayer.addLives(-livesLost);
                        } else {
                            int moneyLost = random.nextInt(10) + 1; // Perdre entre 1 et 10 unités d'argent
                            randomPlayer.addMoney(-moneyLost);
                        }
                        break;
                    default:
                        break;
                }
                break;

            default:
                System.out.println("Type de carte inconnu !");
                break;
        }
    }
    // Add more effect methods as needed
}
