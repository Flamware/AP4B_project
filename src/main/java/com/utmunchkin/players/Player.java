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
    private String name;
    private int score;
    private int lives;
    private double money;
    private int attackForce;
    private int defense;
    private List<Card> equipedObjects;
    private int turn;
    private int number;
    private List<Card> hand; // Use List interface instead of ArrayList

    private String race;
    private String playerClass;
    private int level;
    private String gender;

    private boolean hasEncounteredMonster;
    private Board board;
    private int equipedObjectsSize;
    private boolean isCursed;
    private boolean isdead;

    // Constructors

    public Player(String name, int number) {
        this.name = name;
        this.score = 0;
        this.lives = Constant.MAX_INITIAL_PLAYERS_LIVES;
        this.money = Constant.MAX_INITIAL_PLAYERS_MONEY;
        this.attackForce = Constant.MAX_INITIAL_PLAYERS_ATTACK;
        this.defense = Constant.MAX_INITIAL_PLAYERS_DEFENSE;
        this.equipedObjects = new ArrayList<>();
        this.turn = 0;
        this.number = number;
        this.hand = new ArrayList<>(); // Initialize hand

        // Set default values for race, class, level, and gender
        this.race = "Human";
        this.playerClass = "No Class";
        this.level = 1;
        this.gender = "Player Defined";

        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isdead = false;
    }

    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck) {
        this(name, number); // Call the default constructor to initialize basic fields
        this.hand = new ArrayList<>(); // Initialize hand

        // Distribute les cartes au joueur lors de la construction
        cardDeck.distributeDungeonTreasureCardToPlayer(this, numberOfInitialCardsForEachDeck);
    }

    public Player(String name, int number, Cards cardDeck, int numberOfInitialCardsForEachDeck,
                  String race, String playerClass, int level, String gender) {
        this(name, number); // Call the default constructor to initialize basic fields
        this.hand = new ArrayList<>(); // Initialize hand
                
        this.lives = Constant.MAX_INITIAL_PLAYERS_LIVES;
        this.money = Constant.MAX_INITIAL_PLAYERS_MONEY;
        this.attackForce = Constant.MAX_INITIAL_PLAYERS_ATTACK;
        this.defense = Constant.MAX_INITIAL_PLAYERS_DEFENSE;
        this.equipedObjects = new ArrayList<>();

        // Set specified values for race, class, level, and gender
        this.race = race;
        this.playerClass = playerClass;
        this.level = level;
    
        this.gender = gender;

        this.hasEncounteredMonster = false;
        this.isCursed = false;
        this.isdead = false;

        // Distribute cards to the player during construction
        cardDeck.distributeDungeonTreasureCardToPlayer(this, numberOfInitialCardsForEachDeck);
    }

    public void setCurse(boolean b){this.isCursed = b;}
    public boolean getCurse(){return this.isCursed;}

    public void setDefense(Card c){
        if(board.yesOrNoDialog("Remplacer l'objet de défense ?").equals("Oui")){
            this.defense = c.getInfo().getLevelBonus(); 
        }else{
            System.out.println("objet non remplacé !");
        }
    }

    public void takeGameObjectBonus(Card c) {
        if (this.equipedObjects.isEmpty()) {
            equipedObjects.add(c);
            if (c.getInfo().getTreasureType() == TreasureType.TWO_HANDS) {
                this.equipedObjectsSize = 2;
            } else {
                this.equipedObjectsSize = this.equipedObjects.size();
            }
            updateAttackForce(equipedObjects);
        } else if (this.equipedObjects.size() < 2 && !(this.equipedObjects.get(0).getInfo().getTreasureType() == TreasureType.TWO_HANDS)) {
            equipedObjects.add(c);
            this.equipedObjectsSize = equipedObjects.size() + 1;
            updateAttackForce(equipedObjects);
        } else if (equipedObjects.size() == 0 && c.getInfo().getTreasureType() == TreasureType.TWO_HANDS) {
            equipedObjects.add(c);
            this.equipedObjectsSize = 2;
        } else {
            if (board.yesOrNoDialog("Vos deux mains sont occupées, Voulez vous remplacer l'objet").equals("Oui")) {
                this.equipedObjects.clear();
                this.equipedObjects.add(c);
    
                if (equipedObjects.size() == 0 && c.getInfo().getTreasureType() == TreasureType.TWO_HANDS) {
                    this.equipedObjectsSize = 2;
                } else {
                    this.equipedObjectsSize = this.equipedObjects.size();
                }
    
            } else {
                System.out.println("objet non remplacé !");
            }
        }
    }
    

    public void updateAttackForce(List<Card> equipedObjects){
        setAttackForce(MAX_INITIAL_PLAYERS_ATTACK);
        for(Card c: equipedObjects){
            addAttackForce(c.getInfo().getLevelBonus());
        }
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public int getTurn() {
        return this.turn;
    }

    public int getNumber() {
        return this.number;
    }

    public void addScore(int score) {
        this.score += score;
    }
    public void addLives(int lives){
        this.lives += lives;
        if(this.lives <= 0){this.lives = 0; this.isdead = true;}
    }

    public void addMoney(double money){
        this.money += money;
        if(this.money <= 0){this.money = 0;}
    }

    public void setAttackForce(int attck){
        this.attackForce = attck;
    }

    public void addAttackForce(int attck){
        this.attackForce += attck;
    }

    public int getAttackForce(){
        return this.attackForce;
    }

    public void addTurn() {
        this.turn++;
    }

    public void resetTurn() {
        this.turn = 0;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void reset() {
        this.resetTurn();
        this.resetScore();
    }


    public void gainLevel(int levelsGained) {
        this.level += levelsGained;
    }

    public void loseLevel(int levelsLoosed) {
        this.level -= levelsLoosed;
    }

    public void addToHand(Card card) {
        this.hand.add(card);
    }
    public Card getFromHand(int i){
        return this.hand.remove(i);
    }

    public void removeFromHand(Card card) {
        Iterator<Card> iterator = this.hand.iterator();
        while (iterator.hasNext()) {
            Card currentCard = iterator.next();
            if (currentCard.equals(card)) {
                iterator.remove();
                break;  // Assuming each card is unique in the hand
            }
        }
    }
    
    public void addToHand(List<Card> cards) {
        this.hand.addAll(cards);
    }

    public void removeFromHand(List<Card> cards) {
        this.hand.removeAll(cards);
    }

    public List<Card> getHand() {
        return this.hand;
    }

    public String getRace() {
        return this.race;
    }

    public String getPlayerClass() {
        return this.playerClass;
    }

    public int getLives(){
        return this.lives;
    }

    public double getMoney(){
        return this.money;
    }

    public int getLevel() {
        return this.level;
    }
    public String getInfo() {
        return "Player " + this.number + " Name : " + this.name + " Score - " + this.score + " Turn - " + this.turn;
    }

    public String getGender() {
        return this.gender;
    }


    public void setRace(String race) {
        this.race = race;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public void setLevel(int level) {
        // Ensure that the level is not set to a negative value
        if (level >= 0) {
            this.level = level;
        } else {
            System.out.println("Level cannot be set to a negative value.");
        }
    }


    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean hasEncounteredMonster() {
        return this.hasEncounteredMonster;
    }

    public void setHasEncounteredMonster(boolean b) {
        this.hasEncounteredMonster = b;
    }
}
