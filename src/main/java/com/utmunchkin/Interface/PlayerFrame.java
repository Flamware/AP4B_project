package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the graphical interface for each player in the game.
 */
public class PlayerFrame extends JPanel {
    private static final Color BUTTON_BACKGROUND_COLOR = Constant.CARD_BUTTON_BACKGROUND_COLOR;
    private static final Color BUTTON_FOREGROUND_COLOR = Constant.CARD_BUTTON_FOREGROUND_COLOR;
    private static final Color BUTTON_BORDER_COLOR = Constant.CARD_BUTTON_BORDER_COLOR;


    private String playerName;
    private List<JButton> cardButtons;
    private JTextArea monsterStatsTextArea;
    private boolean isCurrentPlayer;

    private JTextPane playerScrollPane;
    public static Card lastClickedCard;

    // Add a flag to indicate whether a card is clicked
    private static volatile boolean cardClicked;

    public static int lastClickedCardIndex = -1;

    // Keep track of the instances for each player
    private static Map<String, PlayerFrame> playerFrames = new HashMap<>();

    // Add a method to get the PlayerFrame instance for a player
    public static PlayerFrame getPlayerFrame(String playerName) {
        return playerFrames.get(playerName);
    }

    /**
     * Constructor for the PlayerFrame class.
     *
     * @param playerName    The name of the player.
     * @param listOfPlayer  The list of players in the game.
     * @param playerIndex    The index of the player.
     */
    public PlayerFrame(String playerName, ListOfPlayer listOfPlayer, int playerIndex) {
        this.playerName = playerName;
        this.cardButtons = new ArrayList<>();
        this.monsterStatsTextArea = new JTextArea();

        setLayout(new BorderLayout());

        // Set the initial border based on whether it's the turn of the current player
        updateBorder();

        // Assume listOfPlayer is an instance of ListOfPlayer
        List<Card> hand = listOfPlayer.getPlayer(playerIndex).getHand();

        setBackground(getUniqueColor(playerIndex));

        // Dans votre méthode où vous créez les boutons
        JPanel handPanel = new JPanel(new GridLayout(4, 5)); // 4 rows, 5 columns
        for (int indexOfCard = 0; indexOfCard < hand.size(); indexOfCard++) {
            Card card = hand.get(indexOfCard);

            // Créez un JTextArea pour afficher tous les attributs de la carte
            JTextArea cardInfoArea = new JTextArea();
            cardInfoArea.setEditable(false);
            cardInfoArea.append("Name: " + card.getCardName() + "\n");
            cardInfoArea.append("Type: " + card.getInfo().getCardType() + "\n");
            cardInfoArea.append("Subtype: " + card.getInfo().getSubType() + "\n");
            cardInfoArea.append("Value: " + card.getInfo().getLevelBonus() + "\n");
            cardInfoArea.append("Desc.: " + card.getInfo().getDescription() + "\n");
            // Ajoutez d'autres attributs de carte au besoin

            // Utilisez le nom de la carte pour construire le chemin de l'image
            String imagePath = "src/main/java/com/utmunchkin/gameplay/img/game/" + card.getCardName() + ".png";

            // Créez un JLabel pour afficher l'image
            JLabel cardImageLabel = new JLabel();
            cardImageLabel.setHorizontalAlignment(JLabel.CENTER); // Centre l'image

            // Vérifiez si le fichier image existe
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Chargez l'image depuis le chemin du fichier
                ImageIcon imageIcon = new ImageIcon(imagePath);

                // Redimensionnez l'image
                Image image = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedImageIcon = new ImageIcon(image);

                // Appliquez l'image redimensionnée au JLabel
                cardImageLabel.setIcon(resizedImageIcon);
            } else {
                // Chargez l'image par défaut si le fichier n'existe pas
                ImageIcon defaultImageIcon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/default.png");

                // Redimensionnez l'image par défaut
                Image defaultImage = defaultImageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedDefaultImageIcon = new ImageIcon(defaultImage);

                // Appliquez l'image redimensionnée par défaut au JLabel
                cardImageLabel.setIcon(resizedDefaultImageIcon);
            }


            // Créez le JButton et ajoutez le JTextArea et le JLabel
            JButton cardButton = new JButton();
            cardButton.setLayout(new BorderLayout());
            cardButton.add(cardInfoArea, BorderLayout.CENTER);
            cardButton.add(cardImageLabel, BorderLayout.SOUTH);

            // Create a rounded border and compound it with the existing line border
            Border roundedBorder = BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2);
            roundedBorder = BorderFactory.createCompoundBorder(
                roundedBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Adjust padding as needed
            );

            // Set the rounded border
            cardButton.setBorder(roundedBorder);
            cardButton.setBackground(BUTTON_BACKGROUND_COLOR);
            cardButton.setForeground(BUTTON_FOREGROUND_COLOR);

            addHoverEffect(cardButton);
            //cardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            cardButtons.add(cardButton);
            handPanel.add(cardButton);

            cardButton.addActionListener(new CardButtonListener(indexOfCard, card));
        }


        playerScrollPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(playerScrollPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(handPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add the instance to the map
        playerFrames.put(playerName, this);
    }

    /**
     * Update PlayerFrames when a player is removed.
     *
     * @param playerName      The name of the player to be removed.
     * @param listOfPlayer    The updated list of players.
     */
    public static Map<String, PlayerFrame> updatePlayerFramesOnRemoval(String playerName, ListOfPlayer listOfPlayer) {
        PlayerFrame removedFrame = playerFrames.remove(playerName);

        if (removedFrame != null) {
            // Remove the frame from the UI
            removedFrame.setVisible(false);
            removedFrame.getParent().remove(removedFrame);

            // Reorganize the remaining frames
            reorganizePlayerFrames();

            // Repaint the UI
            //removedFrame.getParent().revalidate();
            //removedFrame.getParent().repaint();
        }
        return playerFrames;
    }

    /**
     * Reorganize the remaining PlayerFrames after removal.
     */
    private static void reorganizePlayerFrames() {
        // Logic to reorganize the frames if needed
    }

    /**
     * Gets the index of the clicked card in the player's hand.
     *
     * @return The index of the clicked card.
     */
    public static int getPlayerClickedCardIndex() {
        return Play.getCurrentPlayer().getHand().indexOf(Play.getCurrentPlayer().getHand().get(lastClickedCardIndex));
    }

    // Modify this method to take a Player object as a parameter
    /**
     * Writes the given text and image to the player's scroll pane.
     *
     * @param player    The player associated with the message.
     * @param text      The text to be displayed.
     * @param imageIcon The icon to be displayed.
     */
    public void writeToScrollPane(Player player, String text, ImageIcon imageIcon) {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        if (imageIcon != null) {
            StyleConstants.setIcon(attributeSet, imageIcon);
        }

        try {
            String playerName = player.getName();
            String message = "[" + playerName + "]: " + text + "\n";

            playerScrollPane.getDocument().insertString(playerScrollPane.getDocument().getLength(), message, attributeSet);
            playerScrollPane.setCaretPosition(playerScrollPane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void addHoverEffect(JButton button) {
        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (model.isRollover()) {
                // Effet de survol
                button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
                button.setBackground(new Color(200, 255, 200)); // Couleur de fond vert clair (à ajuster selon vos préférences)
            } else {
                // Retour à l'état normal
                // Create a rounded border and compound it with the existing line border
                Border roundedBorder = BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2);
                roundedBorder = BorderFactory.createCompoundBorder(
                    roundedBorder,
                    BorderFactory.createEmptyBorder(10, 10, 10, 10) // Adjust padding as needed
                );

                // Set the rounded border
                button.setBorder(roundedBorder);
                button.setBackground(BUTTON_BACKGROUND_COLOR);
                button.setForeground(BUTTON_FOREGROUND_COLOR);
            }
        });
    }


    // Get a unique color based on the player index
    private Color getUniqueColor(int playerIndex) {
        int red = playerIndex * 50 % 255;
        int green = playerIndex * 80 % 255;
        int blue = playerIndex * 120 % 255;
        return new Color(red, green, blue);
    }

    // Update the player's hand with clickable card buttons
    /**
     * Updates the player's hand with clickable card buttons.
     *
     * @param updatedHand The updated hand of the player.
     */
    public void updatePlayerHand(List<Card> updatedHand) {
        // Clear existing buttons
        cardButtons.clear();
        removeAll();

        // Calculate the number of rows and columns based on the number of cards
        int numCards = updatedHand.size();
        int columns = Math.min(numCards, 5); // Maximum number of columns
        int rows = (int) Math.ceil((double) numCards / columns);

        JPanel handPanel = new JPanel(new GridLayout(rows, columns)); // Dynamic layout

        for (int indexOfCard = 0; indexOfCard < updatedHand.size(); indexOfCard++) {
            Card card = updatedHand.get(indexOfCard);

            // Créez un JTextArea pour afficher tous les attributs de la carte
            JTextArea cardInfoArea = new JTextArea();
            cardInfoArea.setEditable(false);
            cardInfoArea.append("Name: " + card.getCardName() + "\n");
            cardInfoArea.append("Type: " + card.getInfo().getCardType() + "\n");
            cardInfoArea.append("Subtype: " + card.getInfo().getSubType() + "\n");
            cardInfoArea.append("Value: " + card.getInfo().getLevelBonus() + "\n");
            cardInfoArea.append("Desc.: " + card.getInfo().getDescription() + "\n");

            // Utilisez le nom de la carte pour construire le chemin de l'image
            String imagePath = "src/main/java/com/utmunchkin/gameplay/img/game/" + card.getCardName() + ".png";

            // Créez un JLabel pour afficher l'image
            JLabel cardImageLabel = new JLabel();
            cardImageLabel.setHorizontalAlignment(JLabel.CENTER); // Centre l'image

            // Vérifiez si le fichier image existe
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Chargez l'image depuis le chemin du fichier
                ImageIcon imageIcon = new ImageIcon(imagePath);

                // Redimensionnez l'image
                Image image = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedImageIcon = new ImageIcon(image);

                // Appliquez l'image redimensionnée au JLabel
                cardImageLabel.setIcon(resizedImageIcon);
            } else {
                // Chargez l'image par défaut si le fichier n'existe pas
                ImageIcon defaultImageIcon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/default.png");

                // Redimensionnez l'image par défaut
                Image defaultImage = defaultImageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedDefaultImageIcon = new ImageIcon(defaultImage);

                // Appliquez l'image redimensionnée par défaut au JLabel
                cardImageLabel.setIcon(resizedDefaultImageIcon);
            }

            JButton cardButton = new JButton();
            cardButton.setLayout(new BorderLayout());
            cardButton.add(cardInfoArea, BorderLayout.CENTER);
            cardButton.add(cardImageLabel, BorderLayout.SOUTH);

            // Create a rounded border and compound it with the existing line border
            Border roundedBorder = BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2);
            roundedBorder = BorderFactory.createCompoundBorder(
                roundedBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Adjust padding as needed
            );

            // Set the rounded border
            cardButton.setBorder(roundedBorder);
            cardButton.setBackground(BUTTON_BACKGROUND_COLOR);
            cardButton.setForeground(BUTTON_FOREGROUND_COLOR);
    
            addHoverEffect(cardButton);
            //cardButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            cardButtons.add(cardButton);
            handPanel.add(cardButton);
    
            cardButton.addActionListener(new CardButtonListener(indexOfCard, card));
        }
    
        JScrollPane scrollPane = new JScrollPane(handPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    
        
        add(scrollPane, BorderLayout.CENTER);
    
        // Update the border based on whether it's the turn of the current player
        updateBorder();
    
        revalidate(); // Refresh the layout
        repaint();    // Redraw the panel
    }


    // Get the image associated with the card (not implemented, customize as needed)
    /**
     * Gets the image associated with the card.
     *
     * @return The ImageIcon representing the card's image.
     */
    public ImageIcon getImage() {
        // Replace this with actual logic to get the card image
        return new ImageIcon("path/to/your/card/image.png");
    }

    // Update player statistics
    /**
     * Updates the player's statistics.
     *
     * @param name   The name of the player.
     * @param level  The level of the player.
     * @param lives  The remaining lives of the player.
     * @param money  The amount of money the player has.
     * @param curse  Indicates whether the player is cursed.
     */
    public void updateStats(String name, int level, int lives, double money, boolean curse) {
        setBorder(BorderFactory.createTitledBorder(name + " - Level: " + level + " - Lives: " + lives + " - Money: " + money + " - Curse: " + curse));

        revalidate(); // Refresh the layout
        repaint();    // Redraw the panel
    }

    // Update monster statistics
    /**
     * Updates the monster statistics.
     *
     * @param monsterStats The statistics of the monster.
     */
    public void updateMonsterStats(String monsterStats) {
        monsterStatsTextArea.setText(monsterStats);
    }

    // Update the panel border based on whether it's the turn of the current player
    /**
     * Updates the panel border based on whether it's the turn of the current player.
     */
    public void updateBorder() {
        if (isCurrentPlayer) {
            setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Customize the color and thickness of the border
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
    }

    // Get the player name
    /**
     * Gets the name of the player.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return this.playerName;
    }

    // Event handler for card buttons
    private class CardButtonListener implements ActionListener {
        private int cardIndex;  // Store the index of the clicked card
        private Card card;

        public CardButtonListener(int cardIndex, Card card) {
            this.cardIndex = cardIndex;
            this.card = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle logic when a card is clicked
            System.out.println("Card Clicked: " + card.getCardName());
            handleCardClick(cardIndex, card);  // Pass the index to a method for further processing
        }
    }

    private void handleCardClick(int cardIndex, Card card) {
        lastClickedCard = card;
        lastClickedCardIndex = cardIndex;
        cardClicked = true;  // Set the flag to true when a card is clicked
        // Add any other logic you want to perform when a card is clicked
    }

    // Reset the last clicked card information and the flag
    /**
     * Resets information about the last clicked card.
     */
    public static void resetLastClickedCard() {
        lastClickedCard = null;
        lastClickedCardIndex = -1;
        cardClicked = false;
    }

    // Add a method to check whether a card is clicked
    /**
     * Checks whether a card is clicked.
     *
     * @return True if a card is clicked, false otherwise.
     */
    public static boolean isCardClicked() {
        return cardClicked;
    }

    // Set whether the player is the current player
    /**
     * Sets whether the player is the current player.
     *
     * @param isCurrentPlayer True if the player is the current player, false otherwise.
     */
    public void setCurrentPlayer(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
    }

    public void enlargeFrame() {
        // Enlarge the frame (adjust the size as needed)
        setPreferredSize(new Dimension(800, 600));
    
        // Revalidate and repaint the panel
        revalidate();
        repaint();
    
        // Get the top-level ancestor (likely a JFrame)
        Container topLevelContainer = SwingUtilities.getWindowAncestor(this);
    
        // If the top-level container is a JFrame, pack it to resize
        if (topLevelContainer instanceof JPanel) {
            JPanel topPanel = (JPanel) topLevelContainer;
    
            // Set the new size directly to the frame
            topPanel.setSize(800, 600);
        }
    }

	public void updatePlayerInfo(String playerName, ListOfPlayer listOfPlayer, int playerIndex) {
        // Mettre à jour le nom du joueur
        this.playerName = playerName;
    
        // Mettre à jour la couleur d'arrière-plan en fonction de l'index du joueur
        setBackground(getUniqueColor(playerIndex));
    
        // Mettre à jour la liste des cartes dans la main du joueur
        List<Card> hand = listOfPlayer.getPlayer(playerIndex).getHand();
    
        // Mettre à jour les informations de chaque carte dans la main du joueur
        for (int indexOfCard = 0; indexOfCard < hand.size(); indexOfCard++) {
            Card card = hand.get(indexOfCard);
            JButton cardButton = cardButtons.get(indexOfCard);
    
            // Mettre à jour le JTextArea avec les nouvelles informations de la carte
            JTextArea cardInfoArea = (JTextArea) cardButton.getComponent(0); // Premier composant du JButton
            cardInfoArea.setText(""); // Effacer le texte actuel
            cardInfoArea.append("Name: " + card.getCardName() + "\n");
            cardInfoArea.append("Type: " + card.getInfo().getCardType() + "\n");
            cardInfoArea.append("Subtype: " + card.getInfo().getSubType() + "\n");
            cardInfoArea.append("Value: " + card.getInfo().getLevelBonus() + "\n");
            cardInfoArea.append("Desc.: " + card.getInfo().getDescription() + "\n");
            // Ajouter d'autres attributs de carte au besoin
    
            // Mettre à jour l'image de la carte
            JLabel cardImageLabel = (JLabel) cardButton.getComponent(1); // Deuxième composant du JButton
    
            String imagePath = "src/main/java/com/utmunchkin/gameplay/img/game/" + card.getCardName() + ".png";
            File imageFile = new File(imagePath);
    
            if (imageFile.exists()) {
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image image = imageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedImageIcon = new ImageIcon(image);
                cardImageLabel.setIcon(resizedImageIcon);
            } else {
                ImageIcon defaultImageIcon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/default.png");
                Image defaultImage = defaultImageIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon resizedDefaultImageIcon = new ImageIcon(defaultImage);
                cardImageLabel.setIcon(resizedDefaultImageIcon);
            }
        }
    
        // Rafraîchir l'interface graphique
        revalidate();
        repaint();
    }
    
        
    
}
