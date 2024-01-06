package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Interact.InstructionDialog;
import main.java.com.utmunchkin.cards.Dungeon;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.Ally;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Board extends JFrame {

    private List<PlayerFrame> playerFrames;

    private static ListOfPlayer listOfPlayer;
    private static JLabel mainInfoArea;
    private static JPanel playerStatsPanel;


    private JButton drawDungeonButton;
    private JButton drawTreasureButton;
    private JLabel discardDungeonLabel;
    private JLabel discardTreasureLabel;
    private JTextArea infoTextArea;
    
    private JPanel deckPanel;
    private JPanel otherAttributesPanel;
    private JPanel mapPanel;
    private JPanel playersPanel;
    private JPanel playerHandsPanel;

    private CountDownLatch userInputLatch;

    private boolean playFlag = false;
    private int choice;

    
    // Create and add the MapPanel to the center of the mapPanel
    MapPanel gameMap;
    Font font, titleFont, generalFont;

    


    /**
     * Constructs a Board object with the specified parameters.
     * 
     */
    public Board() {

        // Initialize the main menu directly in the constructor
        initializeMainMenu();

        // JFrame initialization
        setTitle("Munchkin Game Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        resetUserInputLatch();

        titleFont = loadFontFromFile(Constant.FONT_PATH_3, Font.PLAIN, 40);
        font = loadFontFromFile(Constant.FONT_PATH_3, Font.PLAIN, 25);
        generalFont = loadFontFromFile(Constant.FONT_PATH_3, Font.PLAIN, 35);

        // Ajouter un KeyListener pour la touche "Échap"
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    int choice = JOptionPane.showConfirmDialog(Board.this,
                            "Voulez-vous vraiment quitter le jeu ?", "Quitter le jeu",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (choice == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });
        
        waitForPlayButton();

        // Game and deck initialization
        this.gameMap = new MapPanel();
        playerFrames = new ArrayList<>();
        // Change this line to create a new instance of ListOfPlayer
        listOfPlayer = Play.getPlayers();
        System.out.println(listOfPlayer);

        // JTextArea for game info
        infoTextArea = new JTextArea(10, 30);
        infoTextArea.setEditable(false);
        infoTextArea.setBackground(Constant.INFO_TEXT_AREA_BACKGROUND_COLOR);
        infoTextArea.setForeground(Constant.INFO_TEXT_AREA_TEXT_COLOR);
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);

        // JLabel for main info
        mainInfoArea = new JLabel();
        mainInfoArea.setForeground(Color.RED);
        mainInfoArea.setBackground(Constant.INFO_TEXT_AREA_BACKGROUND_COLOR);
        mainInfoArea.setOpaque(true);
        mainInfoArea.setHorizontalAlignment(JLabel.CENTER);
        setFont(mainInfoArea, titleFont);

        // JPanel for different sections
        deckPanel = new JPanel(new GridLayout(2, 2));
        playersPanel = new JPanel();
        playerHandsPanel = new JPanel(new GridLayout(1, 0));
        playerStatsPanel = new JPanel(new GridLayout(1, 2));
        mapPanel = new JPanel();
        otherAttributesPanel = new JPanel();


        // Setting borders and backgrounds
        setBordersAndBackgrounds();

        // Create and organize panels
        organizePanels(infoScrollPane);

        // Initialize draw buttons, player frames, and player stats panel
        initializeDrawButtons(deckPanel);
        initializePlayerFrames(playerHandsPanel);
        initializeStatsPanel();

        initializePlayersPanel();
        initializeMapPanel();
        //initializeOtherAttributesPanel();

        // Set the size of the JFrame to the screen size and center it
        setSizeAndCenter();

        // Apply rounded corners and other effects to panels and buttons
        applyAestheticEffects();

        // Enable fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setUndecorated(true);

        // Make the JFrame visible
        setVisible(true);

        // Afficher un message d'avertissement au lancement
        /*
        JOptionPane.showMessageDialog(this,
        "Vous pouvez quitter le jeu à tout moment en appuyant sur la touche Échap.",
        "Avertissement", JOptionPane.INFORMATION_MESSAGE);
        */

        // Update player stats
        updatePlayerStats(Play.getPlayers());
    }


    //Main Menu-----------------------------------------------------------------
    //--

    /**
     * Initializes the main menu directly in the Board class.
     */
    private void initializeMainMenu() {
        // Create the main menu window
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Create a panel to organize components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        loadSettingsFromFile();

        // Play button
        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                setPlayFlag(true);
                // Execute code when the "Play" button is clicked
                //startGame();
            }
        });

        // Options button
        JButton optionsButton = new JButton("Options");
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Execute code when the "Options" button is clicked
                //JOptionPane.showMessageDialog(frame, "Access options.");

                settings();
            }
        });

        // SaveData button
        JButton recordsButton = new JButton("Data");
        recordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Execute code when the "Records" button is clicked
                //JOptionPane.showMessageDialog(frame, "View records.");
                viewData();
            }
        });

        setFont(playButton, generalFont);
        setFont(optionsButton, generalFont);
        setFont(recordsButton, generalFont);
        playButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR2);
        optionsButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR2);
        recordsButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR2);


        // Add buttons to the panel
        panel.add(playButton);
        panel.add(optionsButton);
        panel.add(recordsButton);

        // Add the panel to the window
        frame.add(panel);

        // Center the window
        frame.setLocationRelativeTo(null);

        // Make the window visible
        frame.setVisible(true);
    }


    //Settings and Components --------------------------------------------------

    /**
     * Displays the settings dialog and allows users to adjust game parameters.
     */
    public void settings() {
        // Create the settings dialog
        JDialog settingsDialog = new JDialog(this, "Game Settings", true);
        settingsDialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Create panels to organize components
        JPanel colorPickersPanel = createColorPickersPanel();
        JPanel numericSlidersPanel = createNumericSlidersPanel();
        JPanel switchPanel = createSwitchPanel();

        // Add the panels to the settings dialog
        settingsDialog.add(colorPickersPanel);
        settingsDialog.add(numericSlidersPanel);
        settingsDialog.add(switchPanel);

        // Save and Cancel buttons
        JButton saveButton = createStyledButton("Save Settings", Color.GREEN);
        saveButton.addActionListener(e -> saveSettingsToFile());
        JButton cancelButton = createStyledButton("Cancel", Color.RED);
        cancelButton.addActionListener(e -> settingsDialog.dispose());

        // Create a panel for buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        // Add the buttons panel to the settings dialog
        settingsDialog.add(buttonsPanel);

        // Set the size, make it visible, and wait for the user to close the dialog
        settingsDialog.setSize(1000, 1000);  // Adjust the size as needed
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    private JPanel createColorPickersPanel() {
        JPanel colorPickersPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Add a color picker for PLAYER_STATS_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Player Stats Background Color", Constant.PLAYER_STATS_BACKGROUND_COLOR);
    
        // Add a color picker for PANEL_BORDER_COLOR
        addColorPicker(colorPickersPanel, "Panel Border Color", Constant.PANEL_BORDER_COLOR);
    
        // Add a color picker for INFO_TEXT_AREA_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Info Text Area Background Color", Constant.INFO_TEXT_AREA_BACKGROUND_COLOR);
    
        // Add a color picker for INFO_TEXT_AREA_TEXT_COLOR
        addColorPicker(colorPickersPanel, "Info Text Area Text Color", Constant.INFO_TEXT_AREA_TEXT_COLOR);
    
        // Add a color picker for DECK_PANEL_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Deck Panel Background Color", Constant.DECK_PANEL_BACKGROUND_COLOR);
    
        // Add a color picker for SHOP_PANEL_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Shop Panel Background Color", Constant.SHOP_PANEL_BACKGROUND_COLOR);
    
        // Add a color picker for PLAYER_HAND_PANEL_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Player Hand Panel Background Color", Constant.PLAYER_HAND_PANEL_BACKGROUND_COLOR);
    
        // Add a color picker for DRAW_BUTTON_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Draw Button Background Color", Constant.DRAW_BUTTON_BACKGROUND_COLOR);
    
        // Add a color picker for DRAW_BUTTON_BACKGROUND_COLOR2
        addColorPicker(colorPickersPanel, "Draw Button Background Color 2", Constant.DRAW_BUTTON_BACKGROUND_COLOR2);
    
        // Add a color picker for DRAW_BUTTON_FOREGROUND_COLOR
        addColorPicker(colorPickersPanel, "Draw Button Foreground Color", Constant.DRAW_BUTTON_FOREGROUND_COLOR);
    
        // Add a color picker for PLAYER_FRAME_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Player Frame Background Color", Constant.PLAYER_FRAME_BACKGROUND_COLOR);
    
        // Add a color picker for BORDER_COLOR_1
        addColorPicker(colorPickersPanel, "Border Color 1", Constant.BORDER_COLOR_1);
    
        // Add a color picker for BORDER_COLOR_2
        addColorPicker(colorPickersPanel, "Border Color 2", Constant.BORDER_COLOR_2);
    
        // Add a color picker for BACKGROUND_COLOR_1
        addColorPicker(colorPickersPanel, "Background Color 1", Constant.BACKGROUND_COLOR_1);
    
        // Add a color picker for BACKGROUND_COLOR_2
        addColorPicker(colorPickersPanel, "Background Color 2", Constant.BACKGROUND_COLOR_2);
    
        // Add a color picker for CARD_BUTTON_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Card Button Background Color", Constant.CARD_BUTTON_BACKGROUND_COLOR);
    
        // Add a color picker for CARD_BUTTON_FOREGROUND_COLOR
        addColorPicker(colorPickersPanel, "Card Button Foreground Color", Constant.CARD_BUTTON_FOREGROUND_COLOR);
    
        // Add a color picker for CARD_BUTTON_BORDER_COLOR
        addColorPicker(colorPickersPanel, "Card Button Border Color", Constant.CARD_BUTTON_BORDER_COLOR);
    
        // Add a color picker for MAP_GRID_VOID_CELL_COLOR
        addColorPicker(colorPickersPanel, "Map Grid Void Cell Color", Constant.MAP_GRID_VOID_CELL_COLOR);
    
        // Add a color picker for MAP_PANEL_BACKGROUND_COLOR
        addColorPicker(colorPickersPanel, "Map Panel Background Color", Constant.MAP_PANEL_BACKGROUND_COLOR);
  

        return colorPickersPanel;
    }

    private JPanel createNumericSlidersPanel() {
        JPanel numericSlidersPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Add sliders for numeric values
        addNumericSlider(numericSlidersPanel, "Max Hand Size", Constant.MAX_HAND_SIZE);
        addNumericSlider(numericSlidersPanel, "Max Initial Players Lives", Constant.MAX_INITIAL_PLAYERS_LIVES);
        addNumericSlider(numericSlidersPanel, "Max Initial Players Money", Constant.MAX_INITIAL_PLAYERS_MONEY);
        // Add more sliders for other numeric constants as needed

        return numericSlidersPanel;
    }

    private JPanel createSwitchPanel() {
        JPanel switchPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Add switches for boolean flags
        addSwitch(switchPanel, "Feature 1", Constant.FEATURE_1);
        addSwitch(switchPanel, "Feature 2", Constant.FEATURE_2);
        // Add more switches for other features as needed

        return switchPanel;
    }

    private JButton createStyledButton(String label, Color color) {
        JButton button = new JButton(label);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorderPainted(false);

        return button;
    }
    
    // Add a method to create sliders for numeric values
    private void addNumericSlider(JPanel numericSlidersPanel, String label, int initialValue) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
        JLabel numericLabel = new JLabel(label);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, initialValue);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
    
        panel.add(numericLabel);
        panel.add(slider);
    
        numericSlidersPanel.add(panel);
    }
    
    // Add a method to create switches for boolean flags
    private void addSwitch(JPanel switchPanel, String label, boolean initialValue) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    
        JLabel switchLabel = new JLabel(label);
        JCheckBox switchCheckBox = new JCheckBox();
        switchCheckBox.setSelected(initialValue);
    
        panel.add(switchLabel);
        panel.add(switchCheckBox);
    
        switchPanel.add(panel);
    }    

    private void addColorPicker(JPanel colorPickersPanel, String label, Color initialColor) {
        JButton colorButton = new JButton(label);
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(colorPickersPanel, "Choose Color", initialColor);
            if (selectedColor != null) {
                // Update the corresponding constant
                // You need to add the appropriate setter methods in the Constant class for each color constant
                if (label.equals("Player Stats Background Color")) {
                    Constant.PLAYER_STATS_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Panel Border Color")) {
                    Constant.PANEL_BORDER_COLOR = selectedColor;
                } else if (label.equals("Info Text Area Background Color")) {
                    Constant.INFO_TEXT_AREA_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Info Text Area Text Color")) {
                    Constant.INFO_TEXT_AREA_TEXT_COLOR = selectedColor;
                } else if (label.equals("Deck Panel Background Color")) {
                    Constant.DECK_PANEL_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Shop Panel Background Color")) {
                    Constant.SHOP_PANEL_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Player Hand Panel Background Color")) {
                    Constant.PLAYER_HAND_PANEL_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Draw Button Background Color")) {
                    Constant.DRAW_BUTTON_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Draw Button Background Color 2")) {
                    Constant.DRAW_BUTTON_BACKGROUND_COLOR2 = selectedColor;
                } else if (label.equals("Draw Button Foreground Color")) {
                    Constant.DRAW_BUTTON_FOREGROUND_COLOR = selectedColor;
                } else if (label.equals("Player Frame Background Color")) {
                    Constant.PLAYER_FRAME_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Border Color 1")) {
                    Constant.BORDER_COLOR_1 = selectedColor;
                } else if (label.equals("Border Color 2")) {
                    Constant.BORDER_COLOR_2 = selectedColor;
                } else if (label.equals("Background Color 1")) {
                    Constant.BACKGROUND_COLOR_1 = selectedColor;
                } else if (label.equals("Background Color 2")) {
                    Constant.BACKGROUND_COLOR_2 = selectedColor;
                } else if (label.equals("Card Button Background Color")) {
                    Constant.CARD_BUTTON_BACKGROUND_COLOR = selectedColor;
                } else if (label.equals("Card Button Foreground Color")) {
                    Constant.CARD_BUTTON_FOREGROUND_COLOR = selectedColor;
                } else if (label.equals("Card Button Border Color")) {
                    Constant.CARD_BUTTON_BORDER_COLOR = selectedColor;
                } else if (label.equals("Map Grid Void Cell Color")) {
                    Constant.MAP_GRID_VOID_CELL_COLOR = selectedColor;
                } else if (label.equals("Map Panel Background Color")) {
                    Constant.MAP_PANEL_BACKGROUND_COLOR = selectedColor;
                }
            }
        });
        colorPickersPanel.add(colorButton);
    }

    private void addNumericTextField(JDialog settingsDialog, String label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel numericLabel = new JLabel(label);
        panel.add(numericLabel);

        textField.setColumns(5);  // Adjust the column count as needed
        panel.add(textField);

        settingsDialog.add(panel);
    }



    //Data and Settings-------------------------------------------------------

    /**
     * Saves the current game settings to a text file.
     */
    private void saveSettingsToFile() {
        try (OutputStream output = new FileOutputStream("settings.properties")) {
            Properties properties = new Properties();
    
            // Obtenez tous les champs dans la classe Constant
            Field[] fields = Constant.class.getDeclaredFields();
    
            // Itérez sur les champs
            for (Field field : fields) {
                String fieldName = field.getName();
                Object fieldValue = field.get(null);
    
                // Ajoutez la propriété uniquement si elle est de type Color ou int
                if ((fieldValue instanceof Color) || (fieldValue instanceof Integer)) {
                    // Construisez le nom de la propriété
                    String propertyKey = fieldName;
    
                    if (fieldValue instanceof Color) {
                        propertyKey += "_R";
                        properties.setProperty(propertyKey, String.valueOf(((Color) fieldValue).getRed()));
    
                        propertyKey = fieldName + "_G";
                        properties.setProperty(propertyKey, String.valueOf(((Color) fieldValue).getGreen()));
    
                        propertyKey = fieldName + "_B";
                        properties.setProperty(propertyKey, String.valueOf(((Color) fieldValue).getBlue()));
                    } else {
                        properties.setProperty(propertyKey, fieldValue.toString());
                    }
                }
            }
    
            // Enregistrez les propriétés dans le fichier
            properties.store(output, null);
    
            // Informez l'utilisateur que les paramètres ont été enregistrés avec succès
            JOptionPane.showMessageDialog(this, "Settings saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving settings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSettingsFromFile() {

        try (InputStream input = new FileInputStream("settings.properties")) {
            Properties properties = new Properties();
    
            // Load properties from the file
            properties.load(input);
    
            // Get all fields in the Constant class
            Field[] fields = Constant.class.getDeclaredFields();
    
            // Iterate over the fields
            for (Field field : fields) {
                if (field.getType() == Color.class || field.getType() == int.class) {
                    String fieldName = field.getName();
                    String propertyValue;
            
                    if (field.getType() == Color.class) {
                        if(properties.getProperty(fieldName + "_R") != null && 
                            properties.getProperty(fieldName + "_G") != null && 
                            properties.getProperty(fieldName + "_B") != null){
                            int red = Integer.parseInt(properties.getProperty(fieldName + "_R"));
                            int green = Integer.parseInt(properties.getProperty(fieldName + "_G"));
                            int blue = Integer.parseInt(properties.getProperty(fieldName + "_B"));
                            field.set(null, new Color(red, green, blue));
                        }
                    } else if (field.getType() == int.class) {
                        propertyValue = properties.getProperty(fieldName);
                        if (propertyValue != null) {
                            field.set(null, Integer.parseInt(propertyValue));
                        }
                    }
                }
            }
    
            // Inform the user that settings have been loaded
            //JOptionPane.showMessageDialog(this, "Settings loaded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading settings. Using default settings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("savedGameData.ser"))) {
            oos.writeObject(listOfPlayer);
            oos.writeObject(Play.getPlayers());
            // Add other objects to serialize as needed

            JOptionPane.showMessageDialog(this, "Game data saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving game data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savedGameData.ser"))) {
            ListOfPlayer loadedListOfPlayer = (ListOfPlayer) ois.readObject();
            ListOfPlayer loadedPlayers = (ListOfPlayer) ois.readObject();
            // Load other objects as needed

            // Display the loaded data
            displayLoadedData(loadedListOfPlayer, loadedPlayers);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading game data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayLoadedData(ListOfPlayer loadedListOfPlayer, ListOfPlayer loadedPlayers) {
        JFrame loadedDataFrame = new JFrame("Loaded Game Data");
        loadedDataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loadedDataFrame.setSize(400, 200);
    
        // Create a panel to organize components
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));
    
        // Add labels or other components to display loaded data
        JLabel loadedPlayersLabel = new JLabel("Loaded Players: " + loadedPlayers);
        JLabel loadedListOfPlayerLabel = new JLabel("Loaded ListOfPlayer: " + loadedListOfPlayer);
        // Add other labels as needed
    
        panel.add(loadedPlayersLabel);
        panel.add(loadedListOfPlayerLabel);
        // Add other components to the panel
    
        // Add the panel to the window
        loadedDataFrame.add(panel);
    
        // Center the window
        loadedDataFrame.setLocationRelativeTo(null);
    
        // Make the window visible
        loadedDataFrame.setVisible(true);
    }
    


    //Creation JComponents--------------------------------------------------

    /*Top*/

    /**
     * Creates the top panel containing main info and player stats.
     *
     * @return The top JPanel.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(mainInfoArea, BorderLayout.PAGE_START);
        topPanel.add(playerStatsPanel, BorderLayout.PAGE_END);
        return topPanel;
    }

    /*Middle*/

    /**
     * Creates the middle panel containing player hands.
     *
     * @return The middle JPanel.
     */
    private JPanel createMiddlePanel() {
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.add(playerHandsPanel, BorderLayout.WEST);
        return middlePanel;
    }

    /*Bottom*/

    /**
     * Creates the bottom panel containing game info, map, and other attributes.
     *
     * @param infoScrollPane JScrollPane for game info text area.
     * @return The bottom JPanel.
     */
    private JPanel createBottomPanel(JScrollPane infoScrollPane) {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.add(infoScrollPane, BorderLayout.WEST);
        bottomPanel.add(mapPanel, BorderLayout.CENTER);
        bottomPanel.add(otherAttributesPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    /*---------------------Panels-------------------*/

    /**
     * Organizes different panels in the main layout.
     *
     * @param infoScrollPane JScrollPane for game info text area.
     */
    private void organizePanels(JScrollPane infoScrollPane) {
        // Create panels to organize the layout
        JPanel topPanel = createTopPanel();
        JPanel middlePanel = createMiddlePanel();
        JPanel bottomPanel = createBottomPanel(infoScrollPane);

        // Add panels to the main container
        JButton settingsButton = new JButton("Settings");
        settingsButton.setBackground(Constant.BUTTON_COLOR_1);
        settingsButton.addActionListener(e -> settings());

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(Constant.BUTTON_COLOR_1);
        saveButton.addActionListener(e -> save());

        topPanel.add(playersPanel, BorderLayout.CENTER);
        topPanel.add(settingsButton, BorderLayout.EAST);
        topPanel.add(saveButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.PAGE_START);

        add(middlePanel, BorderLayout.CENTER);

        add(deckPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.PAGE_END);
    }



    //Setters---------------------------------------------------------------

    /**
     * Sets borders and backgrounds for various panels.
     */
    private void setBordersAndBackgrounds() {
        deckPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR), "Deck & Discard"));
        playersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR), "Players"));
        playerHandsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR), "Main du Joueur"));
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR), "Statistiques du Joueur"));
        otherAttributesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR), "Equipement"));

        deckPanel.setBackground(Constant.DECK_PANEL_BACKGROUND_COLOR);
        playersPanel.setBackground(Constant.SHOP_PANEL_BACKGROUND_COLOR);
        playerHandsPanel.setBackground(Constant.PLAYER_HAND_PANEL_BACKGROUND_COLOR);
        playerStatsPanel.setBackground(Constant.PLAYER_STATS_BACKGROUND_COLOR);
        otherAttributesPanel.setBackground(Constant.DECK_PANEL_BACKGROUND_COLOR);
    }


    /**
     * Sets the size of the JFrame to the screen size and centers it.
     */
    private void setSizeAndCenter() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);
    }

    /**
     * Sets the text of the main information label.
     *
     * @param text The text to set.
     */
    public static void setMainInfoText(String text) {
        mainInfoArea.setText(text);
    }
    

    
    //Image - Icon-------------------------------------------------------

    /**
     * Creates a resized icon from the given image path.
     *
     * @param imagePath The path to the image.
     * @param width     The width of the resized icon.
     * @param height    The height of the resized icon.
     * @return The resized icon.
     */
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

    /**
     * Adds a JLabel with an image to the player statistics panel.
     *
     * @param statName The name of the statistic.
     * @param statValue The value of the statistic.
     * @param imagePath The path to the image.
     */
    private static void addStatWithImage(String statName, String statValue, String imagePath) {
        // Create a JPanel to hold both the stat label and image
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create a JLabel for the stat
        JLabel statLabel = createStatLabel(statName + ": " + statValue);

        // Create an ImageIcon for the image
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(image);

        // Create a JLabel for the image
        JLabel imageLabel = new JLabel(imageIcon);

        // Add the stat label and image label to the stat panel
        statPanel.add(statLabel);
        statPanel.add(imageLabel);

        statPanel.setBackground(Constant.PLAYER_STATS_BACKGROUND_COLOR);

        // Add the stat panel to the playerStatsPanel
        playerStatsPanel.add(statPanel);
    }
    

    //Initialization Methods---------------------------------------------

    // Function to initialize the player statistics panel
    private static void initializeStatsPanel() {

        // Add headers for player statistics
        JLabel nameLabel = createStatLabel("Name");
        JLabel scoreLabel = createStatLabel("Score");
        JLabel levelLabel = createStatLabel("Level");
        JLabel livesLabel = createStatLabel("Lives");
        JLabel moneyLabel = createStatLabel("Money");
        JLabel curseLabel = createStatLabel("Curse");
        JLabel defenseLabel = createStatLabel("Defense");
        JLabel equipLabel = createStatLabel("Equip.");
        JLabel attackLabel = createStatLabel("Attack");

        playerStatsPanel.add(nameLabel);
        playerStatsPanel.add(scoreLabel);
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
                .forEach(label -> label.setOpaque(true));
    }

    /**
     * Creates a styled JLabel for displaying statistics.
     *
     * @param text The text to be displayed.
     * @return The styled JLabel.
     */
    private static JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text, JLabel.LEFT);
        label.setForeground(Color.WHITE); // Set the text color
        label.setBorder(BorderFactory.createLineBorder(Constant.PANEL_BORDER_COLOR, 1, true)); // Add a white border with rounded corners
        label.setBackground(new Color(0, 0, 0, 150)); // Set a transparent black background
        label.setOpaque(true); // Make sure it's opaque to show the background color

        // Set the font size and style (adjust as needed)
        label.setFont(new Font("Arial", Font.BOLD, 12));

        return label;
    }


    private void initializePlayersPanel() {
        // JPanel for player interactions
        playersPanel.setLayout(new GridLayout(1, 0));

        // Add buttons for player interactions
        for (Player player : listOfPlayer.getPlayers()) {

            if(player.getName() == Play.curPlayer.getName()){
                JButton helpButton = new JButton("Go Alone ->");
                helpButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR);
                setFont(helpButton, font);
                helpButton.addActionListener(e -> Ally.requestHelp(player.getMe()));
                playersPanel.add(helpButton);
            }else{
                JButton helpButton = new JButton("Enroll - " + player.getName() + "  " + Constant.PRICE_FOR_REQUESTING_HELP + " money");
                helpButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR2);
                setFont(helpButton, font);
                helpButton.addActionListener(e -> Ally.requestHelp(player.getMe()));
                playersPanel.add(helpButton);
            }
        }
    }

    /**
     * Initializes the panel for displaying player's equipped items and inventory.
     */
    private void initializeOtherAttributesPanel() {
        // JPanel for displaying player's equipped items and inventory
        otherAttributesPanel.setLayout(new GridLayout(2, 1));

        // Add labels or other components to represent player's attributes, inventory, etc.
        JLabel equippedItemsLabel = new JLabel("Objets Equipés : ");
        JLabel inventoryLabel = new JLabel("Inventaire : ");

        // You can customize and add more components based on your game logic
        // For example, add labels or panels to represent equipped items and inventory.

        otherAttributesPanel.add(equippedItemsLabel);
        otherAttributesPanel.add(inventoryLabel);
    }

    /**
     * Initializes the draw buttons for dungeon and treasure decks.
     *
     * @param panel The panel to which buttons are added.
     */
    private void initializeDrawButtons(JPanel panel) {

        // Initialize buttons first with the current size of each deck
        drawDungeonButton = new JButton("Dungeon Deck: " + Dungeon.getDeckPile().size());
        discardDungeonLabel = new JLabel("Dungeon Discard: " + Dungeon.getDiscardPile().size());
        drawTreasureButton = new JButton("Treasure Deck: " + Treasure.getDeckPile().size());
        discardTreasureLabel = new JLabel("Treasure Discard: " + Treasure.getDiscardPile().size());
    
        setFont(drawDungeonButton, font);
        setFont(drawTreasureButton, font);
        setFont(discardDungeonLabel, font);
        setFont(discardTreasureLabel, font);
    
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
        panel.setLayout(new GridLayout(2, 2, 20, 20)); // Adjust the gap between components as needed
    
        // Add buttons to the panel with background and foreground colors
        drawDungeonButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR);
        drawDungeonButton.setForeground(Constant.DRAW_BUTTON_FOREGROUND_COLOR);
        panel.add(drawDungeonButton);
    
        drawTreasureButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR);
        drawTreasureButton.setForeground(Constant.DRAW_BUTTON_FOREGROUND_COLOR);
        panel.add(drawTreasureButton);
    
        discardDungeonLabel.setBackground(Constant.DECK_PANEL_BACKGROUND_COLOR);
        discardDungeonLabel.setForeground(Constant.INFO_TEXT_AREA_TEXT_COLOR);
        panel.add(discardDungeonLabel);
    
        discardTreasureLabel.setBackground(Constant.DECK_PANEL_BACKGROUND_COLOR);
        discardTreasureLabel.setForeground(Constant.INFO_TEXT_AREA_TEXT_COLOR);
        panel.add(discardTreasureLabel);
    }
    


    /**
     * Initializes the player frames based on the game state.
     *
     * @param panel The panel to which player frames are added.
     */
    private void initializePlayerFrames(JPanel panel) {
        //panel.removeAll();

        int numPlayers = Play.getPlayers().getSize();
        int columns = Math.min(numPlayers, 3); // Maximum number of columns for layout
        int rows = (int) Math.ceil((double) numPlayers / columns);

        panel.setLayout(new GridLayout(rows, columns, 10, 10)); // Adjust the gap as needed

        for (int i = 0; i < numPlayers; i++) {
            PlayerFrame playerFrame = new PlayerFrame(Play.getPlayers().getPlayer(i).getName(), Play.getPlayers(), i);

            // Set the background color of the PlayerFrame
            playerFrame.setBackground(Constant.PLAYER_FRAME_BACKGROUND_COLOR);

            // Set the preferred size of the PlayerFrame based on the game window size
            int preferredWidth = getWidth() / columns - 20; // Adjust the spacing
            int preferredHeight = getHeight() / rows - 20; // Adjust the spacing
            playerFrame.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

            // Appelez la méthode updatePlayerInfo pour mettre à jour les informations du joueur
            playerFrame.updatePlayerInfo(Play.getPlayers().getPlayer(i).getName(), Play.getPlayers(), i);

            playerFrames.add(playerFrame);
            panel.add(playerFrame);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the scroll pane to the main container (seulement lors de l'initialisation)
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Initializes the map panel for displaying game actions.
     */
    private void initializeMapPanel() {
        // JPanel for displaying game actions
        mapPanel.setLayout(new BorderLayout());
        mapPanel.setBackground(Constant.MAP_PANEL_BACKGROUND_COLOR);

        // Create a titled border with an orange raised bevel border
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 2),  // Orange border
                BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.ORANGE, Color.ORANGE)
        );
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, "Game Map");
        titledBorder.setTitleColor(Color.ORANGE);
        mapPanel.setBorder(titledBorder);

        mapPanel.add(gameMap, BorderLayout.CENTER);
    }

    //Update Methods---------------------------------------------------


    /**
     * Updates the text of the buttons with the current size of each deck.
     */
    public void updateDeckSizes() {
        drawDungeonButton.setText("Dungeon Deck: " + Dungeon.getDeckPile().size());
        drawTreasureButton.setText("Treasure Deck: " + Treasure.getDeckPile().size());
        discardDungeonLabel.setText("Dungeon Discard :" + Dungeon.getDiscardPile().size());
        discardTreasureLabel.setText("Treasure Discard :" + Treasure.getDiscardPile().size());
        
    }

    public void updatePlayerFrames() {
        // Supprimer tous les composants existants du panel
        playerHandsPanel.removeAll();
        playerFrames.clear();
    
        ListOfPlayer listOfPlayers = Play.getPlayers();
        
        int numPlayers = listOfPlayers.getSize();
        int columns = Math.min(numPlayers, 3); // Maximum number of columns for layout
        int rows = (int) Math.ceil((double) numPlayers / columns);
    
        playerHandsPanel.setLayout(new GridLayout(rows, columns, 10, 10)); // Adjust the gap as needed
    
        for (int i = 0; i < numPlayers; i++) {
            PlayerFrame playerFrame = new PlayerFrame(listOfPlayers.getPlayer(i).getName(), listOfPlayers, i);
    
            // Set the background color of the PlayerFrame
            playerFrame.setBackground(Constant.PLAYER_FRAME_BACKGROUND_COLOR);
    
            // Set the preferred size of the PlayerFrame based on the game window size
            int preferredWidth = getWidth() / columns - 20; // Adjust the spacing
            int preferredHeight = getHeight() / rows - 20; // Adjust the spacing
            playerFrame.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    
            // Appelez la méthode updatePlayerInfo pour mettre à jour les informations du joueur
            playerFrame.updatePlayerInfo(listOfPlayers.getPlayer(i).getName(), listOfPlayers, i);
    
            playerFrames.add(playerFrame);
            playerHandsPanel.add(playerFrame);
        }
    
        // Rafraîchir l'interface graphique après l'ajout des nouveaux PlayerFrames
        revalidate();
        repaint();
    }
    

    

    
    /**
     * Updates the player statistics displayed in the player frames.
     *
     * @param players The list of players containing updated statistics.
     */
    public void updatePlayerStats(ListOfPlayer players) {

        System.out.println("PlayerFrames size: " + playerFrames.size());
    
        // Ajoutez une condition pour éviter l'IndexOutOfBoundsException
        if (!playerFrames.isEmpty()) {
            for (int i = 0; i < players.getSize(); i++) {
                PlayerFrame playerFrame = playerFrames.get(i);
                Player player = players.getPlayer(i);
                playerFrame.updateStats(player.getName(), player.getLevel(), player.getLives(), player.getMoney(), player.getCurse());
            }
        }
    }
    

    /**
     * Updates the player statistics panel based on the current player.
     *
     * @param currentPlayer The current player.
     */
    public static void updatePlayerStatsPanel(Player currentPlayer) {
        
        // Clear the player statistics panel before updating
        playerStatsPanel.removeAll();

        // Add the player's information to the panel
        addStatWithImage("Name", currentPlayer.getName(), "src/main/java/com/utmunchkin/gameplay/img/stats/name.png");
        addStatWithImage("Score", String.valueOf(currentPlayer.getScore()), "src/main/java/com/utmunchkin/gameplay/img/stats/level.png");
        addStatWithImage("Level", String.valueOf(currentPlayer.getLevel()), "src/main/java/com/utmunchkin/gameplay/img/stats/level.png");
        addStatWithImage("Lives", String.valueOf(currentPlayer.getLives()), "src/main/java/com/utmunchkin/gameplay/img/stats/lives.png");
        addStatWithImage("Money", String.valueOf(currentPlayer.getMoney()), "src/main/java/com/utmunchkin/gameplay/img/stats/money.png");
        addStatWithImage("Curse", String.valueOf(currentPlayer.getCurse()), "src/main/java/com/utmunchkin/gameplay/img/stats/curse.png");
        addStatWithImage("Defense", String.valueOf(currentPlayer.getDefense()), "src/main/java/com/utmunchkin/gameplay/img/stats/defense.png");
        addStatWithImage("Equip.", String.valueOf(currentPlayer.getEquippedObjectsNames()), "src/main/java/com/utmunchkin/gameplay/img/stats/equipement.png");
        addStatWithImage("Attack", String.valueOf(currentPlayer.getAttackForce()), "src/main/java/com/utmunchkin/gameplay/img/stats/attack.png");

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

    public void updatePlayersPanel() {

        playersPanel.removeAll();
        // JPanel for player interactions
        playersPanel.setLayout(new GridLayout(1, 0));

        // Add buttons for player interactions
        for (Player player : listOfPlayer.getPlayers()) {

            if(player.getName() == Play.curPlayer.getName()){
                JButton helpButton = new JButton("Go Alone ->");
                helpButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR);
                setFont(helpButton, font);
                helpButton.addActionListener(e -> Ally.requestHelp(player.getMe()));
                playersPanel.add(helpButton);
            }else{
                JButton helpButton = new JButton("Enroll - " + player.getName() + "  " + Constant.PRICE_FOR_REQUESTING_HELP + " money");
                helpButton.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR2);
                setFont(helpButton, font);
                helpButton.addActionListener(e -> Ally.requestHelp(player.getMe()));
                playersPanel.add(helpButton);
            }
        }
    }

    /**
     * Updates the information displayed in the information text area.
     *
     * @param message The message to be appended to the information text area.
     */
    public void updateInfo(String message) {
        infoTextArea.append(message + "\n");
    }




    // Show Methods - Display-----------------------------------------

    /**
     * Displays an instruction dialog to inform the player.
     *
     * @param instruction The instruction message to be displayed.
     * @return Always returns null since the method doesn't require user input.
     */
    public String showInstructionDialog(String instruction) {
        InstructionDialog instructionDialog = new InstructionDialog(this, instruction);
        instructionDialog.setVisible(true);

        // The method no longer needs to check user response or retrieve user input here.
        // The instruction dialog is primarily informative, and the user doesn't have to provide a direct response.

        return null;  // The method no longer needs to return a value.
    }


    /**
     * Placeholder method for showing player stats (can be implemented if needed).
     *
     * @param player The player whose stats are to be displayed.
     */
    public void showPlayerStats(Player player) {
        // Implement this method if needed
    }


    //Effects-------------------------------------------------------

    //GLOBAL

    /**
     * Applies aesthetic effects such as rounded corners, reliefs, and shadows.
     */
    private void applyAestheticEffects() {

        // Apply reliefs and shadows to panels and buttons
        applyReliefsAndShadows(deckPanel);
        applyReliefsAndShadows(playersPanel);
        applyReliefsAndShadows(playerHandsPanel);
        applyReliefsAndShadows(playerStatsPanel);
        applyReliefsAndShadows(mapPanel);
        applyReliefsAndShadows(otherAttributesPanel);

        // Apply reliefs and shadows to buttons
        applyReliefsAndShadowsToButtons(drawDungeonButton, drawTreasureButton);
    }

    //HOVER================================================================================

    /**
     * Adds a hover effect to a JButton.
     *
     * @param button The JButton to which the hover effect is added.
     */
    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Change the background and text color on hover
                button.setBackground(Color.ORANGE);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(Color.RED));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Return to the normal state
                button.setBackground(Constant.DRAW_BUTTON_BACKGROUND_COLOR);
                button.setForeground(Constant.DRAW_BUTTON_FOREGROUND_COLOR);
                button.setBorder(BorderFactory.createEmptyBorder());
                setEmbossedEffect(button);
            }
        });
    }

    
    //EMBOSSED=============================================================================

    public static void setEmbossedEffect(JComponent element) {
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createLoweredBevelBorder()
        );
        element.setBorder(border);
    }

    //HIDDEN===============================================================================

    public static void setHiddenEffect(JComponent element) {
        element.setVisible(false);
    }


    //FONT=================================================================================

    // Example:
    // Font customFont = loadFontFromFile("path/to/your/font.ttf", Font.PLAIN, 20);

    private static Font loadFontFromFile(String path, int style, int size) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File(path));
            return customFont.deriveFont(style, size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFont(JComponent element, String fontName, int fontStyle, int fontSize) {
        Font font = new Font(fontName, fontStyle, fontSize);
        element.setFont(font);
    }

    public static void setFont(JComponent element, Font font) {
        element.setFont(font);
    }

    //BORDER=================================================================================

    

    public void borderAllElements(Color color) {
        setBorderForElement(drawDungeonButton, color);
        setBorderForElement(drawTreasureButton, color);
        setBorderForElement(infoTextArea, color);
        setBorderForElement(mainInfoArea, color);
        setBorderForElement(deckPanel, color);
        setBorderForElement(otherAttributesPanel, color);
        setBorderForElement(mapPanel, color);
        setBorderForElement(playersPanel, color);
        setBorderForElement(playerHandsPanel, color);
        setBorderForElement(playerStatsPanel, color);
        for (PlayerFrame playerFrame : playerFrames) {
            setBorderForElement(playerFrame, color);
        }
    }

    public void setBorderForElement(JComponent element, Color color) {
        Border border = BorderFactory.createLineBorder(color, 5);
        element.setBorder(border);
    }

    
    //RELIEF=================================================================================

    /**
     * Applies reliefs and shadows to the specified panels and buttons.
     *
     * @param components The components to which effects will be applied.
     */
    private void applyReliefsAndShadows(Component... components) {
        for (Component component : components) {
            if (component instanceof JPanel) {
                // Apply a bevel border with lowered type for panels
                ((JPanel) component).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            } else if (component instanceof JButton) {
                // Apply a raised bevel border for buttons
                ((JButton) component).setBorder(BorderFactory.createRaisedBevelBorder());
            }
        }
    }

    /**
     * Applies reliefs and shadows to the specified buttons.
     *
     * @param buttons The buttons to which effects will be applied.
     */
    private void applyReliefsAndShadowsToButtons(JButton... buttons) {
        for (JButton button : buttons) {
            // Apply a raised bevel border for buttons
            button.setBorder(BorderFactory.createRaisedBevelBorder());

            // Add a drop shadow effect using a custom border
            button.setBorder(BorderFactory.createCompoundBorder(
                    button.getBorder(),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
    }

    //REMOVE==================================================================================


    //border________________________________________________

    public void removeBorderForElement(JComponent element) {
        element.setBorder(null);
    }

    public void removeBorderForAllElements() {
        removeBorderForElement(drawDungeonButton);
        removeBorderForElement(drawTreasureButton);
        removeBorderForElement(infoTextArea);
        removeBorderForElement(mainInfoArea);
        removeBorderForElement(deckPanel);
        removeBorderForElement(otherAttributesPanel);
        removeBorderForElement(mapPanel);
        removeBorderForElement(playersPanel);
        removeBorderForElement(playerHandsPanel);
        removeBorderForElement(playerStatsPanel);
        for (PlayerFrame playerFrame : playerFrames) {
            removeBorderForElement(playerFrame);
        }
    }


    //all_effects_____________________________________________

    public static void removeEffects(JComponent element) {
        element.setBorder(null);
        element.setVisible(true);
        element.setFont(null);
    }




    //Input Latch---------------------------------------------------

    /**
     * Waits for user input using a latch.
     */
    public void waitForUserInput() {
        try {
            userInputLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the user input latch.
     */
    public void resetUserInputLatch() {
        userInputLatch = new CountDownLatch(1);
    }

    

    //Players Management--------------------------------------------


    /**
     * Sets the user's choice.
     *
     * @param choice The user's choice.
     */
    public void setChoice(int choice) {
        this.choice = choice;
    }


    // Frames--------------------------------------------------------


    /**
     * Retrieves the list of player frames.
     *
     * @return The list of player frames.
     */
    public List<PlayerFrame> getPlayerFrames() {
        return this.playerFrames;
    }



    //----------------------------------------------------------------



    // Flag for start game

    public void setPlayFlag(boolean playFlag) {
        this.playFlag = playFlag;
    }

    public boolean getPlayFlag(){
        return this.playFlag;
    }

    private void waitForPlayButton() {
        while (!playFlag) {
            try {
                // Sleep for a short duration to avoid freezing the UI
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // Getters---------------------------------------------------------

    /**
     * Gets the map panel.
     *
     * @return The map panel.
     */
    public MapPanel getMap() {
        return this.gameMap;
    }


    // Getters for JComponents-----------------------------------------

    public JButton getDrawDungeonButton() {
        return drawDungeonButton;
    }

    public JButton getDrawTreasureButton() {
        return drawTreasureButton;
    }

    public JTextArea getInfoTextArea() {
        return infoTextArea;
    }

    public static JLabel getMainInfoArea() {
        return mainInfoArea;
    }

    public JPanel getDeckPanel() {
        return deckPanel;
    }

    public JPanel getOtherAttributesPanel() {
        return otherAttributesPanel;
    }

    public JPanel getMapPanel() {
        return mapPanel;
    }

    public JPanel getPlayersPanel() {
        return playersPanel;
    }

    public JPanel getPlayerHandsPanel() {
        return playerHandsPanel;
    }

    public static JPanel getPlayerStatsPanel() {
        return playerStatsPanel;
    }


    //Sub Classes

    public static class DrawPileWindow extends JFrame {

        private JLabel treasureLabel;
        private JLabel dungeonLabel;

        public DrawPileWindow(Treasure treasure, Dungeon dungeon) {
            setTitle("DrawPile Window");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(2, 1)); // 2 rows, 1 column
            setSize(800, 600);

            treasureLabel = new JLabel("Treasure Draw Pile");
            dungeonLabel = new JLabel("Dungeon Draw Pile");

            add(treasureLabel);
            add(dungeonLabel);

            pack();
            setLocationRelativeTo(null); // Center the frame
        }

        public void updateDrawPiles(Treasure updatedTreasure, Dungeon updatedDungeon) {
            // Update the labels with information from the updated draw piles
            treasureLabel.setText("Treasure Draw Pile: " + Treasure.getDeckPile());
            dungeonLabel.setText("Dungeon Draw Pile: " + Dungeon.getDeckPile());
        }
    }
}

