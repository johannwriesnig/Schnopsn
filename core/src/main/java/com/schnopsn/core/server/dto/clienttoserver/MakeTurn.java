package com.schnopsn.core.server.dto.clienttoserver;

import com.schnopsn.core.game.turns.Turn;
import com.schnopsn.core.server.dto.BaseMessage;

public class MakeTurn extends BaseMessage {
    private Turn turn;

    public MakeTurn(){}

    public MakeTurn(Turn turn){
        this.turn = turn;
    }

    public Turn getTurn() {
        return turn;
    }
}
