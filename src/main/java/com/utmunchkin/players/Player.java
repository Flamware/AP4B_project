package main.java.com.utmunchkin.players;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.Interface.Interact;
import main.java.com.utmunchkin.Interface.Shop;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.cards.Cards;
import main.java.com.utmunchkin.cards.Dungeon;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.cards.CardData.CardType;
import main.java.com.utmunchkin.cards.CardData.SubType;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends ListOfPlayer {
    // Player data
    private String name;
    private int score;
    private int lives;
    private double money;
    private int attackForce;
    private int defense;
    private List<Card> equippedObjects;
    private int turn;
    private int number;
    private List<Card> hand;
    private String race;
    private String playerClass;
    private int level;
    private String gender;
    private boolean hasEncounteredMonster;
    private int equippedObjectsSize;
    private boolean isCursed;
    private boolean isDead;

    private boolean drawDungeon; // Flag to indicate whether the player wants to draw a dungeon card
    private boolean drawTreasure; // Flag to indicate whether the player wants to draw a treasure card

    private boolean isShoppingTime;
    private boolean boardActionAuthorized;

    // Constructors

    // Default constructor
    public Player(String name, int number) {
        initializeDefaultValues(name, number);
    }

    // Constructor with card distribution
    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck) {
        initializeDefaultValues(name, number);
        distributeInitialCards(cardDeck, numberOfInitialCardsForEachDeck);
    }

    // Constructor with card distribution and specified race, class, level, and gender
    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck,
                  String race, String playerClass, int level, String gender) {
        initializeDefaultValues(name, number);
        distributeInitialCards(cardDeck, numberOfInitialCardsForEachDeck);
        setAdditionalValues(race, playerClass, level, gender);
    }

    private void initializeDefaultValues(String name, int number) {
        this.name = name;
        this.score = 0;
        this.lives = Constant.MAX_INITIAL_PLAYERS_LIVES;
        this.money = Constant.MAX_INITIAL_PLAYERS_MONEY;
        this.attackForce = Constant.MAX_INITIAL_PLAYERS_ATTACK;
        this.defense = Constant.MAX_INITIAL_PLAYERS_DEFENSE;
        this.equippedObjects = new ArrayList<>();
        this.turn = 0;
        this.number = number;
        this.hand = new ArrayList<>();
        this.race = "Human";
        this.playerClass = "No Class";
        this.level = 1;
        this.gender = "Player Defined";
        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isDead = false;
        this.boardActionAuthorized = false;
    }

    private void distributeInitialCards(Cards cardDeck, int numberOfInitialCardsForEachDeck) {
        this.hand = new ArrayList<>();
        cardDeck.distributeDungeonTreasureCardToPlayer(this, numberOfInitialCardsForEachDeck);
    }

    private void setAdditionalValues(String race, String playerClass, int level, String gender) {
        this.lives = Constant.MAX_INITIAL_PLAYERS_LIVES;
        this.money = Constant.MAX_INITIAL_PLAYERS_MONEY;
        this.attackForce = Constant.MAX_INITIAL_PLAYERS_ATTACK;
        this.defense = Constant.MAX_INITIAL_PLAYERS_DEFENSE;
        this.equippedObjects = new ArrayList<>();
        this.race = race;
        this.playerClass = playerClass;
        this.level = level;
        this.gender = gender;
        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isDead = false;
        this.boardActionAuthorized = false;
    }

    /**
     * Sets whether the player is authorized for board actions.
     *
     * @param boardActionAuthorized True if board actions are authorized, false otherwise.
     */
    public void setBoardActionAuthorized(boolean boardActionAuthorized) {
        this.boardActionAuthorized = boardActionAuthorized;
    }

    /**
     * Checks if the player is authorized for board actions.
     *
     * @return True if board actions are authorized, false otherwise.
     */
    public boolean isBoardActionAuthorized() {
        return this.boardActionAuthorized;
    }

    /**
     * Checks if shopping is authorized for the player.
     *
     * @return True if shopping is authorized, false otherwise.
     */
    public boolean isShoppingAuthorized() {
        return this.isShoppingTime;
    }

    /**
     * Sets whether shopping is authorized for the player.
     *
     * @param authorized True if shopping is authorized, false otherwise.
     */
    public void setShoppingAuthorized(boolean authorized) {
        isShoppingTime = authorized;
    }

    // Methods for handling curses

    /**
     * Sets the curse status of the player.
     *
     * @param isCursed True if the player is cursed, false otherwise.
     */
    public void setCurse(boolean isCursed) {
        this.isCursed = isCursed;
    }

    /**
     * Gets the curse status of the player.
     *
     * @return True if the player is cursed, false otherwise.
     */
    public boolean getCurse() {
        return this.isCursed;
    }

    /**
     * Sets the defense of the player based on user input.
     *
     * @param card  The card representing the defense object.
     * @param board The game board.
     */
    public void setDefense(Card card, Board board) {
        if (Interact.yesOrNoDialog("Replace defense object?").equals("Yes")) {
            this.defense = card.getInfo().getLevelBonus();
        } else {
            System.out.println("No replacement!");
        }
    }

    /**
     * Gets the defense of the player.
     *
     * @return The defense value.
     */
    public int getDefense() {
        return defense;
    }

    public void decreaseDefense(int i) {
        this.defense = this.defense - i;
        Interact.showMessageDialog("Defense decreased: " + i);
        if(this.defense < 0){
            this.defense = 0;
        }
    }

    /**
     * Gets the player itself.
     *
     * @return The player object.
     */
    public Player getMe() {
        return this;
    }

    public boolean isDead(){
        return this.isDead;
    }

    public void becomeDead(){
        this.isDead = true;
    }

    public void becomeAlive(){
        this.isDead = false;
    }

    /**
     * Checks if the player can afford to purchase a card.
     *
     * @param card The card to be purchased.
     * @return True if the player can afford, false otherwise.
     */
    public boolean canAfford(Card card) {
        return getMoney() >= card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF;
    }

    /**
     * Performs a purchase of a card by the player.
     *
     * @param card The card to be purchased.
     */
    public void makePurchase(Card card) {
        addMoney(-(double) (card.getInfo().getLevelBonus() * Constant.OBJECTS_COST_COEFF));
        addToHand(card);
        Interact.showMessageDialog("Purchase successful! You bought the card " + card.getCardName());
    }

    /**
     * Sells a card for the player.
     *
     * @param card The card to be sold.
     */
    public void sellCard(Card card) {
        if (hand.size() > 3) {
            // Logic for selling the card (adjusting player's money, etc.)
            money += card.getInfo().getLevelBonus() * Constant.SELL_COST_COEFF;

            if (Shop.getShopCards().size() < Shop.getShopMaxCards()) {
                if (!card.isMonster()) {
                    Shop.getShopCards().add(card);
                    hand.remove(card);
                } else {
                    Interact.showMessageDialog("You cannot sell a monster card");
                }
            } else {
                if (card.getInfo().getCardType() == CardType.DUNGEON) {
                    Dungeon.getDiscardPile().add(card);
                } else if (card.getInfo().getCardType() == CardType.TREASURE) {
                    Treasure.getDiscardPile().add(card);
                }
                hand.remove(card);
                Interact.showMessageDialog("The shop is full of items, discarded!");

                if(card.getInfo().getCardType() == CardType.DUNGEON){
                    Dungeon.discard(card);
                }else{
                    Treasure.discard(card);
                }
            }

            // Display a confirmation message for the successful sale
            Interact.showMessageDialog("Sale successful! You sold the card " + card.getCardName());
        } else {
            // Display a message if the player doesn't have enough cards to sell
            Interact.showMessageDialog("Unable to sell. You must have at least 4 cards in hand.");
        }
    }


    /**
     * Takes the bonus of a game object card and equips it to the player.
     *
     * @param card The game object card to be equipped.
     */
    public void takeGameObjectBonus(Card card) {
        if (card.getInfo().getSubType() == SubType.TWO_HANDS) {
            // Check if both slots are occupied

            // Add the two-hand object, even if only one slot is occupied
            addAttackForce(card.getInfo().getLevelBonus());
            this.equippedObjects.add(card);
            updateAttackForce(this.equippedObjects);
            System.out.println("EQUIP: " + this.equippedObjects);
            System.out.println("ATTK: " + this.attackForce);

            // Check if only one slot is occupied
            if (equippedObjects.size() >= 1 && card.getInfo().getSubType() == SubType.TWO_HANDS) {
                // Display a dialog box to ask the player if they want to replace equipped objects
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to replace your current objects with the 'two-hand' object?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    // Replace the equipped object with the two-hand object
                    equippedObjects.clear();  // Remove all equipped objects
                    equippedObjects.add(card);  // Add the two-hand object
                    updateAttackForce(this.equippedObjects);
                    System.out.println("EQUIP: " + this.equippedObjects);
                    System.out.println("ATTK: " + this.attackForce);
                } else {
                    // The user chose not to replace the equipped object
                    System.out.println("No replacement made. You gain money as compensation");
                    addMoney(card.getInfo().getLevelBonus());
                }
            }

        } else {
            // Check if the maximum capacity is reached
            if (equippedObjects.size() <= 2) {
                // Add a new equipped object, ask the player to choose if there are already two equipped objects
                if (equippedObjects.size() == 2) {
                    showReplaceDialog(card);
                } else {
                    addAttackForce(card.getInfo().getLevelBonus());
                    this.equippedObjects.add(card);
                    updateAttackForce(this.equippedObjects);
                    System.out.println("EQUIP: " + this.equippedObjects);
                    System.out.println("ATTK: " + this.attackForce);
                }
            } else {
                System.out.println("You cannot carry more than 2 objects.");
                // You can also display a message to the user or take other actions
            }
        }
    }



    /**
     * Updates the player's attack force based on the equipped objects.
     *
     * @param equippedObjects The list of equipped objects.
     */
    public void updateAttackForce(List<Card> equippedObjects) {
        setAttackForce(Constant.MAX_INITIAL_PLAYERS_ATTACK);
        for (Card card : equippedObjects) {
            addAttackForce(card.getInfo().getLevelBonus());
        }
    }


    //----------------

    private void showReplaceDialog(Card newCard) {
        // Construire le message avec les noms des objets équipés actuels
        StringBuilder message = new StringBuilder("Vous avez déjà deux objets équipés. Choisissez un objet à remplacer:\n");
        for (int i = 0; i < equippedObjects.size(); i++) {
            message.append(i + 1).append(". ").append(equippedObjects.get(i).getCardName()).append("\n");
        }

        // Afficher la boîte de dialogue pour sélectionner l'objet à remplacer
        String[] options = equippedObjects.stream().map(card -> card.getCardName()).toArray(String[]::new);
        int choice = JOptionPane.showOptionDialog(null,
                message.toString(),
                "Remplacement d'objet",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice >= 0 && choice < equippedObjects.size()) {
            // Remplacer l'objet sélectionné
            Card replacedCard = equippedObjects.get(choice);
            equippedObjects.set(choice, newCard);
            updateAttackForce(this.equippedObjects);
            System.out.println("EQUIP: " + this.equippedObjects);
            System.out.println("ATTK: " + this.attackForce);

            // Afficher un message ou effectuer d'autres actions pour informer le joueur du remplacement
            JOptionPane.showMessageDialog(null,
                    "Objet remplacé: " + replacedCard.getCardName() + " par " + newCard.getCardName(),
                    "Remplacement effectué",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("Aucun objet n'a été remplacé.");
        }
    }


    //----------------


    public int getEquippedObjectsSize() {
        return equippedObjectsSize;
    }

    public List<Card> getEquippedObjects() {
        return equippedObjects;
    }

    public List<String> getEquippedObjectsNames() {
        List<String> equippedObjectsNames = new ArrayList<>();
        for (Card card : equippedObjects) {
            equippedObjectsNames.add(card.getCardName());
        }
        return equippedObjectsNames;
    }



    //----------------


    // Méthodes d'accès aux données du joueur

    /**
     * Définit le nom du joueur.
     *
     * @param name Le nouveau nom du joueur.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtient le nom du joueur.
     *
     * @return Le nom du joueur.
     */
    public String getName() {
        return this.name;
    }

    //score

    /**
     * Définit le score du joueur.
     *
     * @param score Le nouveau score du joueur.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Ajoute un score au joueur.
     *
     * @param score Le score à ajouter.
     */
    public void addScore(int score) {
        this.score += score;
        if(score >= 100){
            this.level++;
            score = 0;
        }
    }

    /**
     * Obtient le score du joueur.
     *
     * @return Le score du joueur.
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Réinitialise le score du joueur.
     */
    public void resetScore() {
        this.score = 0;
    }

    //turn

    /**
     * Ajoute un tour au joueur.
     */
    public void addTurn() {
        this.turn++;
    }

    /**
     * Définit le tour du joueur.
     *
     * @param turn Le nouveau tour du joueur.
     */
    public void setTurn(int turn) {
        this.turn = turn;
    }

    /**
     * Réinitialise le tour du joueur.
     */
    public void resetTurn() {
        this.turn = 0;
    }

    /**
     * Obtient le tour actuel du joueur.
     *
     * @return Le tour actuel du joueur.
     */
    public int getTurn() {
        return this.turn;
    }
    

    //number

    /**
     * Définit le numéro du joueur.
     *
     * @param number Le nouveau numéro du joueur.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Obtient le numéro du joueur.
     *
     * @return Le numéro du joueur.
     */
    public int getNumber() {
        return this.number;
    }

    
    //reset
    

    /**
     * Réinitialise les données du joueur.
     */
    public void reset() {
        this.resetTurn();
        this.resetScore();
    }


    //---------------------------
 

    /**
     * Ajoute des vies au joueur.
     *
     * @param lives Le nombre de vies à ajouter.
     */
    public void addLives(int lives) {
        if(this.lives < 100){
            this.lives += lives;
        }
        
        if (this.lives <= 0) {
            this.lives = 0;
            this.isDead = true;
            System.out.println("This player is dead !");
        }

        if(this.lives >100){
            this.lives = 100;
        }
    }

    /**
     * Ajoute de l'argent au joueur.
     *
     * @param money La quantité d'argent à ajouter.
     */
    public void addMoney(double money) {
        this.money += money;
        if (this.money <= 0) {
            this.money = 0;
        }
    }

    //attack

    /**
     * Définit la force d'attaque du joueur.
     *
     * @param attack La nouvelle force d'attaque.
     */
    public void setAttackForce(int attack) {
        this.attackForce = attack;
    }

    /**
     * Ajoute à la force d'attaque du joueur.
     *
     * @param attack L'augmentation de la force d'attaque.
     */
    public void addAttackForce(int attack) {
        this.attackForce += attack;
    }

    /**
     * Obtient la force d'attaque du joueur.
     *
     * @return La force d'attaque du joueur.
     */
    public int getAttackForce() {
        return this.attackForce;
    }



    //level

    /**
     * Augmente le niveau du joueur.
     *
     * @param levelsGained Les niveaux à ajouter.
     */
    public void gainLevel(int levelsGained) {
        this.level += levelsGained;
    }

    /**
     * Diminue le niveau du joueur.
     *
     * @param levelsLoosed Les niveaux à perdre.
     */
    public void loseLevel(int levelsLoosed) {
        if(this.level > 0){
            this.level -= levelsLoosed;
        }
    }



    // Méthodes pour manipuler les cartes en main du joueur

    /**
     * Ajoute une carte à la main du joueur.
     *
     * @param card La carte à ajouter.
     */
    public void addToHand(Card card) {
        this.hand.add(card);
    }

    /**
     * Ajoute une liste de cartes à la main du joueur.
     *
     * @param cards La liste de cartes à ajouter.
     */
    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
    }

    /**
     * Retire une carte spécifique de la main du joueur.
     *
     * @param card La carte à retirer.
     */
    public void removeFromHand(Card card) {
        Iterator<Card> iterator = this.hand.iterator();
        while (iterator.hasNext()) {
            Card currentCard = iterator.next();
            if (currentCard.equals(card)) {
                iterator.remove();
                break;  // On suppose que chaque carte est unique dans la main
            }
        }
    }

    /**
     * Retire une liste de cartes de la main du joueur.
     *
     * @param cards La liste de cartes à retirer.
     */
    public void removeFromHand(List<Card> cards) {
        List<Card> cardsToRemove = new ArrayList<>();
        for (Card c : cards) {
            if (this.hand.contains(c)) {
                cardsToRemove.add(c);
            }
        }
        this.hand.removeAll(cardsToRemove);
    }


    /**
     * Obtient une carte spécifique de la main du joueur et la retire de la main.
     *
     * @param index L'index de la carte dans la main.
     * @return La carte obtenue.
     */
    public Card getFromHand(int index) {
        return this.hand.remove(index);
    }


    /**
     * Obtient la main du joueur.
     *
     * @return La liste des cartes en main du joueur.
     */
    public List<Card> getHand() {
        return this.hand;
    }

    /**
     * Obtient une représentation textuelle des informations du joueur.
     *
     * @return Les informations du joueur.
     */
    public String getInfo() {
        return "Player " + this.number + " Name : " + this.name + " Score - " + this.score + " Turn - " + this.turn;
    }


    //--------------------


    /**
     * Définit le niveau du joueur, en évitant les valeurs négatives.
     *
     * @param level Le nouveau niveau du joueur.
     */
    public void setLevel(int level) {
        if (level >= 0) {
            this.level = level;
        } else {
            System.out.println("Level cannot be set to a negative value.");
        }
    }

    /**
     * Obtient le niveau du joueur.
     *
     * @return Le niveau du joueur.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Obtient le nombre de vies du joueur.
     *
     * @return Le nombre de vies du joueur.
     */
    public int getLives() {
        return this.lives;
    }

    /**
     * Obtient la quantité d'argent du joueur.
     *
     * @return La quantité d'argent du joueur.
     */
    public double getMoney() {
        return this.money;
    }

    //------------------------

    /**
     * Définit le genre du joueur.
     *
     * @param gender Le nouveau genre du joueur.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Obtient le genre du joueur.
     *
     * @return Le genre du joueur.
     */
    public String getGender() {
        return this.gender;
    }

    /**
     * Définit la classe du joueur.
     *
     * @param playerClass La nouvelle classe du joueur.
     */
    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    /**
     * Obtient la classe du joueur.
     *
     * @return La classe du joueur.
     */
    public String getPlayerClass() {
        return this.playerClass;
    }

    /**
     * Définit la race du joueur.
     *
     * @param race La nouvelle race du joueur.
     */
    public void setRace(String race) {
        this.race = race;
    }

    /**
     * Obtient la race du joueur.
     *
     * @return La race du joueur.
     */
    public String getRace() {
        return this.race;
    }

    //---------------------
    // Autres méthodes d'information

    /**
     * Indique si le joueur a rencontré un monstre.
     *
     * @return Vrai si le joueur a rencontré un monstre, sinon faux.
     */
    public boolean hasEncounteredMonster() {
        return this.hasEncounteredMonster;
    }

    /**
     * Définit si le joueur a rencontré un monstre.
     *
     * @param hasEncounteredMonster Vrai si le joueur a rencontré un monstre, sinon faux.
     */
    public void setHasEncounteredMonster(boolean hasEncounteredMonster) {
        this.hasEncounteredMonster = hasEncounteredMonster;
    }



    //--------------------


    // Getter method for drawDungeon flag
    public boolean isDrawDungeon() {
        return drawDungeon;
    }

    // Setter method for drawDungeon flag
    public void setDrawDungeon(boolean b) {
        drawDungeon = b;
    }

    public void setDrawTreasure(boolean b) {
        drawTreasure = b;
    }

    public boolean isDrawTreasure() {
        return drawTreasure;
    }

}
