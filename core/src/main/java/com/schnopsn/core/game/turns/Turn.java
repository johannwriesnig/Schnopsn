package com.schnopsn.core.game.turns;

import com.schnopsn.core.game.cards.Card;

public abstract class Turn {
    public Turn(){}

    public abstract Card getPlayedCard();

}
