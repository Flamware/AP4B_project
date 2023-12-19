import main.java.com.utmunchkin.Rules;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
            }
        });*/

        MainMenu.play();
    }

    public static class MainMenu {

        public MainMenu() {
            // Créer la fenêtre principale
            JFrame frame = new JFrame("Menu Principal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);

            // Créer un panneau pour organiser les composants
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1));

            // Bouton "Jouer"
            JButton playButton = new JButton("Jouer");
            playButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    // Ajoutez le code à exécuter lorsque le bouton "Jouer" est cliqué
                    play();
                }
            });

            // Bouton "Options"
            JButton optionsButton = new JButton("Options");
            optionsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ajoutez le code à exécuter lorsque le bouton "Options" est cliqué
                    JOptionPane.showMessageDialog(frame, "Accéder aux options.");
                }
            });

            // Bouton "Records"
            JButton recordsButton = new JButton("Records");
            recordsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ajoutez le code à exécuter lorsque le bouton "Records" est cliqué
                    JOptionPane.showMessageDialog(frame, "Consulter les records.");
                }
            });

            // Ajouter les boutons au panneau
            panel.add(playButton);
            panel.add(optionsButton);
            panel.add(recordsButton);

            // Ajouter le panneau à la fenêtre
            frame.add(panel);

            // Centrer la fenêtre
            frame.setLocationRelativeTo(null);

            // Rendre la fenêtre visible
            frame.setVisible(true);
        }

        public static void play(){
            Rules rules = new Rules();
            ListOfPlayer list = new ListOfPlayer();
            list.addPlayer(new Player("Joueur 1", 1));
            list.addPlayer(new Player("Joueur 2", 2));
            list.addPlayer(new Player("Joueur 3", 3));
            list.addPlayer(new Player("Joueur 4", 4));
            rules.setTurn(list);

            // Now you have the first player index from Rules
            int firstPlayerIndex = rules.getFirstPlayerIndex();

            // Create an instance of Play with the first player index
            Play game = new Play(list, firstPlayerIndex);
            game.gameProcess();
        }
    }
}
