package com.schnopsn;

import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.UpdateListener;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

import java.util.ArrayList;

public class UpdateListenerImpl extends UpdateListener {
    Controller controller;
    public UpdateListenerImpl(Controller controller){
        this.controller = controller;
    }


    @Override
    public void updated(GameUpdate gameUpdate, ArrayList<HandDeck> oldDecks, GameState previousState) {
        controller.setGameUpdate(gameUpdate);
        controller.setOldDecks(oldDecks);
        controller.setPreviousState(previousState);
        this.controller.checkExecution();
    }
}
