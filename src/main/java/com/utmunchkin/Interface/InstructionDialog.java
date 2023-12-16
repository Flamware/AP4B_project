package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.cards.Card;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InstructionDialog extends JDialog {

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
