package com.schnopsn.core.game.cards;

import java.util.ArrayList;

public class CollectedDeck {
    private ArrayList<CardPair> collectedDeck;
    private int callPoints;

    public CollectedDeck(){
        collectedDeck = new ArrayList<>();
        callPoints=0;
    }

    public void add(CardPair cardPair){
        collectedDeck.add(cardPair);
    }

    public ArrayList<CardPair> getCollectedDeck() {
        return collectedDeck;
    }

    public void addCallPoints(int points){
        callPoints+=points;
    }

    public int getPoints(){
        int points=0;
        for(CardPair cardPair: collectedDeck){
            points+=cardPair.getCard1().getCardValue().getValue();
        }
        if(points>0)points+=callPoints;
        return points;
    }
}
