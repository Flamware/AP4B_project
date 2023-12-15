package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Interface.PlayerFrame;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Board extends JFrame {

    private List<PlayerFrame> playerFrames;
    private Play game;
    private JButton drawDungeonButton;
    private JButton drawTreasureButton;
    private JTextArea infoTextArea;
    private int choice;

    private CountDownLatch userInputLatch;

    private JPanel dungeonPanel;
    private JPanel treasurePanel;
    private JPanel playerHandsPanel;
    private JPanel playerStatsPanel;

    public Board(Play a) {
        setTitle("Munchkin Game Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        resetUserInputLatch();

        this.game = a;
        playerFrames = new ArrayList<>();

        infoTextArea = new JTextArea(10, 30);
        infoTextArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        add(infoScrollPane, BorderLayout.EAST);

        dungeonPanel = new JPanel(new GridLayout(2, 1));
        treasurePanel = new JPanel(new GridLayout(2, 1));
        playerHandsPanel = new JPanel(new GridLayout(1, 0));
        playerStatsPanel = new JPanel(new GridLayout(1, 2));

        dungeonPanel.setBorder(BorderFactory.createTitledBorder("Donjon"));
        treasurePanel.setBorder(BorderFactory.createTitledBorder("Trésor"));
        playerHandsPanel.setBorder(BorderFactory.createTitledBorder("Main du Joueur"));
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques du Joueur"));

        add(dungeonPanel, BorderLayout.NORTH);
        add(treasurePanel, BorderLayout.SOUTH);
        add(playerHandsPanel, BorderLayout.CENTER);
        add(playerStatsPanel, BorderLayout.WEST);

        initializeDrawButtons(dungeonPanel);
        initializeDrawButtons(treasurePanel);
        initializePlayerFrames(playerHandsPanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        updatePlayerStats(game.getPlayers());
    }

    private void initializeDrawButtons(JPanel panel) {
        drawDungeonButton = new JButton("Piocher une carte Donjon");
        drawTreasureButton = new JButton("Piocher une carte Trésor");

        drawDungeonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Carte Donjon piochée !");
                updateInfo("Carte Donjon piochée !");
                userInputLatch.countDown();
            }
        });

        drawTreasureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Carte Trésor piochée !");
                updateInfo("Carte Trésor piochée !");
                userInputLatch.countDown();
            }
        });

        addHoverEffect(drawDungeonButton);
        addHoverEffect(drawTreasureButton);

        panel.add(drawDungeonButton);
        panel.add(drawTreasureButton);
    }

    private void initializePlayerFrames(JPanel panel) {
        for (int i = 0; i < game.getPlayers().getSize(); i++) {
            PlayerFrame playerFrame = new PlayerFrame(game.getPlayers().getPlayer(i).getName(), game.getPlayers(), i);
            playerFrames.add(playerFrame);
            panel.add(playerFrame);
        }
    }

    public void updatePlayerStats(ListOfPlayer players) {
        for (int i = 0; i < playerFrames.size(); i++) {
            PlayerFrame playerFrame = playerFrames.get(i);
            Player player = players.getPlayer(i);
            playerFrame.updateStats(player.getName(), player.getLevel(), player.getLives(), player.getMoney(), player.getCurse());
        }
    }

    public void updateInfo(String message) {
        infoTextArea.append(message + "\n");
    }

    // Remplacez la méthode showInstructionDialog par :
    public String showInstructionDialog(String instruction) {
        InstructionDialog instructionDialog = new InstructionDialog(this, instruction);
        instructionDialog.setVisible(true);

        // Vous n'avez plus besoin de vérifier la réponse de l'utilisateur ou de récupérer l'entrée utilisateur ici.
        // Le dialogue d'instruction est principalement informatif, et l'utilisateur n'a pas à fournir une réponse directe.

        return null;  // La méthode n'a plus besoin de renvoyer une valeur.
    }

    // Remplacez la méthode getChoice par :
    public void setChoice(int choice) {
        this.choice = choice;
    }

    // Remplacez la méthode showMessageDialog par :
    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }


    public void waitForUserInput() {
        try {
            userInputLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetUserInputLatch() {
        userInputLatch = new CountDownLatch(1);
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setContentAreaFilled(false);
            }
        });
    }

    public void showPlayerStats(Player player) {
    }

    public int showYesNoDialog(String s) {
        return 0;
    }

    public int showCardSelectionDialog(List<Card> monstersInHand) {
        return 0;
    }

    public String showInputDialog(String s, String message, String[] playerNames, String playerName) {
        return "uwu";
    }

    public int getChoice() {
        return 0;
    }

    public Object yesOrNoDialog(String s) {
        return null;
    }
}
