package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.cards.Card;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * The Interact class provides methods for displaying graphical user interface elements and handling user interactions.
 */
public class Interact {
    private static int selectedButtonIndex;


    /**
     * Displays a dialog to get the number of players.
     *
     * @return The selected number of players.
     */
    public static int showPlayerNumberDialog() {
        String[] options = new String[Constant.INITIAL_CAPACITY - 1];
        for (int i = 0; i < options.length; i++) {
            options[i] = Integer.toString(i + 2);
        }

        String selectedOption = (String) JOptionPane.showInputDialog(
                null,
                "Select the number of players:",
                "Number of Players",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        // Convert the selected option to an integer
        if (selectedOption != null) {
            return Integer.parseInt(selectedOption);
        } else {
            // User canceled the dialog
            return -1;
        }
    }

    /**
     * Displays a dialog to get the names of players.
     *
     * @param numberOfPlayers The number of players.
     * @return A list of player names.
     */
    public static List<String> showPlayerNameDialog(int numberOfPlayers) {
        Set<String> playerNames = new HashSet<>();

        for (int i = 1; i <= numberOfPlayers; i++) {
            String playerName = getPlayerName(i, playerNames);
            playerNames.add(playerName);
        }

        return List.copyOf(playerNames);
    }

    private static String getPlayerName(int playerNumber, Set<String> usedNames) {
        String playerName;
        do {
            playerName = JOptionPane.showInputDialog(
                    null,
                    "Enter name for Player " + playerNumber + ":",
                    "Player Name",
                    JOptionPane.PLAIN_MESSAGE);

            // Check for duplicate names
            if (playerName != null && (playerName.trim().isEmpty() || usedNames.contains(playerName))) {
                JOptionPane.showMessageDialog(null, "Invalid or duplicate name. Please enter a different name.");
            }
        } while (playerName == null || playerName.trim().isEmpty() || usedNames.contains(playerName));

        return playerName;
    }


    /**
     * Displays a message dialog with the specified message.
     *
     * @param message The message to be displayed.
     */
    public static void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Displays a dialog for the player to select a monster card from their hand.
     *
     * @param monstersInHand List of monster cards in the player's hand.
     * @return The index of the selected monster card.
     */
    public static int showCardSelectionDialog(List<Card> monstersInHand) {
        selectedButtonIndex = -1;

        JFrame frame = new JFrame("Select a Card");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

        for (int i = 0; i < monstersInHand.size(); i++) {
            Card card = monstersInHand.get(i);

            // Load the default image
            ImageIcon defaultIcon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/default.png");
            Image image = defaultIcon.getImage();

            // Resize the image to fit the button
            Image resizedImage = image.getScaledInstance(100, 150, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Create the button with the resized image and card name
            JButton button = new JButton("<html><center>" + card.getCardName() + "</center></html>");
            button.setIcon(resizedIcon);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setBackground(Constant.BUTTON_COLOR_1);

            // Add action listener to the button
            button.addActionListener(new CardButtonListener(i));

            // Add the button to the panel
            buttonPanel.add(button);
        }

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        frame.add(scrollPane);

        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Wait for user interaction (button click)
        while (selectedButtonIndex == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        frame.dispose();  // Close the frame after selection

        return selectedButtonIndex;
    }

    /**
     * Displays a dialog with a single button and an icon.
     *
     * @param buttonText The text to be displayed on the button.
     * @param buttonIcon The icon to be displayed on the button.
     * @return The text of the selected button.
     */
    public static String showSingleButtonDialog(String buttonText, ImageIcon buttonIcon) {
        final String[] selectedButtonText = {null};
    
        JFrame frame = new JFrame("Action");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Do nothing on close
    
        JButton button = new JButton(buttonText);
        button.setIcon(buttonIcon);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonText[0] = buttonText;
                frame.dispose();
            }
        });
    
        int preferredWidth = buttonIcon.getIconWidth();
        int preferredHeight = buttonIcon.getIconHeight();
        button.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(button);
    
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        frame.add(scrollPane);
    
        // Add a window listener to handle window close event
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Check if the button has been clicked
                if (selectedButtonText[0] == null) {
                    // Button not clicked, handle accordingly (reopen the window, show a warning, etc.)
                    System.out.println("Window closed without clicking the button. Reopening...");
                    frame.setVisible(true);
                } else {
                    // Button clicked, dispose of the frame
                    frame.dispose();
                }
            }
        });
    
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    
        while (selectedButtonText[0] == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        return selectedButtonText[0];
    }    

    private static class CardButtonListener implements ActionListener {
        private final int buttonIndex;

        public CardButtonListener(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedButtonIndex = buttonIndex;
        }
    }

    /**
     * Displays an input dialog for the player to enter a choice from a list of options.
     *
     * @param title       The title of the dialog.
     * @param message     The message to be displayed.
     * @param playerNames The list of player names as options.
     * @param playerName  The default selected player name.
     * @return The selected player name.
     */
    public static String showInputDialog(String title, String message, String[] playerNames, String playerName) {
        // Create a JComboBox with the playerNames array
        JComboBox<String> comboBox = new JComboBox<>(playerNames);

        // Set an initial selection if playerName is not null
        if (playerName != null) {
            comboBox.setSelectedItem(playerName);
        }

        // Create a JOptionPane with the JComboBox
        int option = JOptionPane.showOptionDialog(
                null,
                new Object[]{message, comboBox},
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );

        // Check if the user selected OK
        if (option == JOptionPane.OK_OPTION) {
            // Return the selected item
            return (String) comboBox.getSelectedItem();
        } else {
            // User canceled or closed the dialog
            return null;
        }
    }

    /**
     * Returns a placeholder value (0) indicating a choice.
     *
     * @return 0 (placeholder value).
     */
    public static int getChoice() {
        return 0;
    }

    /**
     * Displays a dialog with "Yes" and "No" options and returns the user's choice.
     *
     * @param message The message to be displayed.
     * @return "Yes" if the user chooses "Yes," "No" otherwise.
     */
    public static String yesOrNoDialog(String message) {
        int dialogResult = JOptionPane.showOptionDialog(null, message, "Choice", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No"}, null);

        // Check the user's choice and return "Yes" or "No" accordingly
        if (dialogResult == JOptionPane.YES_OPTION) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public static boolean confirmPurchase(Card card) {
        int result = JOptionPane.showConfirmDialog(
                null,
                "Voulez-vous acheter la carte " + card.getCardName() +
                        " pour " + (double) (card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF) + " pièces d'or ?",
                "Confirmation d'achat",
                JOptionPane.YES_NO_OPTION);

        return result == JOptionPane.YES_OPTION;
    }

    public static class InstructionDialog extends JDialog {

        private JTextField userInputField;
        private boolean userResponse;
    
        public InstructionDialog(JFrame parent, String instruction) {
            super(parent, "Instruction", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    
            JLabel instructionLabel = new JLabel(instruction);
            userInputField = new JTextField(10); // Allow the user to enter a number
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
    
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (validateUserInput()) {
                        userResponse = true;
                        dispose(); // Close the dialog
                    } else {
                        JOptionPane.showMessageDialog(InstructionDialog.this,
                                "Please enter a valid number between 1 and 8.",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
    
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userResponse = false;
                    dispose(); // Close the dialog
                }
            });
    
            JPanel panel = new JPanel();
            panel.add(instructionLabel);
            panel.add(userInputField);
            panel.add(okButton);
            panel.add(cancelButton);
            getContentPane().add(panel);
    
            pack();
            setLocationRelativeTo(parent); // Center the dialog relative to the parent frame
            setResizable(false);
        }
    
        public boolean getUserResponse() {
            return userResponse;
        }
    
        private boolean validateUserInput() {
            try {
                int userInput = Integer.parseInt(userInputField.getText());
                return userInput >= 1 && userInput <= 8;
            } catch (NumberFormatException e) {
                return false; // Not a valid integer
            }
        }
    
        public int getUserInput() {
            try {
                return Integer.parseInt(userInputField.getText());
            } catch (NumberFormatException e) {
                return -1; // Not a valid integer
            }
        }
    
    }
    
}