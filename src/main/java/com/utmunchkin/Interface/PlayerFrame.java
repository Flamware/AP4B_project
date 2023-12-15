package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.players.ListOfPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PlayerFrame extends JPanel {

    private String playerName;
    private List<JButton> cardButtons;
    private JTextArea monsterStatsTextArea;
    private boolean isCurrentPlayer;

    // Constructeur
    public PlayerFrame(String playerName, ListOfPlayer listOfPlayer, int playerIndex) {
        this.playerName = playerName;
        this.cardButtons = new ArrayList<>();
        this.monsterStatsTextArea = new JTextArea();

        setLayout(new BorderLayout());

        // Définit la bordure initiale en fonction du tour du joueur actuel
        updateBorder();

        // Supposons que listOfPlayer soit une instance de ListOfPlayer
        List<Card> hand = listOfPlayer.getPlayer(playerIndex).getHand();

        setBackground(getUniqueColor(playerIndex));

        JPanel handPanel = new JPanel(new GridLayout(2, 4)); // 2 lignes, 4 colonnes
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            JButton cardButton = new JButton(card.getCardName()/*, card.getImage()*/);
            cardButtons.add(cardButton);
            handPanel.add(cardButton);

            cardButton.addActionListener(new CardButtonListener(card));
        }

        JScrollPane scrollPane = new JScrollPane(monsterStatsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(handPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Obtient une couleur unique en fonction de l'index du joueur
    private Color getUniqueColor(int playerIndex) {
        int red = playerIndex * 50 % 255;
        int green = playerIndex * 80 % 255;
        int blue = playerIndex * 120 % 255;
        return new Color(red, green, blue);
    }

    // Met à jour la main du joueur avec des boutons de cartes cliquables
    public void updatePlayerHand(List<Card> updatedHand) {
        // Efface les boutons existants
        cardButtons.clear();
        removeAll();

        JPanel handPanel = new JPanel(new GridLayout(2, 4)); // 2 lignes, 4 colonnes
        for (int i = 0; i < updatedHand.size(); i++) {
            Card card = updatedHand.get(i);
            JButton cardButton = new JButton(card.getCardName()/*, card.getImage()*/);
            cardButtons.add(cardButton);
            handPanel.add(cardButton);

            cardButton.addActionListener(new CardButtonListener(card));
        }

        JScrollPane scrollPane = new JScrollPane(monsterStatsTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(handPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Met à jour la bordure en fonction du tour du joueur actuel
        updateBorder();

        revalidate(); // Rafraîchit la mise en page
        repaint();    // Redessine le panneau
    }

    // Obtient l'image associée à la carte (non implémenté, à personnaliser)
    public ImageIcon getImage() {
        // Vous devez remplacer cela par la logique réelle pour obtenir l'image de la carte
        return new ImageIcon("path/to/your/card/image.png");
    }

    // Met à jour les statistiques du joueur
    public void updateStats(String name, int level, int lives, double money, boolean curse) {
        setBorder(BorderFactory.createTitledBorder(name + " - Level: " + level + " - Lives: " + lives + " - Money: " + money + " - Curse: " + curse));

        revalidate(); // Rafraîchit la mise en page
        repaint();    // Redessine le panneau
    }

    // Met à jour les statistiques du monstre
    public void updateMonsterStats(String monsterStats) {
        monsterStatsTextArea.setText(monsterStats);
    }

    // Met à jour la bordure du panneau en fonction du tour du joueur actuel
    public void updateBorder() {
        if (isCurrentPlayer) {
            setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Vous pouvez personnaliser la couleur et l'épaisseur de la bordure
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
    }

    // Obtient le nom du joueur
    public String getPlayerName() {
        return this.playerName;
    }

    // Gestionnaire d'événements pour les boutons de carte
    private class CardButtonListener implements ActionListener {
        private Card card;

        public CardButtonListener(Card card) {
            this.card = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Gérer ici la logique lorsqu'une carte est cliquée
            System.out.println("Card Clicked: " + card.getCardName());
        }
    }

    // Définit si le joueur est le joueur actuel
    public void setCurrentPlayer(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
    }
}
