package com.schnopsn.core.game.cards;

import java.util.ArrayList;
import java.util.Collections;


public class DeckBuilder {
    private HandDeck HandDeck1;
    private HandDeck HandDeck2;
    private DrawDeck drawDeck;

    public void buildDecks(){
        ArrayList<Card> allCards = getAllCards();
        shuffleCards(allCards);

        Card[] firstDeck = getDeck(allCards);
        Card[] secondDeck = getDeck(allCards);

        HandDeck1 = new HandDeck(firstDeck);
        HandDeck2 = new HandDeck(secondDeck);
        drawDeck = new DrawDeck(allCards);

    }

    public void shuffleCards(ArrayList<Card> cards){
        for(int i=0; i<10;i++)
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getAllCards(){
        ArrayList<Card> allCards = new ArrayList<>();
        for(CardColor color: CardColor.values()){
            for(CardValue value:CardValue.values()){
                allCards.add(new Card(color,value));
            }
        }
        return allCards;
    }

    private Card[] getDeck(ArrayList<Card> allCards){
        if(allCards.size()<5) return null;
        Card[] deck = new Card[5];
        for(int i = 0; i < deck.length; i++) {
            deck[i] = allCards.remove(i);
        }
        return deck;

    }

    public HandDeck getHandDeck1() {
        return HandDeck1;
    }

    public HandDeck getHandDeck2() {
        return HandDeck2;
    }

    public DrawDeck getDrawDeck() {
        return drawDeck;
    }
}
