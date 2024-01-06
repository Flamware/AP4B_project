package main.java.com.utmunchkin.Interface;

import main.java.com.utmunchkin.Constant;
import main.java.com.utmunchkin.cards.Card;
import main.java.com.utmunchkin.cards.CardData.CardInfo;
import main.java.com.utmunchkin.cards.Treasure;
import main.java.com.utmunchkin.gameplay.Play;
import main.java.com.utmunchkin.players.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    private static List<Card> shopCards;
    private boolean playerMadeChoice = false;
    private static final int nbShopCardsMax = Constant.SHOP_MAX_ITEMS;
    private static ShopDialog sd;

    public Shop() {
        shopCards = new ArrayList<>();
        // Chargez initialement trois cartes dans le magasin
        //loadCardsToShop(3);
    }

    public static List<Card> getShopCards() {
        return shopCards;
    }

    public static int getShopMaxCards() {
        return nbShopCardsMax;
    }

    // Méthode pour charger des cartes dans le magasin si nécessaire
    public void loadCardsToShop() {
        System.out.println("Size shop : " + shopCards.size());
        if (shopCards.size() < 3) {
            Card card = Treasure.draw();
            System.out.println("added to shop : " + card.getCardName());
            shopCards.add(card);
        }

        for(Card c : shopCards){
            System.out.println(c.getCardName());
        }
    }


    public void openShopDialog(Player player) {
        SwingUtilities.invokeLater(() -> new ShopDialog(player));
    }

    public boolean purchaseCard(Player player, Card purchasedCard) {
        if (shopCards.contains(purchasedCard) && player.canAfford(purchasedCard)) {
            openShopDialog(player);
            return true;
        } else {
            return false;
        }
    }

    public boolean isPlayerMadeChoice() {
        return playerMadeChoice;
    }

    public void setMadeChoice(boolean b) {
        this.playerMadeChoice = b;
    }


    // Classe interne ShopDialog
    public class ShopDialog extends JDialog {
        private Player currentPlayer;
        private Card selectedCd;
        JPanel statsPanel = new JPanel();
        JPanel cardstatsPanel;
        private JPanel cardButtonsPanel;  // Nouveau panneau pour les boutons radio

        public ShopDialog(Player currentPlayer) {
            this.currentPlayer = currentPlayer;
            this.selectedCd = new Card();

            setTitle("Boutique");
            setSize(800, 800);
            getContentPane().setBackground(new Color(50, 50, 50));  // Définit le fond en gris foncé
            setLayout(new BorderLayout());

            // Ajoutez ici des composants Swing pour afficher les statistiques, les boutons d'achat, etc.
            cardstatsPanel = new JPanel();
            cardstatsPanel.setBackground(new Color(50, 50, 50));
            JLabel statsLabel = new JLabel("SHOP -->");
            statsPanel.add(statsLabel);

            cardButtonsPanel = new JPanel();  // Initialisez le nouveau panneau
            statsPanel.add(cardButtonsPanel);  // Ajoutez-le à statsPanel

            // Ajoutez un groupe de boutons pour les cartes de la boutique
            ButtonGroup cardGroup = new ButtonGroup();

            // Parcourez chaque carte dans la boutique
            for (Card card : shopCards) {
                JRadioButton cardButton = new JRadioButton(card.getCardName());
                cardButton.setActionCommand(card.getCardName()); // Utilisez le nom de la carte comme commande

                // Ajoutez un écouteur d'événements pour le bouton de la carte
                cardButton.addActionListener(e -> handleCardSelection(card));

                // Ajoutez le bouton de la carte au groupe de boutons
                cardGroup.add(cardButton);

                cardButtonsPanel.add(cardButton);
            }

            // Ajoutez des boutons d'achat et de vente
            JButton buyButton = new JButton("Acheter");
            JButton sellButton = new JButton("Vendre");
            JButton quitButton = new JButton("Quitter");

            buyButton.addActionListener(e -> handleBuyButtonClick());
            sellButton.addActionListener(e -> handleSellButtonClick());
            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    playerMadeChoice = true;
                }
            });

            // Ajoutez un gestionnaire d'événements pour détecter la fermeture de la fenêtre
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // Indiquez que le joueur a fait un choix lorsque la fenêtre est fermée
                    playerMadeChoice = true;
                }
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(buyButton);
            buttonPanel.add(sellButton);
            buttonPanel.add(quitButton);

            add(statsPanel, BorderLayout.NORTH);
            add(cardstatsPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            updateShopButtons(); // Mettez à jour les boutons initiaux

            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setModal(true);
            setVisible(true);
        }

        private void updateShopButtons() {
            // Nettoyez le panneau des boutons radio existants
            cardButtonsPanel.removeAll();
    
            // Ajoutez un groupe de boutons pour les cartes de la boutique
            ButtonGroup cardGroup = new ButtonGroup();
    
            // Parcourez chaque carte dans la boutique
            for (Card card : shopCards) {
                JRadioButton cardButton = new JRadioButton(card.getCardName());
                cardButton.setActionCommand(card.getCardName()); // Utilisez le nom de la carte comme commande
    
                // Ajoutez un écouteur d'événements pour le bouton de la carte
                cardButton.addActionListener(e -> handleCardSelection(card));
    
                // Ajoutez le bouton de la carte au groupe de boutons
                cardGroup.add(cardButton);
    
                cardButtonsPanel.add(cardButton);  // Ajoutez le bouton au nouveau panneau
            }
    
            // Repaint pour refléter les changements
            cardButtonsPanel.revalidate();
            cardButtonsPanel.repaint();
        }

        private void handleCardSelection(Card selectedCard) {
            // Ajoutez ici la logique pour gérer la carte sélectionnée
            // Par exemple, vous pouvez stocker la carte sélectionnée dans une variable
            this.selectedCd = selectedCard;
            // Afficher les statistiques de la carte sélectionnée
            displayCardStatistics(selectedCard);
        }

        private void displayCardStatistics(Card card) {
            // Obtenez l'objet CardInfo de la carte
            CardInfo cardInfo = card.getInfo();
        
            // Créez un GridLayout avec une colonne pour le panneau de statistiques
            GridLayout gridLayout = new GridLayout(0, 1);
            cardstatsPanel.setLayout(gridLayout);
        
            // Ajoutez ici la logique pour afficher les statistiques de la carte
            // Par exemple, vous pouvez utiliser un JLabel dans statsPanel
            JLabel statsLabel = new JLabel("You have : " + Play.getCurrentPlayer().getMoney() + " money");
            statsLabel.setForeground(Color.GREEN);
        
            // Ajoutez une image à partir du chemin de l'image (ajustez selon votre structure de projet)
            String imagePath = "src/main/java/com/utmunchkin/gameplay/img/game/" + card.getCardName() + ".png";
            ImageIcon cardImage = new ImageIcon(imagePath);
            JLabel imageLabel = new JLabel(cardImage);
        
            // Ajoutez des détails supplémentaires ici en utilisant les méthodes de la classe CardInfo
            JLabel descriptionLabel = new JLabel(cardInfo.getCardName() + " Description : " + cardInfo.getDescription());
            JLabel levelBonusLabel = new JLabel("Bonus/Valeur : " + cardInfo.getLevelBonus());
            JLabel priceLabel = new JLabel("Prix : " + cardInfo.getLevelBonus() * Constant.OBJECTS_COST_COEFF);
            JLabel isCursedLabel = new JLabel("Objet Maudit ? : " + cardInfo.isCursed());
            JLabel cardTypeLabel = new JLabel("Type de carte : " + cardInfo.getCardType());
            JLabel subTypeLabel = new JLabel("Sous-type : " + cardInfo.getSubType());

            // Ajoutez d'autres labels pour les statistiques supplémentaires
        
            // Remplacez le label précédent s'il existe
            cardstatsPanel.removeAll();
            cardstatsPanel.setBackground(Color.BLACK);
        
            // Ajoutez les composants au panneau avec le GridLayout
            cardstatsPanel.add(statsLabel);
            cardstatsPanel.add(imageLabel);  // Ajoutez l'image
            cardstatsPanel.add(Box.createVerticalStrut(10));  // Ajoutez un espace vertical
        
            cardstatsPanel.add(descriptionLabel);
            cardstatsPanel.add(levelBonusLabel);
            cardstatsPanel.add(priceLabel);
            cardstatsPanel.add(isCursedLabel);
            cardstatsPanel.add(cardTypeLabel);
            cardstatsPanel.add(subTypeLabel);
            // Ajoutez d'autres labels pour les statistiques supplémentaires
        
            cardstatsPanel.revalidate();
            cardstatsPanel.repaint();
        }
        
                


        private void handleBuyButtonClick() {
            Player currentPlayer = Play.getCurrentPlayer();

            if (currentPlayer.isShoppingAuthorized() && currentPlayer.canAfford(selectedCd)) {
                if (Interact.confirmPurchase(selectedCd)) {
                    currentPlayer.makePurchase(selectedCd);
                    shopCards.remove(selectedCd);
                }
            } else {
                Interact.showMessageDialog("Achat non autorisé ou fonds insuffisants.");
            }

            // Indiquez que le joueur a fait un choix
            playerMadeChoice = true;

            // Fermez la boîte de dialogue après l'achat
            dispose();
        }

        private void handleSellButtonClick() {
            // Ajoutez ici la logique pour la vente
            // Ouvrez une boîte de dialogue pour sélectionner les cartes à vendre
            openSellDialog();

            // Indiquez que le joueur a fait un choix
            playerMadeChoice = true;

            // Fermez la boîte de dialogue après la vente
            dispose();
        }

        private void openSellDialog() {
            SellDialog sellDialog = new SellDialog(currentPlayer);
            sellDialog.setVisible(true);
        }

    }

    public class SellDialog extends JDialog {
        private Player currentPlayer;
        private ArrayList<Card> selectedCards;
    
        public SellDialog(Player currentPlayer) {
            this.currentPlayer = currentPlayer;
            this.selectedCards = new ArrayList<>();
    
            setTitle("Vendre des cartes");
            setSize(400, 200);
            setLayout(new BorderLayout());
    
            // Ajoutez ici des composants Swing pour afficher les cartes du joueur
            JPanel cardsPanel = new JPanel();
            JLabel cardsLabel = new JLabel("Sélectionnez les cartes à vendre");
            cardsPanel.add(cardsLabel);
    
            // Ajoutez des boutons de checkbox pour chaque carte dans la main du joueur
            for (Card card : currentPlayer.getHand()) {
                JCheckBox checkBox = new JCheckBox(card.getCardName());
                checkBox.addActionListener(e -> handleCheckboxSelection(checkBox, card));
                cardsPanel.add(checkBox);
            }
    
            // Ajoutez un bouton de confirmation pour la vente
            JButton sellButton = new JButton("Vendre");
            sellButton.addActionListener(e -> handleSellButtonClick());
            cardsPanel.add(sellButton);
    
            add(cardsPanel, BorderLayout.CENTER);
    
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setModal(true);
        }
    
        private void handleCheckboxSelection(JCheckBox checkBox, Card card) {
            if (checkBox.isSelected()) {
                // Ajoutez la carte à la liste des cartes sélectionnées
                selectedCards.add(card);
            } else {
                // Retirez la carte de la liste des cartes sélectionnées
                selectedCards.remove(card);
            }
    
            // Vérifiez le nombre total de cartes dans la main du joueur
            int totalCards = currentPlayer.getHand().size() - selectedCards.size();
    
            // Désactivez toutes les autres options si le nombre de cartes est inférieur à 4
            for (Component component : checkBox.getParent().getComponents()) {
                if (component instanceof JCheckBox) {
                    JCheckBox otherCheckBox = (JCheckBox) component;
                    if (otherCheckBox != checkBox) {
                        otherCheckBox.setEnabled(totalCards >= 4);
                    }
                }
            }
        }
    
        private void handleSellButtonClick() {
            // Ajoutez ici la logique pour vendre les cartes sélectionnées
            for(Card c : selectedCards){
                currentPlayer.sellCard(c);
            }
    
            setMadeChoice(true);

            // Fermez la boîte de dialogue après la vente
            dispose();
        }
    }
    

}


/*
    public void handleCardPurchase(Card clickedCard) {
        Player currentPlayer = getCurrentPlayer();

        if (currentPlayer.isShoppingAuthorized() && currentPlayer.canAfford(clickedCard)) {
            if (Interact.confirmPurchase(clickedCard)) {
                currentPlayer.makePurchase(clickedCard);
            }
        } else {
            Interact.showMessageDialog("Achat non autorisé ou fonds insuffisants.");
        }
    }

    public void handleSellButtonClick(Card selectedCard) {
        getCurrentPlayer().sellCard(selectedCard);
    }
 */