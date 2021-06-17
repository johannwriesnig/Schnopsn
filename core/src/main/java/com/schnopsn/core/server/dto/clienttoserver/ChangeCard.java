package com.schnopsn.core.server.dto.clienttoserver;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.server.dto.BaseMessage;

public class ChangeCard extends BaseMessage {
    private Card cardToChange;

    public ChangeCard(){}

    public ChangeCard(Card cardToChange){
        this.cardToChange = cardToChange;
    }

    public Card getCardToChange() {
        return cardToChange;
    }
}
