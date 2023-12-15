package main.java.com.utmunchkin.players;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.Interface.Board;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.cards.Cards;
import main.java.com.utmunchkin.cards.CardData.TreasureType;

public class Player extends ListOfPlayer {
    // Données du joueur
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
    private Board board;
    private int equippedObjectsSize;
    private boolean isCursed;
    private boolean isDead;

    // Constructeurs

    // Constructeur par défaut
    public Player(String name, int number) {
        // Initialisation des champs avec des valeurs par défaut
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

        // Valeurs par défaut pour race, classe, niveau et genre
        this.race = "Human";
        this.playerClass = "No Class";
        this.level = 1;
        this.gender = "Player Defined";

        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isDead = false;
    }

    // Constructeur avec distribution de cartes
    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck) {
        this(name, number); // Appel du constructeur par défaut pour initialiser les champs de base
        this.hand = new ArrayList<>();

        // Distribution des cartes au joueur lors de la construction
        cardDeck.distributeDungeonTreasureCardToPlayer(this, numberOfInitialCardsForEachDeck);
    }

    // Constructeur avec distribution de cartes et spécification de race, classe, niveau et genre
    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck,
                  String race, String playerClass, int level, String gender) {
        this(name, number); // Appel du constructeur par défaut pour initialiser les champs de base
        this.hand = new ArrayList<>();

        // Réinitialisation de certaines valeurs pour éviter les duplications
        this.lives = Constant.MAX_INITIAL_PLAYERS_LIVES;
        this.money = Constant.MAX_INITIAL_PLAYERS_MONEY;
        this.attackForce = Constant.MAX_INITIAL_PLAYERS_ATTACK;
        this.defense = Constant.MAX_INITIAL_PLAYERS_DEFENSE;
        this.equippedObjects = new ArrayList<>();

        // Spécification des valeurs pour race, classe, niveau et genre
        this.race = race;
        this.playerClass = playerClass;
        this.level = level;
        this.gender = gender;

        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isDead = false;

        // Distribution des cartes au joueur lors de la construction
        cardDeck.distributeDungeonTreasureCardToPlayer(this, numberOfInitialCardsForEachDeck);
    }

    // Méthodes pour manipuler les malédictions
    public void setCurse(boolean isCursed) {
        this.isCursed = isCursed;
    }

    public boolean getCurse() {
        return this.isCursed;
    }

    // Méthode pour définir la défense du joueur
    public void setDefense(Card card) {
        if (board.yesOrNoDialog("Remplacer l'objet de défense ?").equals("Oui")) {
            this.defense = card.getInfo().getLevelBonus();
        } else {
            System.out.println("objet non remplacé !");
        }
    }

    // Méthode pour gérer les objets équipés par le joueur
    public void takeGameObjectBonus(Card card) {
        // Logique pour gérer les objets équipés
    }

    // Méthode pour mettre à jour la force d'attaque du joueur
    public void updateAttackForce(List<Card> equippedObjects) {
        setAttackForce(Constant.MAX_INITIAL_PLAYERS_ATTACK);
        for (Card card : equippedObjects) {
            addAttackForce(card.getInfo().getLevelBonus());
        }
    }

    // Méthodes d'accès aux données du joueur

    /**
     * Obtient le nom du joueur.
     *
     * @return Le nom du joueur.
     */
    public String getName() {
        return this.name;
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
     * Obtient le tour actuel du joueur.
     *
     * @return Le tour actuel du joueur.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Obtient le numéro du joueur.
     *
     * @return Le numéro du joueur.
     */
    public int getNumber() {
        return this.number;
    }

    // Méthodes pour manipuler les niveaux, l'argent, etc.

    /**
     * Ajoute un score au joueur.
     *
     * @param score Le score à ajouter.
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * Ajoute des vies au joueur.
     *
     * @param lives Le nombre de vies à ajouter.
     */
    public void addLives(int lives) {
        this.lives += lives;
        if (this.lives <= 0) {
            this.lives = 0;
            this.isDead = true;
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

    /**
     * Ajoute un tour au joueur.
     */
    public void addTurn() {
        this.turn++;
    }

    /**
     * Réinitialise le tour du joueur.
     */
    public void resetTurn() {
        this.turn = 0;
    }

    /**
     * Réinitialise le score du joueur.
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Définit le nom du joueur.
     *
     * @param name Le nouveau nom du joueur.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Définit le numéro du joueur.
     *
     * @param number Le nouveau numéro du joueur.
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Définit le score du joueur.
     *
     * @param score Le nouveau score du joueur.
     */
    public void setScore(int score) {
        this.score = score;
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
     * Réinitialise les données du joueur.
     */
    public void reset() {
        this.resetTurn();
        this.resetScore();
    }

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
        this.level -= levelsLoosed;
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
     * Obtient une carte spécifique de la main du joueur et la retire de la main.
     *
     * @param index L'index de la carte dans la main.
     * @return La carte obtenue.
     */
    public Card getFromHand(int index) {
        return this.hand.remove(index);
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
     * Ajoute une liste de cartes à la main du joueur.
     *
     * @param cards La liste de cartes à ajouter.
     */
    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
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
     * Obtient la main du joueur.
     *
     * @return La liste des cartes en main du joueur.
     */
    public List<Card> getHand() {
        return this.hand;
    }

    // Autres méthodes d'information

    /**
     * Obtient la race du joueur.
     *
     * @return La race du joueur.
     */
    public String getRace() {
        return this.race;
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

    /**
     * Obtient le niveau du joueur.
     *
     * @return Le niveau du joueur.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Obtient une représentation textuelle des informations du joueur.
     *
     * @return Les informations du joueur.
     */
    public String getInfo() {
        return "Player " + this.number + " Name : " + this.name + " Score - " + this.score + " Turn - " + this.turn;
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
     * Définit la race du joueur.
     *
     * @param race La nouvelle race du joueur.
     */
    public void setRace(String race) {
        this.race = race;
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
     * Définit le genre du joueur.
     *
     * @param gender Le nouveau genre du joueur.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

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

}
