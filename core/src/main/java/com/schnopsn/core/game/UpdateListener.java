package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

import java.util.ArrayList;

abstract public class UpdateListener {
    abstract public void updated(GameUpdate gameUpdate, ArrayList<HandDeck> oldDecks);
}
