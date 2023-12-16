package main.java.com.utmunchkin.cards;

import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Interface.Interact;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import java.util.Random;

import javax.swing.JOptionPane;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.cards.CardData;
import main.java.com.utmunchkin.cards.CardData.CardType;
import main.java.com.utmunchkin.cards.CardData.SubType;

public class CardEffects {
    private static final Random random = new Random();

    public CardEffects() {
        // Private constructor to prevent instantiation, as this class contains only static methods
    }

    public void applyEffect(Card card, Player player, String effectFunction, ListOfPlayer allPlayers, Board board) {
        int typeNb;
        SubType type;
        type = card.getInfo().getSubType();

        switch (card.getInfo().getCardType()) {
            case TREASURE:
                if(type == SubType.POTION || type == SubType.USABLE || type == SubType.LIMITED_USE || type == SubType.CONSUMABLE){
                    typeNb = 1; //Consommables
                }else if(type == SubType.VALUABLE || type == SubType.JEWELRY){
                    typeNb = 2; //Valuable Objects
                }else if(type == SubType.WEAPON || type == SubType.ONE_HAND || type == SubType.TWO_HANDS || type == SubType.SPECIAL){
                    typeNb = 3; //Offensive Objects
                }else if(type == SubType.ARMOR || type == SubType.HEADGEAR || type == SubType.FOOTGEAR){
                    typeNb = 4; //Defensive Objects
                }else{
                    typeNb = 0;
                    System.out.println("NOT KNOWN TYPE !");
                }
                
                switch (typeNb) {
                    case 1:
                        board.updateInfo("Player " + player.getName() + " uses " + card.getCardName() + " and get " + card.getInfo().getLevelBonus() + " live(s)");
                        player.addLives(card.getInfo().getLevelBonus());
                        break;
                    case 2:
                        board.updateInfo("Player " + player.getName() + " uses (solds) " + card.getCardName() + " and get " + card.getInfo().getLevelBonus() + " money");
                        player.addMoney(card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF);
                        break;
                    case 3:
                        board.updateInfo("Player " + player.getName() + " uses " + card.getCardName());
                        player.takeGameObjectBonus(card);
                        break;
                    case 4:
                        board.updateInfo("Player " + player.getName() + " uses " + card.getCardName());
                        player.setDefense(card, board);
                        break;
                
                    default:
                        break;
                }
                break;

            case DUNGEON:
                if(type == SubType.MONSTER || type == SubType.ENEMY || type == SubType.DEADLY || type == SubType.UNDEAD || type == SubType.MYSTERIOUS
                        || type == SubType.FEROCIOUS || type == SubType.DREADFUL){
                    typeNb = 11; //Monstres communs
                }else if(type == SubType.TREACHEROUS || type == SubType.VENOMOUS){
                    typeNb = 12; //Monstres traîtres
                }else if(type == SubType.BOSS || type == SubType.ANCIENT || type == SubType.MYTHICAL || type == SubType.EVIL){
                    typeNb = 13; //Boss
                }else if(type == SubType.CURSED || type == SubType.DARK || type == SubType.HAUNTED) {
                    typeNb = 14; //Monstres maudits
                }else if(type == SubType.CURSE){
                    typeNb = 15;
                }else{
                    typeNb = 10;
                    System.out.println("Type inconnu !");
                }

                switch (typeNb) {
                    case 11:
                        // Exemple d'effet pour les monstres communs
                        board.updateInfo("Monster " + card.getCardName() + " make you lose " + " -1 " + " live(s)");
                        player.addLives(-1); // Le joueur perd une vie en combattant un monstre commun
                        break;
                    case 12:
                        // Exemple d'effet pour les monstres traîtres
                        int lostLives = random.nextInt(3) + 1; // Perdre entre 1 et 3 vies
                        int lostMoney = random.nextInt(10) + 1; // Perdre entre 1 et 10 unités d'argent
                        board.updateInfo("Monster " + card.getCardName() + " make you lose " + lostLives + " live(s) & " + lostMoney + " money");
                        player.addLives(-lostLives);
                        player.addMoney(-lostMoney);
                        break;
                    case 13:
                        // Exemple d'effet pour les boss
                        int bossPower = card.getInfo().getLevelBonus() * 2; // Coefficient arbitraire
                        if (player.getAttackForce() < bossPower) {
                            int damage = random.nextInt(6) + 5; // Perdre entre 5 et 10 vies en cas de défaite contre le boss
                            board.updateInfo("BOSS " + card.getCardName() + " make you lose " + damage + " live(s)");
                            player.addLives(-damage);
                        }
                        break;
                    case 14:
                        // Exemple d'effet pour les monstres maudits
                        int randomPlayerIndex = random.nextInt(allPlayers.getSize()); // Sélectionne un joueur au hasard
                        Player randomPlayer = allPlayers.getPlayer(randomPlayerIndex);
                        int lostLivesOrMoney = random.nextInt(2); // Choix aléatoire entre perdre des vies (0) ou de l'argent (1)
                        
                        if (lostLivesOrMoney == 0) {
                            int livesLost = random.nextInt(3) + 1; // Perdre entre 1 et 3 vies
                            board.updateInfo("Monster " + card.getCardName() + " make lose to " + randomPlayer.getName() + ", " + livesLost + " live(s) ");
                            randomPlayer.addLives(-livesLost);
                        } else {
                            int moneyLost = random.nextInt(10) + 1; // Perdre entre 1 et 10 unités d'argent
                            board.updateInfo("Monster " + card.getCardName() + " make lose to " + randomPlayer.getName() + moneyLost + " money");
                            randomPlayer.addMoney(-moneyLost);
                        }
                        break;
                    case 15:
                        // Exemple d'effet pour les cartes de type CURSE
                        int response = JOptionPane.showConfirmDialog(null, "Vous êtes maudit ! Voulez-vous perdre une vie pour lever la malédiction?", "Malédiction", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            player.addLives(-1); // Le joueur perd une vie pour lever la malédiction
                        }else{
                            player.setCurse(true);
                        }
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
