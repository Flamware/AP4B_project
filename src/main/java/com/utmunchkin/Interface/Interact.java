package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.cards.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * The Interact class provides methods for displaying graphical user interface elements and handling user interactions.
 */
public class Interact {
    private static int selectedButtonIndex;

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
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton button = new JButton(buttonText);
        button.setIcon(buttonIcon);  // Set the icon on the button
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonText[0] = buttonText;
                frame.dispose();  // Close the frame after button click
            }
        });

        // Set the preferred size of the button based on the image dimensions
        int preferredWidth = buttonIcon.getIconWidth();
        int preferredHeight = buttonIcon.getIconHeight();
        button.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(button);

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        frame.add(scrollPane);

        frame.pack();  // Pack the frame to accommodate the preferred size
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Wait for user interaction (button click)
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
}
