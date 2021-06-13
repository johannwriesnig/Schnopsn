package com.schnopsn.core.game.cards;

public enum CardValue {
    ASS (11),
    ZEHNER(10),
    KOENIG(4),
    DAME(3),
    BUBE(2);

    private final int value;

    private CardValue(int value){
        this.value=value;
    }

    public int getValue(){
        return value;
    }
}
