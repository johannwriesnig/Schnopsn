package com.schnopsn.core.server.utils;

import com.esotericsoftware.kryo.Kryo;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardColor;
import com.schnopsn.core.game.cards.CardValue;
import com.schnopsn.core.game.cards.CollectedDeck;
import com.schnopsn.core.game.cards.DrawDeck;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.server.dto.clienttoserver.ClientJoined;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;
import com.schnopsn.core.server.dto.clienttoserver.InitGame;

public class RegisterHelper {
    private RegisterHelper(){}

    public static void registerClasses(Kryo kryo){
        kryo.register(ClientJoined.class);
        kryo.register(FindGame.class);
        kryo.register(InitGame.class);
        kryo.register(Game.class);
        kryo.register(Player.class);
        kryo.register(GameState.class);
        kryo.register(Card.class);
        kryo.register(CardColor.class);
        kryo.register(CardValue.class);
        kryo.register(HandDeck.class);
        kryo.register(DrawDeck.class);
        kryo.register(CollectedDeck.class);
    }
}
