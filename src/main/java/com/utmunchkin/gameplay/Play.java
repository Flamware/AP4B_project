package main.java.com.utmunchkin.gameplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
//import java.util.Scanner;

import javax.swing.JOptionPane;

import main.java.com.utmunchkin.Rules;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.cards.CardData.CardInfo;
import main.java.com.utmunchkin.cards.CardData.CardType;
import main.java.com.utmunchkin.cards.Card.Monster;
import main.java.com.utmunchkin.cards.Card.Curse;
import main.java.com.utmunchkin.cards.CardData;
import main.java.com.utmunchkin.cards.CardEffects;
import main.java.com.utmunchkin.cards.Cards;
import main.java.com.utmunchkin.cards.CardsActions;
import main.java.com.utmunchkin.cards.Dungeon;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.players.ListOfPlayer;
import main.java.com.utmunchkin.players.Player;
import main.java.com.utmunchkin.Interface.Board;

public class Play implements CardsActions {

    private int currentPlayerIndex;
    private int firstPlayer;
    private ListOfPlayer players;
    private boolean gameWon;
    private Cards cardsOfGame;
    private Dungeon dungeonCard;
    //Dungeon drawnCard = new Dungeon();
    private Treasure treasureCard;
    private Rules rules;
    private Board board;
    private Curse curse;

    // Default constructor without specifying the first player index
    public Play(ListOfPlayer players) {
        this.players = players;
        this.rules = new Rules(); // Initialize rules
        this.currentPlayerIndex = rules.getFirstPlayerIndex();
        this.gameWon = false;
    }

    // Constructor with a specified first player index
    public Play(ListOfPlayer players, int firstPlayerIndex) {
        this.players = players;
        this.rules = new Rules(); // Initialize rules
        this.currentPlayerIndex = firstPlayerIndex;
        this.gameWon = false;
        this.firstPlayer = firstPlayerIndex;
        this.cardsOfGame = new Cards(); // Initialize cardsOfGame
        this.cardsOfGame.printCards();
        this.dungeonCard = new Dungeon();
        this.treasureCard = new Treasure();
    }
    public void gameProcess() {
        initializeGame();
        while (!gameWon) {
            Player currentPlayer = players.getPlayer(currentPlayerIndex);
            board.showMessageDialog("Tour du joueur: " + currentPlayer.getName());

            if (currentPlayer.getCurse()) {
                int response = board.showYesNoDialog("Payer 5 unités d'argent ou perdre 1 vie pour lever la malédiction ?");
                if (response == JOptionPane.YES_OPTION) {
                    if (currentPlayer.getMoney() >= 5) {
                        currentPlayer.addMoney(-5);
                        currentPlayer.setCurse(false);
                        board.showMessageDialog("La malédiction a été levée en échange de 5 unités d'argent.");
                    } else {
                        if (currentPlayer.getLives() > 1) {
                            currentPlayer.addLives(-1);
                            currentPlayer.setCurse(false);
                            board.showMessageDialog("La malédiction a été levée en perdant 1 vie.");
                        } else {
                            board.showMessageDialog("Vous n'avez pas suffisamment d'argent ni de vies pour lever la malédiction.");
                            currentPlayer.setCurse(true);
                        }
                    }
                } else {
                    currentPlayer.setCurse(true);
                }
            }

            openDoorPhase(currentPlayer);

            if (!currentPlayer.hasEncounteredMonster()) {
                board.updateInfo("Chercher Bagarre");
                lookForTroublePhase(currentPlayer);
                board.updateInfo("Piller la salle");
                lootTheRoomPhase(currentPlayer);
            }

            charityPhase(currentPlayer, players);
            endTurnPhase(currentPlayer);

            checkGameWon(currentPlayer);

            // Mettez à jour l'affichage des statistiques des joueurs après chaque action
            updatePlayersStats();
            
            currentPlayerIndex = (currentPlayerIndex + 1) % players.getSize();
        }
    }

    private void updatePlayersStats() {
        // Affichez les statistiques des joueurs sur l'interface utilisateur
        for (Player player : players.getPlayers()) {
            board.showPlayerStats(player);
        }
    }

    private void initializeGame() {
        for (int i = 0; i < players.getSize(); i++) {
            cardsOfGame.distributeDungeonTreasureCardToPlayer(players.getPlayer(i), 4);
            System.out.println("\n"+players.getPlayer(i).getInfo());
            //for each card in hand, print card name
            for (int j = 0; j < players.getPlayer(i).getHand().size(); j++) {
                System.out.println(players.getPlayer(i).getHand().get(j).getCardName());
            }
            System.out.println("\n");
        }
        board = new Board(this);
    }

    private void checkGameWon(Player currentPlayer) {
        if (currentPlayer.getLevel() >= 10) {
            gameWon = true;
            System.out.println(currentPlayer.getName() + " has won the game!");
        }
    }

    @Override
    public void onOpenDoor(Player player) {
        System.out.println(player.getName() + " is opening a door...");
        Card topDungeonCard = dungeonCard.removeFirstFromDeck();
        
        System.out.println("Top Dungeon Card: " + topDungeonCard);
        
        if (topDungeonCard != null) {
            handleDungeonCard(player, topDungeonCard);
        }
    }

    private void handleDungeonCard(Player player, Card dungeonCard) {
        String cardName = dungeonCard.getCardName();
        CardInfo cardInfo = Card.getCardInfo(cardName);
    
        if (cardInfo.getCardType() == CardData.CardType.MONSTER) {
            System.out.println("monster");
            Monster monster = (Monster) dungeonCard;
            handleMonsterEncounter(player, monster);
        } else if (cardInfo.getCardType() == CardData.CardType.CURSE) {
            System.out.println("curse");
            Curse curse = (Curse) dungeonCard;
            handleCurse(player, curse);
        } else {
            System.out.println("other");
            handleNonMonsterCard(player, dungeonCard, cardInfo);
        }
    }

    private void handleMonsterEncounter(Player player, Monster monster) {
        System.out.println("It's a monster! " + player.getName() + " must face it.");
        player.setHasEncounteredMonster(true);
        faceMonster(player, monster);
    }

    private void handleCurse(Player player, Curse curse) {
        System.out.println("It's a curse! " + player.getName() + " must suffer its effects.");
        sufferCurse(player, curse);
    }

    private void handleNonMonsterCard(Player player, Card dungeonCard, CardInfo cardInfo) {
        System.out.println("It's not a monster or curse. " + player.getName() + " adds it to their hand.");
        player.addToHand(dungeonCard);

        int chx;
        do {
            System.out.println("select a card from your hand");
            chx = board.getChoice();
        } while (chx <0 || chx > player.getHand().size() - 1);

        String effectFunctionName = cardInfo.getEffectFunctionName();
        if (effectFunctionName != null && !effectFunctionName.isEmpty()) {
            applySpecialEffect(player, player.getHand().get(chx), effectFunctionName);
        }
    }

    private void applySpecialEffect(Player player, Card card, String effectFunctionName) {
        CardEffects.applyEffect(card, player, effectFunctionName);
    }

    private void openDoorPhase(Player player) {
        onOpenDoor(player);
    }

    private void lootTheRoomPhase(Player player) {
        System.out.println("Vous n'avez rencontré aucun monstre en ouvrant la porte.");
        System.out.println("Vous pouvez piller la salle.");
        Dungeon drawnCard = dungeonCard;
        player.addToHand(drawnCard.removeFirstFromDeck());
        System.out.println(player.getName() + " a tiré une carte Donjon face cachée : " + drawnCard.getCardName());
    }

    private void lookForTroublePhase(Player player) {
        List<Card> playerHand = player.getHand();
        List<Card> monstersInHand = new ArrayList<>();
        board.updateInfo("Vous n'avez croisé aucun monstre en ouvrant la porte.");
        for (Card card : playerHand){
            if (card.getInfo().getCardType() == CardType.MONSTER){
                monstersInHand.add(card);
            }
        }
        if (!playerHand.isEmpty() && !monstersInHand.isEmpty()) {
            
            board.updateInfo("Vous pouvez affronter un monstre de votre main :");

            displayPlayerHand(playerHand);
            System.out.println(monstersInHand);
            
            Card selectedMonster = selectMonsterFromHand(playerHand);

            faceMonster(player, selectedMonster);
            
            board.updateInfo(player.getName() + " affronte le monstre : " + selectedMonster.getCardName());
        } else {
            board.updateInfo("Vous n'avez aucun monstre dans votre main.");
        }

        System.out.println("Fin du combat");
    }

    private void displayPlayerHand(List<Card> playerHand) {
        for (int i = 0; i < playerHand.size(); i++) {
            System.out.println((i + 1) + ". " + playerHand.get(i).getCardName());
        }
    }

    private Card selectMonsterFromHand(List<Card> playerHand) {
        if (playerHand.isEmpty()) {
            board.updateInfo("Vous n'avez aucun monstre dans votre main.");
            return null;
        }

        board.updateInfo("Choisissez un monstre de votre main :");
        board.showInstructionDialog("Entrez le numéro du monstre que vous souhaitez affronter : ");
        int choice = board.getChoice();
        return playerHand.get(choice);
    }

    private void endTurnPhase(Player player) {
        player.setHasEncounteredMonster(false);
    }

    private void faceMonster(Player player, Card selectedMonster) {
        int playerCombatStrength = player.getLevel();

        board.showMessageDialog("Combat details:\n" +
                "Player Combat Strength: " + playerCombatStrength + "\n" +
                "Monster Combat Strength: " + selectedMonster.getMonsterCombatStrength());

        if (playerCombatStrength >= selectedMonster.getMonsterCombatStrength()) {
            board.showMessageDialog("You defeat the monster!");
            int levelsGained = Math.min(2, selectedMonster.getLevels());
            player.gainLevel(levelsGained);
            int treasuresGained = selectedMonster.getTreasures();
            gainTreasures(treasuresGained, player);
            board.showMessageDialog("Gained " + levelsGained + " level(s) and " + treasuresGained + " treasure(s).");
        } else {
            board.showMessageDialog("You must flee from the monster!");
            player.loseLevel(1);
        }
    }

    private void gainTreasures(int treasuresGained, Player player) {
        System.out.println("Drawing " + treasuresGained + " treasure(s).");

        List<Card> drawnTreasures = drawTreasureCards(treasuresGained);
        player.addToHand(drawnTreasures);
    }

    private List<Card> drawTreasureCards(int count) {
        List<Card> treasureCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            treasureCards.add(new Treasure());
        }
        return treasureCards;
    }

    private void sufferCurse(Player player, Curse curse) {
        player.setCurse(true);
    }

    private void charityPhase(Player currentPlayer, ListOfPlayer allPlayers) {
        if (currentPlayer.getHand().size() > 5) {
            System.out.println("Munchkin : La charité");

            int i = 0;
            while (/*currentPlayer.getHand().size() > 5*/ i < 2) {
                playExcessCard(currentPlayer);
                i++;
            }

            Player lowestLevelPlayer = findPlayerWithLowestLevel(allPlayers);

            if (lowestLevelPlayer != null) {
                giveExcessCards(currentPlayer, players);
            }
        }
    }

    private void playExcessCard(Player player) {
        
        if (!players.getPlayer(currentPlayerIndex).getHand().isEmpty()) {
            Card cardToPlay = players.getPlayer(currentPlayerIndex).getFromHand(0);
            System.out.println(player.getName() + " a joué la carte : " + cardToPlay.getCardName());
            playCard(cardToPlay, player);
            
        }
    }

    private void playCard(Card cardToPlay, Player curPlayer) {
        String effectFunctionName = cardToPlay.getInfo().getEffectFunctionName();
    
        if (effectFunctionName != null && !effectFunctionName.isEmpty()) {
            CardEffects.applyEffect(cardToPlay, curPlayer, effectFunctionName);
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

            System.out.println(donor.getName() + " a donné les cartes excédentaires à " + recipient.getName());
        } else {
            System.out.println("Le joueur n'a pas sélectionné de destinataire. Aucun transfert effectué.");
        }
    }

    private Player askPlayerForRecipient(ListOfPlayer players2) {
        // Créez un tableau de noms de joueurs
        String[] playerNames = new String[players2.getSize()];
        for (int i = 0; i < players2.getSize(); i++) {
            playerNames[i] = players2.getPlayer(i).getName();
        }

        // Affichez une boîte de dialogue pour demander au joueur à qui donner les cartes excédentaires
        String selectedPlayer = (String) JOptionPane.showInputDialog(
                null,
                "À qui souhaitez-vous donner vos cartes excédentaires ?",
                "Sélectionnez le destinataire",
                JOptionPane.QUESTION_MESSAGE,
                null,
                playerNames,
                playerNames[0]
        );

        // Recherchez le joueur sélectionné dans la liste
        for (Player player : players.getPlayers()) {
            if (player.getName().equals(selectedPlayer)) {
                return player;
            }
        }

        // Aucun joueur sélectionné
        return null;
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
}
