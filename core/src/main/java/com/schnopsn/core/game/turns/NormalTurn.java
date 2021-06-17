package com.schnopsn.core.game.turns;

import com.schnopsn.core.game.cards.Card;

public class NormalTurn extends Turn {
    private Card playedCard;

    public NormalTurn(){}

    public NormalTurn(Card playedCard){
        this.playedCard = playedCard;
    }

    public Card getPlayedCard() {
        return playedCard;
    }
}
