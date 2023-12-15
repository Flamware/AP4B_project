// Board.java
package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.players.Player;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.cards.Dungeon;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.gameplay.Play;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Board extends JFrame {

    private List<PlayerFrame> playerFrames;
    private Play game;
    private JButton drawDungeonButton;
    private JButton drawTreasureButton;
    private JTextArea infoTextArea;
    private int choice;

    public Board(Play a) {
        setTitle("Munchkin Game Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.game = a;
        playerFrames = new ArrayList<>();

        // Initialize JTextArea for information display
        infoTextArea = new JTextArea(10, 30);
        infoTextArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        add(infoScrollPane, BorderLayout.EAST);

        // Initialize center panel for player frames
        JPanel centerPanel = new JPanel(new GridLayout(3, 4));
        initializePlayerFrames(centerPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Initialize top panel for draw buttons
        JPanel topPanel = new JPanel();
        initializeDrawButtons(topPanel);
        add(topPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);

        // Mise à jour initiale des statistiques des joueurs
        updatePlayerStats(game.getPlayers());
    }

    private void initializeDrawButtons(JPanel panel) {
        drawDungeonButton = new JButton("Draw Dungeon");
        drawTreasureButton = new JButton("Draw Treasure");

        drawDungeonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gérer ici la logique de tirage de carte Donjon
                System.out.println("Dungeon Card drawn!");
                updateInfo("Dungeon Card drawn!");
            }
        });

        drawTreasureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Gérer ici la logique de tirage de carte Trésor
                System.out.println("Treasure Card drawn!");
                updateInfo("Treasure Card drawn!");
            }
        });

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

    public void showInstructionDialog(String instruction) {
        InstructionDialog instructionDialog = new InstructionDialog(this, instruction);
        instructionDialog.setVisible(true);

        // Check user response if needed
        if (instructionDialog.getUserResponse()) {
            choice = instructionDialog.getUserInput();
            updateInfo("User entered: " + choice);
        } else {
            updateInfo("User canceled the input.");
        }
    }


    public void updateDrawPileWindow(Treasure updatedTreasure, Dungeon updatedDungeon) {
        // Update logic for draw pile window based on updatedTreasure and updatedDungeon
        infoTextArea.append("Updated Draw Pile Window\n");
    }

    public int getChoice() {
        return choice;
    }

    public String yesOrNoDialog(String message) {
        // Affiche une boîte de dialogue avec les boutons "Oui" et "Non"
        int choix = JOptionPane.showConfirmDialog(null, message, "Question", JOptionPane.YES_NO_OPTION);

        // Vérifie le choix de l'utilisateur et renvoie "Oui" ou "Non"
        if (choix == JOptionPane.YES_OPTION) {
            System.out.println("Vous avez choisi Oui.");
            return "Oui";
        } else {
            System.out.println("Vous avez choisi Non.");
            return "Non";
        }
    }

    public void showMessageDialog(String string) {
    }

    public void showPlayerStats(Player player) {
    }

    public int showYesNoDialog(String string) {
        return 0;
    }


}
