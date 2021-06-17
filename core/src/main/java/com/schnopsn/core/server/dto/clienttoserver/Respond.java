package com.schnopsn.core.server.dto.clienttoserver;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.server.dto.BaseMessage;

public class Respond extends BaseMessage {
    private Card responseCard;

    public Respond(){
    }

    public Respond(Card responseCard){
        this.responseCard = responseCard;
    }

    public Card getResponseCard() {
        return responseCard;
    }
}
