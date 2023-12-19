package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Interface.PlayerFrame;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.cards.Dungeon;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
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

    private JPanel deckPanel;

    private JPanel playerHandsPanel;
    private static JPanel playerStatsPanel;

    private Dungeon dungeonDeck;
    private Treasure treasureDeck;

    public Board(Play a, Dungeon d, Treasure t) {
        setTitle("Munchkin Game Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        resetUserInputLatch();

        this.game = a;
        this.dungeonDeck = d;
        this.treasureDeck = t;

        playerFrames = new ArrayList<>();

        infoTextArea = new JTextArea(10, 30);
        infoTextArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        add(infoScrollPane, BorderLayout.PAGE_END);

        deckPanel = new JPanel(new GridLayout(2, 2));
        playerHandsPanel = new JPanel(new GridLayout(1, 0));
        playerStatsPanel = new JPanel(new GridLayout(1, 2));
        playerStatsPanel.setBackground(Color.BLACK); // Set the background color of playerStatsPanel to black

        deckPanel.setBorder(BorderFactory.createTitledBorder("Deck & Discard"));
        playerHandsPanel.setBorder(BorderFactory.createTitledBorder("Main du Joueur"));
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques du Joueur"));

        add(deckPanel, BorderLayout.EAST);
        add(playerHandsPanel, BorderLayout.CENTER);
        add(playerStatsPanel, BorderLayout.PAGE_START);

        initializeDrawButtons(deckPanel);
        initializePlayerFrames(playerHandsPanel);
        initializeStatsPanel();

        // Set the size of the JFrame to the screen size
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        setSize(screenWidth, screenHeight);

        // Center the JFrame on the screen
        setLocationRelativeTo(null);

        setVisible(true);

        updatePlayerStats(game.getPlayers());
    }

    // Function to initialize the player statistics panel
    private static void initializeStatsPanel() {

        // Add headers for player statistics
        JLabel nameLabel = new JLabel("Name", JLabel.LEFT);
        JLabel levelLabel = new JLabel("Level", JLabel.LEFT);
        JLabel livesLabel = new JLabel("Lives", JLabel.LEFT);
        JLabel moneyLabel = new JLabel("Money", JLabel.LEFT);
        JLabel curseLabel = new JLabel("Curse", JLabel.LEFT);
        JLabel defenseLabel = new JLabel("Defense", JLabel.LEFT);
        JLabel equipLabel = new JLabel("Equip.", JLabel.LEFT);
        JLabel attackLabel = new JLabel("Attack", JLabel.LEFT);

        playerStatsPanel.add(nameLabel);
        playerStatsPanel.add(levelLabel);
        playerStatsPanel.add(livesLabel);
        playerStatsPanel.add(moneyLabel);
        playerStatsPanel.add(curseLabel);
        playerStatsPanel.add(defenseLabel);
        playerStatsPanel.add(equipLabel);
        playerStatsPanel.add(attackLabel);

        // Add any other statistics headers you want to display

        // Set the background color of all JLabel components in playerStatsPanel to black
        Arrays.stream(playerStatsPanel.getComponents())
                .filter(component -> component instanceof JLabel)
                .map(component -> (JLabel) component)
                .forEach(label -> label.setBackground(Color.BLACK));
    }

    // Function to update the player statistics panel based on the current player
    public static void updatePlayerStatsPanel(Player currentPlayer) {
        // Clear the player statistics panel before updating
        playerStatsPanel.removeAll();

        // Add the player's information to the panel
        addStatWithImage("Name", currentPlayer.getName(), "src/main/java/com/utmunchkin/gameplay/img/stats/name.png");
        addStatWithImage("Level", String.valueOf(currentPlayer.getLevel()), "src/main/java/com/utmunchkin/gameplay/img/stats/level.png");
        addStatWithImage("Lives", String.valueOf(currentPlayer.getLives()), "src/main/java/com/utmunchkin/gameplay/img/stats/lives.png");
        addStatWithImage("Money", String.valueOf(currentPlayer.getMoney()), "src/main/java/com/utmunchkin/gameplay/img/stats/money.png");
        addStatWithImage("Curse", String.valueOf(currentPlayer.getCurse()), "src/main/java/com/utmunchkin/gameplay/img/stats/curse.png");
        addStatWithImage("Defense", String.valueOf(currentPlayer.getDefense()) , "src/main/java/com/utmunchkin/gameplay/img/stats/defense.png");
        addStatWithImage("Equip.", String.valueOf(currentPlayer.getEquippedObjectsNames()) , "src/main/java/com/utmunchkin/gameplay/img/stats/equipement.png");
        addStatWithImage("Attack", String.valueOf(currentPlayer.getAttackForce()) , "src/main/java/com/utmunchkin/gameplay/img/stats/attack.png");

        // Add any other statistics you want to display

        // Set the background color of all JLabel components in playerStatsPanel to black
        Arrays.stream(playerStatsPanel.getComponents())
                .filter(component -> component instanceof JLabel)
                .map(component -> (JLabel) component)
                .forEach(label -> label.setBackground(Color.BLACK));

        // Repaint the panel to reflect the changes
        playerStatsPanel.revalidate();
        playerStatsPanel.repaint();
    }

    // Function to add a JLabel with an image to the player statistics panel
    private static void addStatWithImage(String statName, String statValue, String imagePath) {
        // Create a JPanel to hold both the stat label and image
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create a JLabel for the stat
        JLabel statLabel = new JLabel(statName + ": " + statValue, JLabel.LEFT);

        // Create an ImageIcon for the image
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);

        // Create a JLabel for the image
        JLabel imageLabel = new JLabel(imageIcon);

        // Add the stat label and image label to the stat panel
        statPanel.add(statLabel);
        statPanel.add(imageLabel);

        // Add the stat panel to the playerStatsPanel
        playerStatsPanel.add(statPanel);
    }

    private void initializeDrawButtons(JPanel panel) {
        // Initialize buttons first with the current size of each deck
        drawDungeonButton = new JButton("Dungeon Deck: " + dungeonDeck.getDeckPile().size());
        drawTreasureButton = new JButton("Treasure Deck: " + treasureDeck.getDeckPile().size());

        // Create initial icons
        int initialButtonWidth = 100; // Adjust as needed
        int initialButtonHeight = 100; // Adjust as needed
        ImageIcon dunIcon = new ImageIcon(createResizedIcon("src/main/java/com/utmunchkin/gameplay/img/dungeon.png", initialButtonWidth, initialButtonHeight));
        ImageIcon treIcon = new ImageIcon(createResizedIcon("src/main/java/com/utmunchkin/gameplay/img/treasure.png", initialButtonWidth, initialButtonHeight));

        // Set icons for buttons
        drawDungeonButton.setIcon(dunIcon);
        drawTreasureButton.setIcon(treIcon);

        // Add action listeners
        drawDungeonButton.addActionListener(e -> {
            // Set a flag to indicate that the user wants to draw a dungeon card
             Play.getCurrentPlayer().setDrawDungeon(true);
        });

        drawTreasureButton.addActionListener(e -> {
            Play.getCurrentPlayer().setDrawTreasure(true);
        });

        // Add hover effects
        addHoverEffect(drawDungeonButton);
        addHoverEffect(drawTreasureButton);

        // Set layout manager for the panel
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        // Add buttons to the panel
        panel.add(drawDungeonButton);
        panel.add(drawTreasureButton);


    }

    // Update the text of the buttons with the current size of each deck
    public void updateDeckSizes() {
        drawDungeonButton.setText("Dungeon Deck: " + dungeonDeck.getDeckPile().size());
        drawTreasureButton.setText("Treasure Deck: " + treasureDeck.getDeckPile().size());
    }

    private Image createResizedIcon(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();

        // Ensure that width and height are positive
        width = Math.max(1, width);
        height = Math.max(1, height);

        // Create a BufferedImage to allow smoother scaling
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();

        return bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
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
                // Changer la couleur du fond et du texte lors du survol
                button.setBackground(Color.BLUE);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(Color.BLUE));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Retour à l'état normal
                button.setBackground(UIManager.getColor("Button.background"));
                button.setForeground(UIManager.getColor("Button.foreground"));
                button.setBorder(BorderFactory.createEmptyBorder());
            }
        });
    }



    public void showPlayerStats(Player player) {
    }



    public List<PlayerFrame> getPlayerFrames(){
        return this.playerFrames;
    }
}
