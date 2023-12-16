package main.java.com.utmunchkin.gameplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Interface.PlayerFrame;
import main.java.com.utmunchkin.Rules;
import main.java.com.utmunchkin.cards.*;
import main.java.com.utmunchkin.cards.CardData.SubType;
import main.java.com.utmunchkin.cards.CardData.MonstersType;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;
import main.java.com.utmunchkin.Interface.Interact;

public class Play implements CardsActions {

    private int currentPlayerIndex;
    private static Player curPlayer;
    private int firstPlayer;
    private ListOfPlayer players;
    private boolean gameWon;
    private Cards cardsOfGame;
    private final Dungeon dungeonCard;
    private final Treasure treasureCard;
    private Rules rules;
    private Board board;

    // Card effects manager
    private final CardEffects effects = new CardEffects();

    // Constructor for initializing the game with a list of players and a specified first player index
    /**
     * Constructor for initializing the game with a list of players and the index of the first player.
     * @param players           List of players participating in the game.
     * @param firstPlayerIndex  Index of the first player.
     */
    public Play(ListOfPlayer players, int firstPlayerIndex) {
        this.players = players;
        this.rules = new Rules();
        this.currentPlayerIndex = firstPlayerIndex;
        this.gameWon = false;
        this.firstPlayer = firstPlayerIndex;
        this.cardsOfGame = new Cards();
        this.dungeonCard = new Dungeon();
        this.treasureCard = new Treasure();
    }

    // Method to distribute cards and initialize the game
    /**
     * Initializes the game by distributing dungeon and treasure cards to players and setting up the game board.
     */
    private void initializeGame() {
        // Distribute cards to players (4 dungeon and 4 treasure)
        for (int i = 0; i < players.getSize(); i++) {
            cardsOfGame.distributeDungeonTreasureCardToPlayer(players.getPlayer(i), Constant.NUMBER_OF_CARDS_FOR_EACH_TYPE);
            System.out.println("INFO:\n" + players.getPlayer(i).getInfo());
            for (int j = 0; j < players.getPlayer(i).getHand().size(); j++) {
                System.out.println("CARD:" + players.getPlayer(i).getHand().get(j).getCardName());
            }
            System.out.println("\n");
        }
        // Create the game board
        board = new Board(this, dungeonCard, treasureCard);
    }

    // Method to execute the game process
    /**
     * Manages the main game process, including player turns, encounters, and victory conditions.
     */
    public void gameProcess() {
        // Initialize the game by distributing cards to players
        initializeGame();

        while (!isGameWon()) {
            // Setup the current player
            setCurrentPlayer(players.getPlayer(currentPlayerIndex));
            Player currentPlayer = getCurrentPlayer();

            // Update player statistics panel on the board
            Board.updatePlayerStatsPanel(currentPlayer);

            // Display information about the current player's turn on the board
            board.updateInfo("Player's Turn: " + currentPlayer.getName());

            // Handle curse effects if the player is cursed
            if (currentPlayer.getCurse()) {
                handleCurseEffect(currentPlayer);
            }

            // Debugging information
            System.out.println("Check 1");
            System.out.println("Monster encountered?: " + currentPlayer.hasEncounteredMonster());

            // Execute phases related to encountering monsters and looting rooms
            lookForTroubleAndLootTheRoomPhases(currentPlayer);

            // Execute the charity phase
            charityPhase(currentPlayer, players);

            // Update player statistics on the board
            board.updatePlayerStats(players);

            // Execute the end turn phase
            endTurnPhase(currentPlayer);

            // Check if the game is won by the current player
            checkGameWon(currentPlayer);

            // Update player statistics for all players
            updatePlayersStats();

            // Move to the next player's turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.getSize();
        }
    }

    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    // Method to execute the "Look for Trouble" and "Loot the Room" phases for the current player
    /**
     * Manages the phases of looking for trouble and looting the room for the current player.
     * @param currentPlayer The player initiating the phases.
     */
    private void lookForTroubleAndLootTheRoomPhases(Player currentPlayer) {
        // Execute the "Open Door" phase and determine if a monster is encountered
        currentPlayer.setHasEncounteredMonster(openDoorPhase(currentPlayer));
        System.out.println("Monster encountered?: " + currentPlayer.hasEncounteredMonster());

        // If no monster is encountered, execute the "Look for Trouble" and "Loot the Room" phases
        if (!currentPlayer.hasEncounteredMonster()) {
            board.updateInfo("Look for Trouble");
            lookForTroublePhase(currentPlayer);
            board.updateInfo("Loot the Room");
            lootTheRoomPhase(currentPlayer);
        }
        // Update the player stats panel on the board
        Board.updatePlayerStatsPanel(curPlayer);
    }


    // Method representing the "Open Door" phase where the player opens a door and reveals a card
    /**
     * Executes the open door phase, revealing dungeon cards and applying their effects.
     * @param player The player initiating the open door phase.
     * @return True if a monster is encountered, false otherwise.
     */
    private boolean openDoorPhase(Player player) {
        board.updateInfo("Munchkin: Open a door");
        ImageIcon icon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/door.png");
        Interact.showSingleButtonDialog("Open door", icon);

        // Reveal the first card from the Dungeon deck
        Card revealedCard = dungeonCard.removeFirstFromDeck();
        board.updateDeckSizes();
        SubType cardSubType = revealedCard.getInfo().getSubType();

        // Update information about the revealed card on the board
        board.updateInfo("Revealed Card: " + revealedCard.getCardName() + " MONSTER?: " + this.isMonsterType(cardSubType));

        // Apply immediate effects of monster and curse cards
        if (this.isMonsterType(cardSubType) || revealedCard.getInfo().getSubType() == SubType.CURSE) {
            handleRevealedCardEffect(player, revealedCard);
            // Update the player stats panel on the board
            Board.updatePlayerStatsPanel(curPlayer);
            return true;
        } else {
            // Add the card to the player's hand if it's not a monster or curse card
            player.addToHand(revealedCard);
            board.updateInfo("The card has been added to your hand: " + revealedCard.getCardName());
            // Update the player stats panel on the board
            Board.updatePlayerStatsPanel(curPlayer);
            return false;
        }

        // Wait for the user's input before proceeding to the next action
        // board.waitForUserInput();
    }


    // Method representing the "Look for Trouble" phase for the player
    /**
     * Manages the player's choice to face a monster from their hand during the look for trouble phase.
     * @param player The player initiating the look for trouble phase.
     */
    private void lookForTroublePhase(Player player) {
        List<Card> playerHand = player.getHand();
        List<Card> monstersInHand = new ArrayList<>();

        // Identify monsters in the player's hand
        for (Card card : playerHand) {
            if (isMonsterType(card.getInfo().getSubType())) {
                monstersInHand.add(card);
            }
        }

        if (!monstersInHand.isEmpty()) {
            board.updateInfo("You can face a monster from your hand:");
            displayPlayerHand(monstersInHand);

            // Utilize the interface to allow the user to choose a card
            int choice = Interact.showCardSelectionDialog(monstersInHand);
            if (choice != -1) {
                Card selectedMonster = monstersInHand.get(choice);
                faceMonster(player, selectedMonster);
                board.updateInfo(player.getName() + " faces the monster: " + selectedMonster.getCardName());
            } else {
                board.updateInfo("No monster selected.");
            }
        } else {
            board.updateInfo("You have no monsters in your hand.");
        }

        refreshFrames();
        // Update information and end the combat phase
        board.updateInfo("End of combat");
    }


    // Method representing the "Loot the Room" phase for the player

    /**
     * Initiates the loot the room phase for the current player.
     * @param player The player initiating the loot the room phase.
     */
    private void lootTheRoomPhase(Player player) {
        board.updateInfo("Munchkin Treasure");
        board.updateInfo("You haven't encountered any monsters. You can loot the room.");

        // Ask the player if they want to draw a Dungeon card
        String response = Interact.showInputDialog("Do you want to draw a Dungeon card?", "Draw Dungeon", new String[]{"Yes", "No"}, player.getName());

        assert response != null;
        if (response.equals("Yes")) {
            drawDungeonCard();
        } else {
            board.updateInfo("You have chosen not to draw a Dungeon card.");
        }
    }


    // Method representing the "Charity" phase for the current player
    /**
     * Manages the charity phase, allowing the player to give excess cards to a player with the lowest level.
     * @param currentPlayer The player initiating the charity phase.
     * @param allPlayers    List of all players in the game.
     */
    private void charityPhase(Player currentPlayer, ListOfPlayer allPlayers) {
        ImageIcon icon = new ImageIcon("src/main/java/com/utmunchkin/gameplay/img/charity.png");

        // If the player's hand size exceeds 5, initiate the charity phase
        if (currentPlayer.getHand().size() > 5) {
            Interact.showSingleButtonDialog("charity", icon);

            // Continue playing excess cards until the hand size is 8 or less
            while (currentPlayer.getHand().size() > 8) {
                playExcessCard(currentPlayer);
            }

            // Find the player with the lowest level among all players
            Player lowestLevelPlayer = findPlayerWithLowestLevel(allPlayers);

            // If a player with the lowest level is found, give excess cards to them
            if (lowestLevelPlayer != null) {
                giveExcessCards(currentPlayer, players);
            }
        }

        // Update player stats panel
        Board.updatePlayerStatsPanel(curPlayer);

        // Wait for the user's input before proceeding to the next action
        //board.waitForUserInput();
    }


    //
    /**
     * Ends the turn for the current player, resetting flags and checking for victory conditions.
     * @param player The player whose turn is ending.
     */
    private void endTurnPhase(Player player) {
        player.setHasEncounteredMonster(false);
        checkHandAndDraw();

        // Update the player frames and stats panel
        refreshFrames();
    }


    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    // Method to handle the effect of a revealed card (either facing a monster or enduring a curse)
    /**
     * Handles the effects of the revealed card, either facing a monster or enduring a curse.
     * @param player        The player facing the effect.
     * @param revealedCard  The card revealed during the open door phase.
     */
    private void handleRevealedCardEffect(Player player, Card revealedCard) {
        SubType cardSubType = revealedCard.getInfo().getSubType();

        if (isMonsterType(cardSubType)) {
            // If the revealed card is a monster, the player must face it
            System.out.println("Facing monster...");
            faceMonster(player, revealedCard);
        } else if (cardSubType == SubType.CURSE) {
            // If the revealed card is a curse, the player must endure it
            System.out.println("Suffering curse...");
            handleCurseEffect(player);
        }
    }

    // Method to check if the subtype of a card is a monster type

    private boolean isMonsterType(SubType subType) {
        for (MonstersType monsterType : MonstersType.values()) {
            if (monsterType.name().equals(subType.name())) {
                return true;
            }
        }
        return false;
    }

    //
    /**
     * Displays the player's hand on the game board.
     * @param playerHand The list of cards in the player's hand.
     */
    private void displayPlayerHand(List<Card> playerHand) {
        StringBuilder message = new StringBuilder("Your hand:\n");
        for (int i = 0; i < playerHand.size(); i++) {
            message.append((i + 1)).append(". ").append(playerHand.get(i).getCardName()).append("\n");
        }
        // Use the graphical interface to display the message
        board.updateInfo(message.toString());
    }

    //
    /**
     * Facilitates the combat between the player and a faced monster, handling victory or defeat.
     * @param player          The player facing the monster.
     * @param selectedMonster The monster card being faced.
     */
    private void faceMonster(Player player, Card selectedMonster) {
        int playerCombatStrength = player.getAttackForce();

        System.out.println("Combat details:\n" +
                "Player's combat strength: " + playerCombatStrength + "\n" +
                "Monster's combat strength: " + selectedMonster.getMonsterCombatStrength(selectedMonster));

        // Use the graphical interface to display combat details
        Interact.showMessageDialog("Combat details:\n" +
                "Player's combat strength: " + playerCombatStrength + "\n" +
                "Monster's combat strength: " + selectedMonster.getMonsterCombatStrength(selectedMonster));

        if (Interact.yesOrNoDialog("Fight? " + selectedMonster.getCardName()).equals("Yes")) {
            if (playerCombatStrength >= selectedMonster.getMonsterCombatStrength(selectedMonster)) {
                // Use the graphical interface to display the victory message
                Interact.showMessageDialog("You have defeated the monster!");
                int levelsGained = Math.min(2, selectedMonster.getLevels());
                player.gainLevel(levelsGained);
                int treasuresGained = selectedMonster.getTreasures();
                gainTreasures(treasuresGained, player);
                Interact.showMessageDialog("You got " + levelsGained + " level(s) and " + treasuresGained + " treasure(s).");
            } else {
                // Use the graphical interface to display the defeat message
                Interact.showMessageDialog("You must flee from the monster. You lost 1 level.");
                player.loseLevel(1);
                effects.applyEffect(selectedMonster, curPlayer, selectedMonster.getInfo().getEffectFunctionName(), players, board);
            }
        } else {
            System.out.println("You flee from the monster (lost money and level)");
            Interact.showMessageDialog("You must flee from the monster. You lost 1 level.");
            player.gainLevel(-1);
            player.addMoney(-2);
        }
        Board.updatePlayerStatsPanel(curPlayer);
    }

    // Method to handle the curse effect for the current player
    /**
     * Handles the curse effect for the current player, giving them a choice to pay or lose a life.
     * @param currentPlayer The player facing the curse.
     */
    private void handleCurseEffect(Player currentPlayer) {
        // Ask the player to pay 5 units of money or lose 1 life to lift the curse
        String response = Interact.yesOrNoDialog("Pay 5 units of money or lose 1 life to lift the curse?");
        if (response.equals("Yes")) {
            // Handle curse payment if the player chooses to pay
            handleCursePayment(currentPlayer);
        } else {
            // Set the curse status to true if the player chooses not to pay
            currentPlayer.setCurse(true);
        }
        // Update the player stats panel on the board
        Board.updatePlayerStatsPanel(curPlayer);
    }

    // Method to handle the curse payment for the current player
    /**
     * Handles the payment to lift the curse for the current player.
     * @param currentPlayer The player facing the curse.
     */
    private void handleCursePayment(Player currentPlayer) {
        if (currentPlayer.getMoney() >= 5) {
            // Pay 5 units of money to lift the curse
            currentPlayer.addMoney(-5);
            currentPlayer.setCurse(false);
            Interact.showMessageDialog("The curse has been lifted in exchange for 5 units of money.");
        } else if (currentPlayer.getLives() > 1) {
            // Lose 1 life to lift the curse if the player doesn't have enough money
            currentPlayer.addLives(-1);
            currentPlayer.setCurse(false);
            Interact.showMessageDialog("The curse has been lifted by losing 1 life.");
        } else {
            // Inform the player that they don't have enough money or lives to lift the curse, and set the curse status to true
            Interact.showMessageDialog("You don't have enough money or lives to lift the curse.");
            currentPlayer.setCurse(true);
        }
        // Update the player stats panel on the board
        Board.updatePlayerStatsPanel(curPlayer);
    }



    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------


    // Method to ask the player to select a recipient for excess cards
    /**
     * Asks the current player to select a recipient for giving excess cards during the charity phase.
     * @param players List of all players in the game.
     * @return The selected recipient player.
     */
    private Player askPlayerForRecipient(ListOfPlayer players) {
        Player currentPlayer = players.getPlayer(currentPlayerIndex);

        // Create an array of player names
        String[] playerNames = new String[players.getSize()];
        for (int i = 0; i < players.getSize(); i++) {
            playerNames[i] = players.getPlayer(i).getName();
        }

        // Show a dialog box to ask the player to whom to give the excess cards
        String selectedPlayer = Interact.showInputDialog(
                "To whom do you want to give your excess cards?",
                "Select Recipient",
                playerNames,
                playerNames[0]
        );

        // Check if the selection was canceled
        if (selectedPlayer == null) {
            // The user canceled the selection; handle it as needed
            return null;
        }

        // Check if the current player is selected
        if (currentPlayer.getName().equals(selectedPlayer)) {
            // The current player is selected; handle it as needed
            // For example, display an error message and request a new selection
            Interact.showMessageDialog("You cannot give yourself your own cards.");
            return askPlayerForRecipient(players);
        }

        // Search for the selected player in the list
        for (Player player : players.getPlayers()) {
            if (player.getName().equals(selectedPlayer)) {
                return player;
            }
        }

        // No matching player found
        return null;
    }


    // Method to give excess cards from a donor to another player
    /**
     * Gives excess cards from the donor player to a recipient player during the charity phase.
     * @param donor   The player giving excess cards.
     * @param listPlayers List of all players in the game.
     */
    private void giveExcessCards(Player donor, ListOfPlayer listPlayers) {
        if (donor.getHand().size() >= 5) {
            List<Card> excessCards = donor.getHand().subList(5, donor.getHand().size());

            // Ask the player to whom they want to give the excess cards
            Player recipient = askPlayerForRecipient(listPlayers);

            // Ensure a player has been selected
            if (recipient != null) {
                // Transfer the cards
                recipient.addToHand(excessCards);
                donor.removeFromHand(excessCards);

                Interact.showMessageDialog(donor.getName() + " gave the excess cards to " + recipient.getName());
                refreshFrames(players);
            } else {
                Interact.showMessageDialog("The player did not select a recipient. No transfer was made.");
                refreshFrames(players);
            }
        } else {
            System.out.println("Not enough cards to give");

            // Propose to the player to draw cards to reach 5 cards
            while (donor.getHand().size() < 5) {
                drawDungeonCard();
            }
        }
    }

    // Method representing the "Play Excess Card" action for the player
    /**
     * Plays excess cards from the player's hand during the charity phase.
     * @param player The player playing excess cards.
     */
    private void playExcessCard(Player player) {
        System.out.println("Checking excess cards...");

        // Check if the player's hand is not empty
        if (!players.getPlayer(currentPlayerIndex).getHand().isEmpty()) {
            curPlayer.setBoardActionAuthorized(true);

            // Check if the board action is authorized
            if (curPlayer.isBoardActionAuthorized()) {
                // Wait until the user clicks a card button
                while (!PlayerFrame.isCardClicked()) {
                    try {
                        Thread.sleep(100); // Add a small delay to avoid CPU-intensive waiting
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        Thread.currentThread().interrupt();  // Restore the interrupted status
                    }
                }

                refreshFrames();

                // Process the clicked card
                System.out.println(PlayerFrame.lastClickedCard);
                if (curPlayer.getHand().contains(PlayerFrame.lastClickedCard)) {
                    Card cardToPlay = players.getPlayer(currentPlayerIndex).getFromHand(PlayerFrame.getPlayerClickedCardIndex());
                    Interact.showMessageDialog(player.getName() + " played the card: " + cardToPlay.getCardName());
                    playCard(cardToPlay, player);
                    PlayerFrame.resetLastClickedCard();  // Reset the card click information
                    curPlayer.setBoardActionAuthorized(false);

                    refreshFrames();
                } else {
                    Interact.showMessageDialog("This card does not belong to you, " + curPlayer.getName());
                    PlayerFrame.resetLastClickedCard();  // Reset the card click information
                    curPlayer.setBoardActionAuthorized(false);

                    refreshFrames();
                }
            }
        }
    }

    // Method to play a card and apply its effects
    /**
     * Plays a selected card from the player's hand.
     * @param cardToPlay The card to be played.
     * @param curPlayer  The current player.
     */
    private void playCard(Card cardToPlay, Player curPlayer) {
        String effectFunctionName = cardToPlay.getInfo().getEffectFunctionName();

        // Apply the card's effect if it has one
        if (effectFunctionName != null && !effectFunctionName.isEmpty()) {
            effects.applyEffect(cardToPlay, curPlayer, effectFunctionName, players, board);
            Board.updatePlayerStatsPanel(curPlayer);
            System.out.println("Card played");
        } else {
            System.out.println("No special effect for card: " + cardToPlay.getCardName());
        }
    }

    // Method to find the player with the lowest level among all players
    /**
     * Finds the player with the lowest level among all players.
     * @param allPlayers List of all players in the game.
     * @return The player with the lowest level.
     */
    private Player findPlayerWithLowestLevel(ListOfPlayer allPlayers) {
        List<Player> playersList = allPlayers.getPlayers();

        return playersList.stream()
                .min(Comparator.comparing(Player::getLevel))
                .orElse(null);
    }



    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    //
    /**
     * Checks the player's hand size and draws dungeon cards until they have at least 5 cards.
     */
    public void checkHandAndDraw() {
        while (players.getPlayer(currentPlayerIndex).getHand().size() < 5) {
            drawDungeonCard();
        }
        refreshFrames();
    }

    //
    /**
     * Checks if the current player has reached the level required for winning the game.
     * @param currentPlayer The player to check for victory.
     */
    private void checkGameWon(Player currentPlayer) {
        if (currentPlayer.getLevel() >= 10) {
            setGameWon(true);
            // Use the graphical interface to display the victory message
            Interact.showMessageDialog(currentPlayer.getName() + " has won the game!");
        }
    }



    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------

    //
    /**
     * Handles the process of gaining treasures for the player.
     * @param treasuresGained The number of treasures to be gained.
     * @param player          The player gaining treasures.
     */
    private void gainTreasures(int treasuresGained, Player player) {
        // Use the graphical interface to display the message
        Interact.showMessageDialog("Drawing " + treasuresGained + " treasure(s).");

        // Draw the treasure cards using the drawTreasures method
        List<Card> drawnTreasures = drawTreasures(treasuresGained);
        player.addToHand(drawnTreasures);

        // Use the graphical interface to display the gained treasures
        StringBuilder treasuresMessage = new StringBuilder("Gained treasures:\n");
        for (Card treasure : drawnTreasures) {
            treasuresMessage.append(treasure.getCardName()).append("\n");
        }
        Interact.showMessageDialog(treasuresMessage.toString());
    }

    // Move the core functionality to drawTreasures method
    /**
     * Draws a specified number of treasure cards from the treasure deck.
     * @param count The number of treasure cards to draw.
     * @return A list of drawn treasure cards.
     */
    private List<Card> drawTreasures(int count) {
        Player player = players.getPlayer(currentPlayerIndex);

        // Notify the board to enable the "Draw Treasure" button
        player.setBoardActionAuthorized(true);

        // Wait until the user clicks the "Draw Treasure" button
        while (!player.isDrawTreasure()) {
            try {
                Thread.sleep(100); // Add a small delay to avoid CPU-intensive waiting
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        // Reset the flag after the action is performed
        player.setDrawTreasure(false);

        // Draw the treasure cards
        List<Card> treasureCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            treasureCards.add(treasureCard.removeFirstFromDeck());
            board.updateDeckSizes();
        }

        // Notify the board that the action is complete
        player.setBoardActionAuthorized(false);

        refreshFrames();
        return treasureCards;
    }


    // Method to handle the player drawing a Dungeon card during the "Loot the Room" phase
    private void drawDungeonCard() {
        Player player = players.getPlayer(currentPlayerIndex);
        // Notify the board to enable the "Draw Dungeon" button
        player.setBoardActionAuthorized(true);

        // Wait until the user clicks the "Draw Dungeon" button
        while (!player.isDrawDungeon()) {
            try {
                Thread.sleep(100); // Add a small delay to avoid CPU-intensive waiting
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        // Reset the flag after the action is performed
        player.setDrawDungeon(false);

        // Draw the dungeon card
        Dungeon drawnCard = dungeonCard;
        player.addToHand(drawnCard.removeFirstFromDeck());
        board.updateInfo(player.getName() + " has drawn a face-down Dungeon card: " + drawnCard.getCardName());
        board.updateDeckSizes();

        // Notify the board that the action is complete
        player.setBoardActionAuthorized(false);
        refreshFrames();
    }




    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------


    // Method to refresh the player frames and update the display
    /**
     * Refreshes the graphical interface frames to update player information.
     */
    public void refreshFrames() {
        // Call the updatePlayerHand method to update the player's hand display
        board.getPlayerFrames().get(currentPlayerIndex).updatePlayerHand(curPlayer.getHand());

        // Add calls to the graphical interface here to update the display
        System.out.println("Player hand updated: " + curPlayer.getName());
        board.updatePlayerStats(players);
    }

    // Method to refresh the player frames for a list of players and update the display
    /**
     * Refreshes the graphical interface frames for all players to update their information.
     * @param lPlayers List of all players in the game.
     */
    public void refreshFrames(ListOfPlayer lPlayers) {
        List<Player> playersList = lPlayers.getPlayers();

        for (Player p : playersList) {
            // Call the updatePlayerHand method to update the player's hand display
            board.getPlayerFrames().get(playersList.indexOf(p)).updatePlayerHand(p.getHand());

            // Add calls to the graphical interface here to update the display
            System.out.println("Player hand updated: " + p.getName());
            board.updatePlayerStats(players);
        }
    }

    // Method to update the statistics for all players on the board
    /**
     * Updates the stats and information for all players on the game board.
     */
    private void updatePlayersStats() {
        for (Player player : players.getPlayers()) {
            // Show the player statistics on the board
            board.showPlayerStats(player);
        }
        // Update the player stats panel on the board for the current player
        Board.updatePlayerStatsPanel(curPlayer);
    }




    //
    //GETTERS, SETTERS
    //







    // Getters and setters

    //--------------------------------------------------------------

    // Check if the game is won
    /**
     * Check if the game is won.
     * @return True if the game is won, false otherwise.
     */
    public boolean isGameWon() {
        return gameWon;
    }

    // Set the game won status
    /**
     * Set the game won status.
     * @param gameWon True if the game is won, false otherwise.
     */
    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    //--------------------------------------------------------------

    // Get the cards used in the game
    /**
     * Get the cards used in the game.
     * @return The cards used in the game.
     */
    public Cards getCardsOfGame() {
        return cardsOfGame;
    }

    // Set the cards used in the game
    /**
     * Set the cards used in the game.
     * @param cardsOfGame The cards used in the game.
     */
    public void setCardsOfGame(Cards cardsOfGame) {
        this.cardsOfGame = cardsOfGame;
    }

    //--------------------------------------------------------------

    // Get the rules of the game
    /**
     * Get the rules of the game.
     * @return The rules of the game.
     */
    public Rules getRules() {
        return rules;
    }

    // Set the rules of the game
    /**
     * Set the rules of the game.
     * @param rules The rules of the game.
     */
    public void setRules(Rules rules) {
        this.rules = rules;
    }

    //--------------------------------------------------------------

    // Get the dungeon card
    /**
     * Get the dungeon card.
     * @return The dungeon card.
     */
    public Dungeon getDungeonCard() {
        return dungeonCard;
    }

    // Get the treasure card
    /**
     * Get the treasure card.
     * @return The treasure card.
     */
    public Treasure getTreasureCard() {
        return treasureCard;
    }


    //--------------------------------------------------------------

    // Get the game board
    /**
     * Get the game board.
     * @return The game board.
     */
    public Board getBoard() {
        return board;
    }

    // Set the game board
    /**
     * Set the game board.
     * @param board The game board.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    //--------------------------------------------------------------

    // Get the current player
    /**
     * Get the current player.
     * @return The current player.
     */
    public static Player getCurrentPlayer() {
        return curPlayer;
    }

    // Get the index of the current player
    /**
     * Get the index of the current player.
     * @return The index of the current player.
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    // Set the current player
    /**
     * Set the current player.
     * @param curP The current player.
     */
    public void setCurrentPlayer(Player curP) {
        curPlayer = curP;
    }

    // Get the index of the first player
    /**
     * Get the index of the first player.
     * @return The index of the first player.
     */
    public int getFirstPlayerIndex() {
        return firstPlayer;
    }

    // Set the index of the first player
    /**
     * Set the index of the first player.
     * @param firstPlayerIndex The index of the first player.
     */
    public void setFirstPlayerIndex(int firstPlayerIndex) {
        this.firstPlayer = firstPlayerIndex;
    }

    // Get the list of players
    /**
     * Get the list of players.
     * @return The list of players.
     */
    public ListOfPlayer getPlayers() {
        return players;
    }

    // Set the list of players
    /**
     * Set the list of players.
     * @param players The list of players.
     */
    public void setPlayers(ListOfPlayer players) {
        this.players = players;
    }

    //--------------------------------------------------------------

    // Event handler for opening doors (not implemented)
    /**
     * Event handler for opening doors (not implemented).
     * @param player The player opening the door.
     */
    @Override
    public void onOpenDoor(Player player) {
        // Implementation not provided
    }


}



//
//LIST OF ALL METHODS OF PLAY CLASS
//

/*
//public Play(ListOfPlayer players);
//public Play(ListOfPlayer players, int firstPlayerIndex);
//private void initializeGame();
//public void gameProcess();
//private void handleCurseEffect(Player currentPlayer);
//private void handleCursePayment(Player currentPlayer);
//private void lookForTroubleAndLootTheRoomPhases(Player currentPlayer);
//private void updatePlayersStats();
//private boolean openDoorPhase(Player player);
//private void handleRevealedCardEffect(Player player, Card revealedCard);
//private void lootTheRoomPhase(Player player);
//private void lookForTroublePhase(Player player);
//private void charityPhase(Player currentPlayer, ListOfPlayer allPlayers);
//private void playExcessCard(Player player);
//private void playCard(Card cardToPlay, Player curPlayer);
//private Player findPlayerWithLowestLevel(ListOfPlayer allPlayers);
//private void giveExcessCards(Player donor, ListOfPlayer players);
//private Player askPlayerForRecipient(ListOfPlayer players);
//private void endTurnPhase(Player player);
//public void refreshFrames();
//public void refreshFrames(ListOfPlayer lPlayers);
//private void checkGameWon(Player currentPlayer);
//private void displayPlayerHand(List<Card> playerHand);
//private Card selectMonsterFromHand(List<Card> playerHand);
//private void faceMonster(Player player, Card selectedMonster);
//public void checkHandAndDraw();
//private void gainTreasures(int treasuresGained, Player player);
//private List<Card> drawTreasures(int count);
//public int getFirstPlayerIndex();
//public void setFirstPlayerIndex(int firstPlayerIndex);
//public ListOfPlayer getPlayers();
//public void setPlayers(ListOfPlayer players);
//public boolean isGameWon();
//public void setGameWon(boolean gameWon);
//public Cards getCardsOfGame();
//public void setCardsOfGame(Cards cardsOfGame);
//public Rules getRules();
//public void setRules(Rules rules);
//public Board getBoard();
//public Dungeon getDungeonCard();
//public Treasure getTreasureCard();
//public void setBoard(Board board);
//public int getCurrentPlayerIndex();
//public void onOpenDoor(Player player);
//public void setCurrentPlayer(Player curP);
//public static Player getCurrentPlayer();
*/