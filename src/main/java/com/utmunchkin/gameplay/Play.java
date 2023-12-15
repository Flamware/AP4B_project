package main.java.com.utmunchkin.gameplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Rules;
import main.java.com.utmunchkin.cards.*;
import main.java.com.utmunchkin.cards.CardData.CardInfo;
import main.java.com.utmunchkin.cards.CardData.TreasureType;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;

public class Play implements CardsActions {

    private int currentPlayerIndex;
    private int firstPlayer;
    private ListOfPlayer players;
    private boolean gameWon;
    private Cards cardsOfGame;
    private Dungeon dungeonCard;
    private Treasure treasureCard;
    private Rules rules;
    private Board board;
    private CardEffects effects = new CardEffects();

    public Play(ListOfPlayer players) {
        this.players = players;
        this.rules = new Rules();
        this.currentPlayerIndex = rules.getFirstPlayerIndex();
        this.gameWon = false;
    }

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

    public void gameProcess() {
        initializeGame();
        while (!gameWon) {
            Player currentPlayer = players.getPlayer(currentPlayerIndex);
            board.showMessageDialog("Tour du joueur: " + currentPlayer.getName());

            if (currentPlayer.getCurse()) {
                handleCurseEffect(currentPlayer);
            }

            openDoorPhase(currentPlayer);

            if (!currentPlayer.hasEncounteredMonster()) {
                lookForTroubleAndLootTheRoomPhases(currentPlayer);
            }

            charityPhase(currentPlayer, players);
            board.updatePlayerStats(players);
            endTurnPhase(currentPlayer);

            checkGameWon(currentPlayer);

            updatePlayersStats();

            currentPlayerIndex = (currentPlayerIndex + 1) % players.getSize();
        }
    }

    private void handleCurseEffect(Player currentPlayer) {
        int response = board.showYesNoDialog("Payer 5 unités d'argent ou perdre 1 vie pour lever la malédiction ?");
        if (response == JOptionPane.YES_OPTION) {
            handleCursePayment(currentPlayer);
        } else {
            currentPlayer.setCurse(true);
        }
    }

    private void handleCursePayment(Player currentPlayer) {
        if (currentPlayer.getMoney() >= 5) {
            currentPlayer.addMoney(-5);
            currentPlayer.setCurse(false);
            board.showMessageDialog("La malédiction a été levée en échange de 5 unités d'argent.");
        } else if (currentPlayer.getLives() > 1) {
            currentPlayer.addLives(-1);
            currentPlayer.setCurse(false);
            board.showMessageDialog("La malédiction a été levée en perdant 1 vie.");
        } else {
            board.showMessageDialog("Vous n'avez pas suffisamment d'argent ni de vies pour lever la malédiction.");
            currentPlayer.setCurse(true);
        }
    }

    private void lookForTroubleAndLootTheRoomPhases(Player currentPlayer) {
        openDoorPhase(currentPlayer);

        if (!currentPlayer.hasEncounteredMonster()) {
            board.updateInfo("Chercher Bagarre");
            lookForTroublePhase(currentPlayer);
            board.updateInfo("Piller la salle");
            lootTheRoomPhase(currentPlayer);
        }
    }

    private void updatePlayersStats() {
        for (Player player : players.getPlayers()) {
            board.showPlayerStats(player);
        }
    }

    private void initializeGame() {
        for (int i = 0; i < players.getSize(); i++) {
            cardsOfGame.distributeDungeonTreasureCardToPlayer(players.getPlayer(i), 4);
            System.out.println("\n" + players.getPlayer(i).getInfo());
            for (int j = 0; j < players.getPlayer(i).getHand().size(); j++) {
                System.out.println(players.getPlayer(i).getHand().get(j).getCardName());
            }
            System.out.println("\n");
        }
        board = new Board(this);
    }

    private void openDoorPhase(Player player) {
        board.updateInfo("Munchkin : Ouvrir une porte");

        // Révéler la 1ère carte de la pile Donjon
        Card revealedCard = dungeonCard.removeFirstFromDeck();
        board.updateInfo("Carte révélée : " + revealedCard.getCardName());

        // Appliquer immédiatement les effets des cartes monstre et malédiction
        if (revealedCard.getInfo().getTreasureType() == TreasureType.MONSTER
                || revealedCard.getInfo().getTreasureType() == TreasureType.CURSE) {
            handleRevealedCardEffect(player, revealedCard);
        } else {
            // Récupérer la carte dans la main
            player.addToHand(revealedCard);
            board.updateInfo("La carte a été ajoutée à votre main : " + revealedCard.getCardName());
        }

        // Wait for the user's input before proceeding to the next action
        board.waitForUserInput();
    }

    private void handleRevealedCardEffect(Player player, Card revealedCard) {
        if (revealedCard.getInfo().getTreasureType() == TreasureType.MONSTER) {
            // Le joueur doit affronter le monstre
            faceMonster(player, revealedCard);
        } else if (revealedCard.getInfo().getTreasureType() == TreasureType.CURSE) {
            // Le joueur doit subir la malédiction
            handleCurseEffect(player);
        }
    }

    private void lootTheRoomPhase(Player player) {
        board.updateInfo("Munchkin Trésor");
        board.updateInfo("Si vous n'avez rencontré AUCUN monstre ♦, vous pouvez piller la salle :");

        // Utiliser l'interface pour demander à l'utilisateur s'il veut piocher une carte Donjon
        int response = board.showYesNoDialog("Voulez-vous piocher une carte Donjon ?");

        if (response == JOptionPane.YES_OPTION) {
            Dungeon drawnCard = dungeonCard;
            player.addToHand(drawnCard.removeFirstFromDeck());
            board.updateInfo(player.getName() + " a tiré une carte Donjon face cachée : " + drawnCard.getCardName());
        } else {
            board.updateInfo("Vous avez choisi de ne pas piocher de carte Donjon.");
        }

        // Wait for the user's input before proceeding to the next action
        board.waitForUserInput();
    }

    private void lookForTroublePhase(Player player) {
        List<Card> playerHand = player.getHand();
        List<Card> monstersInHand = new ArrayList<>();
        board.updateInfo("Vous n'avez croisé aucun monstre en ouvrant la porte.");

        for (Card card : playerHand) {
            if (card.getInfo().getTreasureType() == TreasureType.MONSTER) {
                monstersInHand.add(card);
            }
        }

        if (!monstersInHand.isEmpty()) {
            board.updateInfo("Vous pouvez affronter un monstre de votre main :");
            displayPlayerHand(monstersInHand);

            // Utilize the interface to allow the user to choose a card
            int choice = board.showCardSelectionDialog(monstersInHand);
            if (choice != -1) {
                Card selectedMonster = monstersInHand.get(choice);
                faceMonster(player, selectedMonster);
                board.updateInfo(player.getName() + " affronte le monstre : " + selectedMonster.getCardName());
            } else {
                board.updateInfo("Aucun monstre sélectionné.");
            }
        } else {
            board.updateInfo("Vous n'avez aucun monstre dans votre main.");
        }

        // Wait for the user's input before proceeding to the next action
        board.waitForUserInput();

        board.updateInfo("Fin du combat");
    }

    private void charityPhase(Player currentPlayer, ListOfPlayer allPlayers) {
        if (currentPlayer.getHand().size() > 5) {
            board.showMessageDialog("Munchkin : La charité");

            int i = 0;
            while (i < 2) {
                playExcessCard(currentPlayer);
                i++;
            }

            Player lowestLevelPlayer = findPlayerWithLowestLevel(allPlayers);

            if (lowestLevelPlayer != null) {
                giveExcessCards(currentPlayer, players);
            }
        }

        // Wait for the user's input before proceeding to the next action
        board.waitForUserInput();
    }

    private void playExcessCard(Player player) {
        if (!players.getPlayer(currentPlayerIndex).getHand().isEmpty()) {
            Card cardToPlay = players.getPlayer(currentPlayerIndex).getFromHand(0);
            board.showMessageDialog(player.getName() + " a joué la carte : " + cardToPlay.getCardName());
            playCard(cardToPlay, player);
        }
    }

    private void playCard(Card cardToPlay, Player curPlayer) {
        String effectFunctionName = cardToPlay.getInfo().getEffectFunctionName();

        if (effectFunctionName != null && !effectFunctionName.isEmpty()) {
            effects.applyEffect(cardToPlay, curPlayer, effectFunctionName, players);
            System.out.println("played");
        } else {
            System.out.println("No special effect for card: " + cardToPlay.getCardName());
        }
    }

    private Player findPlayerWithLowestLevel(ListOfPlayer allPlayers) {
        List<Player> playersList = allPlayers.getPlayers();

        return playersList.stream()
                .min(Comparator.comparing(Player::getLevel))
                .orElse(null);
    }

    private void giveExcessCards(Player donor, ListOfPlayer players2) {
        List<Card> excessCards = donor.getHand().subList(5, donor.getHand().size());

        // Demandez au joueur à qui il souhaite donner les cartes excédentaires
        Player recipient = askPlayerForRecipient(players2);

        // Assurez-vous qu'un joueur a été sélectionné
        if (recipient != null) {
            // Effectuez le transfert de cartes
            recipient.addToHand(excessCards);
            donor.removeFromHand(excessCards);

            board.showMessageDialog(donor.getName() + " a donné les cartes excédentaires à " + recipient.getName());
        } else {
            board.showMessageDialog("Le joueur n'a pas sélectionné de destinataire. Aucun transfert effectué.");
        }
    }

    private Player askPlayerForRecipient(ListOfPlayer players) {
        Player currentPlayer = players.getPlayer(currentPlayerIndex);

        // Créez un tableau de noms de joueurs
        String[] playerNames = new String[players.getSize()];
        for (int i = 0; i < players.getSize(); i++) {
            playerNames[i] = players.getPlayer(i).getName();
        }

        // Affichez une boîte de dialogue pour demander au joueur à qui donner les cartes excédentaires
        String selectedPlayer = board.showInputDialog(
                "À qui souhaitez-vous donner vos cartes excédentaires ?",
                "Sélectionnez le destinataire",
                playerNames,
                playerNames[0]
        );

        // Vérifiez si la sélection a été annulée
        if (selectedPlayer == null) {
            // L'utilisateur a annulé la sélection, vous pouvez gérer cela comme nécessaire
            return null;
        }

        // Vérifiez si le joueur actuel est sélectionné
        if (currentPlayer.getName().equals(selectedPlayer)) {
            // Le joueur actuel est sélectionné, vous pouvez gérer cela comme nécessaire
            // Par exemple, affichez un message d'erreur et demandez une nouvelle sélection
            board.showMessageDialog("Vous ne pouvez pas vous donner vos propres cartes.");
            return askPlayerForRecipient(players);
        }

        // Recherchez le joueur sélectionné dans la liste
        for (Player player : players.getPlayers()) {
            if (player.getName().equals(selectedPlayer)) {
                return player;
            }
        }

        // Aucun joueur correspondant trouvé
        return null;
    }

    // Remplacez la méthode endTurnPhase par :
    private void endTurnPhase(Player player) {
        player.setHasEncounteredMonster(false);
        checkHandAndDraw();

        // Ajoutez des appels à l'interface graphique ici pour mettre à jour l'affichage
        board.updatePlayerStats(players);
    }

    // Remplacez la méthode checkGameWon par :
    private void checkGameWon(Player currentPlayer) {
        if (currentPlayer.getLevel() >= 10) {
            gameWon = true;
            // Utilisez l'interface graphique pour afficher le message de victoire
            board.showMessageDialog(currentPlayer.getName() + " a remporté la partie !");
        }
    }

    // Modifiez la méthode displayPlayerHand comme suit :
    private void displayPlayerHand(List<Card> playerHand) {
        StringBuilder message = new StringBuilder("Votre main :\n");
        for (int i = 0; i < playerHand.size(); i++) {
            message.append((i + 1)).append(". ").append(playerHand.get(i).getCardName()).append("\n");
        }
        // Utilisez l'interface graphique pour afficher le message
        board.updateInfo(message.toString());
    }

    // Modifiez la méthode selectMonsterFromHand comme suit :
    private Card selectMonsterFromHand(List<Card> playerHand) {
        if (playerHand.isEmpty()) {
            board.updateInfo("Vous n'avez aucun monstre dans votre main.");
            return null;
        }

        board.updateInfo("Vous pouvez affronter un monstre de votre main :");

        displayPlayerHand(playerHand);

        // Utilisez l'interface graphique pour demander le choix du monstre
        board.showInstructionDialog("Entrez le numéro du monstre que vous souhaitez affronter : ");
        int choice = board.getChoice();
        return playerHand.get(choice);
    }

    // Modifiez la méthode faceMonster comme suit :
    private void faceMonster(Player player, Card selectedMonster) {
        int playerCombatStrength = player.getLevel();

        // Utilisez l'interface graphique pour afficher les détails du combat
        board.showMessageDialog("Détails du combat :\n" +
                "Force de combat du joueur : " + playerCombatStrength + "\n" +
                "Force de combat du monstre : " + selectedMonster.getMonsterCombatStrength());

        if (playerCombatStrength >= selectedMonster.getMonsterCombatStrength()) {
            // Utilisez l'interface graphique pour afficher le message de victoire
            board.showMessageDialog("Vous avez vaincu le monstre !");
            int levelsGained = Math.min(2, selectedMonster.getLevels());
            player.gainLevel(levelsGained);
            int treasuresGained = selectedMonster.getTreasures();
            gainTreasures(treasuresGained, player);
            board.showMessageDialog("Gagné " + levelsGained + " niveau(x) et " + treasuresGained + " trésor(s).");
        } else {
            // Utilisez l'interface graphique pour afficher le message de défaite
            board.showMessageDialog("Vous devez fuir le monstre !");
            player.loseLevel(1);
        }
    }

    // Modifiez la méthode checkHandAndDraw comme suit :
    public void checkHandAndDraw() {
        while (players.getPlayer(currentPlayerIndex).getHand().size() < 5) {
            // Piocher une carte du donjon et l'ajouter à la main
            Card drawnCard = dungeonCard.removeFirstFromDeck();
            players.getPlayer(currentPlayerIndex).addToHand(drawnCard);
            // Utilisez l'interface graphique pour afficher le message
            board.showMessageDialog("Vous avez pioché une carte Donjon face cachée : " + drawnCard.getCardName());
        }
    }

    // Modifiez la méthode gainTreasures comme suit :
    private void gainTreasures(int treasuresGained, Player player) {
        // Utilisez l'interface graphique pour afficher le message
        board.showMessageDialog("Piochage de " + treasuresGained + " trésor(s).");

        List<Card> drawnTreasures = drawTreasureCards(treasuresGained);
        player.addToHand(drawnTreasures);

        // Utilisez l'interface graphique pour afficher les trésors gagnés
        StringBuilder treasuresMessage = new StringBuilder("Trésors gagnés :\n");
        for (Card treasure : drawnTreasures) {
            treasuresMessage.append(treasure.getCardName()).append("\n");
        }
        board.showMessageDialog(treasuresMessage.toString());
    }

    // Ajoutez la méthode drawTreasureCards pour générer des cartes trésor :
    private List<Card> drawTreasureCards(int count) {
        List<Card> treasureCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            treasureCards.add(new Treasure());
        }
        return treasureCards;
    }



    //getters and setters
    public int getFirstPlayerIndex() {
        return firstPlayer;
    }
    public void setFirstPlayerIndex(int firstPlayerIndex) {
        this.firstPlayer = firstPlayerIndex;
    }
    public ListOfPlayer getPlayers() {
        return players;
    }
    public void setPlayers(ListOfPlayer players) {
        this.players = players;
    }
    public boolean isGameWon() {
        return gameWon;
    }
    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }
    public Cards getCardsOfGame() {
        return cardsOfGame;
    }
    public void setCardsOfGame(Cards cardsOfGame) {
        this.cardsOfGame = cardsOfGame;
    }
    public Rules getRules() {
        return rules;
    }
    public void setRules(Rules rules) {
        this.rules = rules;
    }
    public Board getBoard() {
        return board;
    }
    public Dungeon getDungeonCard() {
        return dungeonCard;
    }
    public Treasure getTreasureCard() {
        return treasureCard;
    }
    public void setBoard(Board board) {
        this.board = board;
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    @Override
    public void onOpenDoor(Player player) {

    }
}
