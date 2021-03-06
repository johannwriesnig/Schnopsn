package com.schnopsn.core.game.turns;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardPair;

public class AnsagenTurn extends Turn {
    private CardPair cardPair;

    public AnsagenTurn(){}

    @Override
    public Card getPlayedCard() {
        return cardPair.getCard1();
    }

    public AnsagenTurn(CardPair cardPair){
        this.cardPair = cardPair;
    }

    public CardPair getCardPair() {
        return cardPair;
    }

    public boolean isCallable(){
        return false;
    }

}
