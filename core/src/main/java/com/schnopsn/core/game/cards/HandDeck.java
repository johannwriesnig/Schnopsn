package com.schnopsn.core.game.cards;

public class HandDeck {
    private Card[] deck;

    public HandDeck(){
    }

    public HandDeck(Card[] deck){
        this.deck = deck;
    }

    public void setDeck(Card[] deck) {
        this.deck = deck;
    }

    public boolean contains(Card cardToCompare){
        for(Card deckCard: deck){
            if(deckCard!=null && deckCard.getCardColor()==cardToCompare.getCardColor() && deckCard.getCardValue()==cardToCompare.getCardValue())return true;
        }
        return false;
    }

    public int getCardIndex(Card card){
        int index = -1;

        for(int i =0; i<deck.length;i++){
            if(deck[i]!=null&&deck[i].getCardValue()==card.getCardValue() && deck[i].getCardColor()==card.getCardColor()){
                index = i;
                break;
            }
        }

        return index;
    }

    public void remove(Card card){
        for(int i=0; i<deck.length;i++){
            if(deck[i].getCardColor()==card.getCardColor() && deck[i].getCardValue()==card.getCardValue())deck[i]=null;
        }
    }
    public void add(Card card){
        for(int i=0; i<deck.length;i++){
            if(deck[i]==null)deck[i]=card;
        }
    }

    public Card[] getDeck() {
        return deck;
    }
}
