package com.schnopsn.core.game.cards;

import java.util.ArrayList;

public class DrawDeck {
    ArrayList<Card> drawDeck;

    public DrawDeck(){}

    public DrawDeck(ArrayList<Card> drawDeck){
        this.drawDeck=drawDeck;
    }

    public ArrayList<Card> getDrawDeck() {
        return drawDeck;
    }

    public void setDrawDeck(ArrayList<Card> drawDeck) {
        this.drawDeck = drawDeck;
    }

    public Card drawCard(){
        return drawDeck.remove(0);
    }
}
