package main.java.com.utmunchkin.cards;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Interface.Interact;
import main.java.com.utmunchkin.cards.CardData.SubType;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * The CardEffects class contains static methods representing various card effects in the game.
 */
public class CardEffects {
    private static final Random random = new Random();
    private static final CountDownLatch latch = new CountDownLatch(1);

    public CardEffects() {
        // Private constructor to prevent instantiation, as this class contains only static methods
    }

    /**
     * Applies the effect of a given card to a player.
     *
     * @param card         The card for which the effect is applied.
     * @param player       The player to apply the effect to.
     * @param allPlayers   The list of all players in the game.
     * @param board        The game board.
     */
    public static void applyEffect(Card card, Player player, ListOfPlayer allPlayers, Board board) {
        int typeNb;
        SubType type;
        type = card.getInfo().getSubType();

        switch (card.getInfo().getCardType()) {
            case TREASURE:
                handleTreasureCardEffect(card, player, board);
                break;

            case DUNGEON:
                handleDungeonCardEffect(card, player, allPlayers, board);
                break;

            default:
                System.out.println("Type de carte inconnu !");
                break;
        }
    }

    private static void handleTreasureCardEffect(Card card, Player player, Board board) {
        int typeNb;
        SubType type;
        type = card.getInfo().getSubType();

        if (type == SubType.POTION || type == SubType.USABLE || type == SubType.LIMITED_USE || type == SubType.CONSUMABLE) {
            typeNb = 1; //Consommables
            board.updateInfo("Player " + player.getName() + " uses " + card.getCardName() + " and gets " + card.getInfo().getLevelBonus() + " life(s)");
            Interact.showMessageDialog("Player " + player.getName() + " uses " + card.getCardName() + " and gets " + card.getInfo().getLevelBonus() + " life(s)");
            player.addLives(card.getInfo().getLevelBonus());
        } else if (type == SubType.VALUABLE || type == SubType.JEWELRY || type == SubType.BIG) {
            typeNb = 2; //Valuable Objects
            board.updateInfo("Player " + player.getName() + " sells " + card.getCardName() + " and gets " + card.getInfo().getLevelBonus() + " money");
            Interact.showMessageDialog("Player " + player.getName() + " sells " + card.getCardName() + " and gets " + card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF + " money");
            player.addMoney(card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF);
        } else if (type == SubType.WEAPON || type == SubType.ONE_HAND || type == SubType.TWO_HANDS || type == SubType.SPECIAL || type == SubType.MAGICAL) {
            typeNb = 3; //Offensive Objects
            board.updateInfo("Player " + player.getName() + " uses " + card.getCardName());
            Interact.showMessageDialog("Player " + player.getName() + " uses weapon: " + card.getCardName() +". Attack updated!");
            player.takeGameObjectBonus(card);
        } else if (type == SubType.ARMOR || type == SubType.HEADGEAR || type == SubType.FOOTGEAR || type == SubType.ENCHANTED) {
            typeNb = 4; //Defensive Objects
            board.updateInfo("Player " + player.getName() + " uses " + card.getCardName());
            Interact.showMessageDialog("Player " + player.getName() + " uses defense object: " + card.getCardName() +". Defense updated!");
            player.setDefense(card, board);
        } else {
            typeNb = 0;
            System.out.println("NOT KNOWN TYPE !");
        }
    }

    private static void handleDungeonCardEffect(Card card, Player player, ListOfPlayer allPlayers, Board board) {
        int typeNb;
        SubType type;
        type = card.getInfo().getSubType();

        if (type == SubType.MONSTER || type == SubType.ENEMY || type == SubType.DEADLY || type == SubType.UNDEAD
                || type == SubType.FEROCIOUS || type == SubType.DREADFUL) {
            typeNb = 11; //Monstres communs
            handleMonsterCommonEffect(card, player, board);
        } else if (type == SubType.TREACHEROUS || type == SubType.VENOMOUS) {
            typeNb = 12; //Monstres traîtres
            handleMonsterTraitorEffect(card, player, board);
        } else if (type == SubType.BOSS || type == SubType.ANCIENT || type == SubType.MYTHICAL || type == SubType.EVIL) {
            typeNb = 13; //Boss
            handleBossEffect(card, player, board);
        } else if (type == SubType.CURSED || type == SubType.DARK || type == SubType.HAUNTED) {
            typeNb = 14; //Monstres maudits
            handleCursedMonsterEffect(card, player, allPlayers, board);
        } else if (type == SubType.CURSE) {
            typeNb = 15;
            handleCurseCardEffect(card, player);
        } else if (type == SubType.TRAP || type == SubType.MYSTERIOUS) {
            typeNb = 16;
            handleTrapCard(card, player);
        } else {
            typeNb = 10;
            System.out.println("Type inconnu !");
        }
    }

    private static void handleMonsterCommonEffect(Card card, Player player, Board board) {
        int damage;

        // Exemple d'effet pour les monstres communs
        if(card.getInfo().getLevelBonus() > player.getDefense()){
            damage = Math.abs(card.getInfo().getLevelBonus() - player.getDefense());
        }else{
            damage = random.nextInt(2);
        }
        
        board.updateInfo("Monster " + card.getCardName() + " makes you lose " + damage + " life(s)");
        if(player.getDefense()>0){
            Interact.showMessageDialog("You defense has limited the damage");
            player.decreaseDefense(10);
        }
        Interact.showMessageDialog("Monster " + card.getCardName() + " makes you lose " + damage + " life(s)");
        player.addLives(- damage); // Le joueur perd une vie en combattant un monstre commun
    }

    private static void handleMonsterTraitorEffect(Card card, Player player, Board board) {
        // Exemple d'effet pour les monstres traîtres
        int lostLives = random.nextInt(card.getInfo().getLevelBonus()) + 1; // Perdre nombre aléatoire de vies
        int lostMoney = random.nextInt(card.getInfo().getLevelBonus()) + 1; // Perdre entre random unités d'argent
        int damage;

        if(lostLives > player.getDefense()){
            damage = Math.abs(lostLives - player.getDefense());
        }else{
            damage = random.nextInt(2);
        }

        board.updateInfo("Monster " + card.getCardName() + " makes you lose " + damage + " life(s) & " + lostMoney + " money");
        if(player.getDefense()>0){
            Interact.showMessageDialog("You defense has limited the damage");
            player.decreaseDefense(10);
        }
        Interact.showMessageDialog("Monster " + card.getCardName() + " makes you lose " + damage + " life(s) & " + lostMoney + " money");
        player.addLives(-lostLives);
        player.addMoney(-lostMoney);
    }

    private static void handleBossEffect(Card card, Player player, Board board) {
        // Exemple d'effet pour les boss
        int bossPower = card.getInfo().getLevelBonus() * random.nextInt(Constant.BOSS_UPGRADE) + 1; // Coefficient arbitraire
        
        if (player.getAttackForce() < bossPower) {
            
            int damage = random.nextInt(bossPower) + 50; // Perdre random * 2 vies en cas de défaite contre le boss

            if(damage > player.getDefense()){
                damage = Math.abs(damage - player.getDefense());
            }else{
                damage = random.nextInt(10)+10;
            }
            
            board.updateInfo("BOSS " + card.getCardName() + " makes you lose " + damage + " life(s)");

            if(player.getDefense()>0){
                Interact.showMessageDialog("You defense has limited the damage");
                player.decreaseDefense(20);
            }

            Interact.showMessageDialog("BOSS " + card.getCardName() + " makes you lose " + damage + " life(s)");
            player.addLives(-damage);
        }
    }

    private static void handleCursedMonsterEffect(Card card, Player player, ListOfPlayer allPlayers, Board board) {
        // Exemple d'effet pour les monstres maudits
        int randomPlayerIndex = random.nextInt(allPlayers.getSize()); // Sélectionne un joueur au hasard
        Player randomPlayer = allPlayers.getPlayer(randomPlayerIndex);
        int lostLivesOrMoney = random.nextInt(2); // Choix aléatoire entre perdre des vies (0) ou de l'argent (1)

        if (lostLivesOrMoney == 0) {
            int livesLost = random.nextInt(card.getInfo().getLevelBonus()) + 1; // Perdre entre 1 et 3 vies
            int damage;

            if(livesLost > player.getDefense()){
                damage = Math.abs(livesLost - player.getDefense());
            }else{
                damage = random.nextInt(2);
            }
            
            board.updateInfo("Monster " + card.getCardName() + " makes " + randomPlayer.getName() + " lose " + damage + " life(s)");
            if(player.getDefense()>0){
                Interact.showMessageDialog("You defense has limited the damage");
                player.decreaseDefense(10);
            }
            Interact.showMessageDialog("Monster " + card.getCardName() + " makes " + randomPlayer.getName() + " lose " + damage + " life(s)");
            randomPlayer.addLives(-livesLost);
        } else {
            int moneyLost = random.nextInt(card.getInfo().getLevelBonus()) + 1; // Perdre entre 1 et 10 unités d'argent
            board.updateInfo("Monster " + card.getCardName() + " makes " + randomPlayer.getName() + " lose " + moneyLost + " money");
            Interact.showMessageDialog("Monster " + card.getCardName() + " makes " + randomPlayer.getName() + " lose " + moneyLost + " money");
            randomPlayer.addMoney(-moneyLost);
        }
    }

    private static void handleCurseCardEffect(Card card, Player player) {
        // Exemple d'effet pour les cartes de type CURSE
        int response = JOptionPane.showConfirmDialog(null, "You are cursed ! Do you want to lose lives to remove it ?", "Curse", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            player.addLives(- card.getInfo().getLevelBonus()/10); // Le joueur perd une vie pour lever la malédiction
        } else {
            player.setCurse(true);
            Interact.showMessageDialog("You stay cursed !");
        }
    }

    public static void handleTrapCard(Card card, Player player) {
        JFrame frame = new JFrame("Trap Card Window");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new GridLayout(5, 5));

        JButton[][] buttons = new JButton[5][5];

        List<Integer> allPositions = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            allPositions.add(i);
        }

        Collections.shuffle(allPositions);

        List<Integer> positiveEffectPositions = allPositions.subList(0, 3);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBackground(Color.WHITE);

                if (positiveEffectPositions.contains(i * 5 + j)) {
                    buttons[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton source = (JButton) e.getSource();
                            handlePositiveAction(player);
                            frame.dispose();
                            latch.countDown(); // Count down the latch to release the waiting thread
                        }
                    });
                } else {
                    buttons[i][j].addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JButton source = (JButton) e.getSource();
                            handleNegativeAction(player);
                            frame.dispose();
                            latch.countDown(); // Count down the latch to release the waiting thread
                        }
                    });
                }

                frame.add(buttons[i][j]);
            }
        }

        int[][] matrix = new int[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (positiveEffectPositions.contains(i * 5 + j)) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        printMatrix(matrix);

        frame.setAlwaysOnTop(true);
        frame.setVisible(true);

        try {
            latch.await(); // Wait for the latch to be counted down
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void printMatrix(int[][] matrix) {
        System.out.println("Matrix:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void handlePositiveAction(Player player) {
        player.addLives(player.getLives()+10);
        player.addMoney(player.getMoney()+10);
        Interact.showMessageDialog("You escaped the trap successfully !");
    }

    private static void handleNegativeAction(Player player) {
        player.addLives(-10);
        player.addMoney(-20);
        Interact.showMessageDialog(" Ouch !, you lose lives and money!");
    }

    // Add more effect methods as need
}
    
