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

    public PlayerFrame(String playerName, ListOfPlayer listOfPlayer, int playerIndex) {
        this.playerName = playerName;
        this.cardButtons = new ArrayList<>();

        setLayout(new GridLayout(2, 4)); // 2 rows, 4 columns
        setBorder(BorderFactory.createTitledBorder(playerName));

        // Assuming listOfPlayer is an instance of ListOfPlayer
        List<Card> hand = listOfPlayer.getPlayer(playerIndex).getHand();

        setBackground(getUniqueColor(playerIndex));

        for (int i = 0; i < hand.size(); i++) {
            String cardName = hand.get(i).getCardName();
            JButton cardButton = new JButton(cardName);
            cardButton.setEnabled(false);
            cardButtons.add(cardButton);
            add(cardButton);

            cardButton.addActionListener(new CardButtonListener(cardName));
        }
    }

    private Color getUniqueColor(int playerIndex) {
        int red = playerIndex * 50 % 255;
        int green = playerIndex * 80 % 255;
        int blue = playerIndex * 120 % 255;
        return new Color(red, green, blue);
    }

    public void updatePlayerHand(List<Card> updatedHand) {
        removeAll(); // Clear existing buttons

        for (int i = 0; i < updatedHand.size(); i++) {
            String cardName = updatedHand.get(i).getCardName();
            JButton cardButton = new JButton(cardName);
            cardButton.setEnabled(false);
            cardButtons.add(cardButton);
            add(cardButton);

            cardButton.addActionListener(new CardButtonListener(cardName));
        }

        revalidate(); // Refresh the layout
        repaint();    // Repaint the panel
    }

    private class CardButtonListener implements ActionListener {
        private String cardName;

        public CardButtonListener(String cardName) {
            this.cardName = cardName;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle the button click (you can implement specific behavior here)
        }
    }

    public void updateStats(String name, int level, int lives, double money, boolean curse) {
        setBorder(BorderFactory.createTitledBorder(name + " - Level: " + level + " - Lives: " + lives + " - Money: " + money + " - Curse: " + curse));

        revalidate(); // Refresh the layout
        repaint();    // Repaint the panel
    }
}
