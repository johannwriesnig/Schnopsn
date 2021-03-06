package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CollectedDeck;
import com.schnopsn.core.game.cards.HandDeck;

public class Player {
    private int id;
    private String name;
    private HandDeck handDeck;
    private CollectedDeck collectedDeck;
    private int bummerl=7;


    public Player(){

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HandDeck getHandDeck() {
        return handDeck;
    }

    public void setDeck(HandDeck handDeck) {
        this.handDeck = handDeck;
    }

    public CollectedDeck getCollectedDeck() {
        return collectedDeck;
    }

    public void setCollectedDeck(CollectedDeck collectedDeck) {
        this.collectedDeck = collectedDeck;
    }

    public int getBummerl() {
        return bummerl;
    }

    public void setBummerl(int bummerl) {
        this.bummerl = bummerl;
    }

}
