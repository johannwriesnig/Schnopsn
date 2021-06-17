package com.schnopsn.core.server.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.IntArray;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardColor;
import com.schnopsn.core.game.cards.CardPair;
import com.schnopsn.core.game.cards.CardValue;
import com.schnopsn.core.game.cards.CollectedDeck;
import com.schnopsn.core.game.cards.DrawDeck;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.game.turns.AnsagenTurn;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.game.turns.Turn;
import com.schnopsn.core.server.dto.clienttoserver.ChangeCard;
import com.schnopsn.core.server.dto.clienttoserver.ClientJoined;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;
import com.schnopsn.core.server.dto.clienttoserver.MakeTurn;
import com.schnopsn.core.server.dto.clienttoserver.Respond;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;
import com.schnopsn.core.server.dto.servertoclient.InitGame;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
        kryo.register(ChangeCard.class);
        kryo.register(MakeTurn.class);
        kryo.register(Respond.class);
        kryo.register(GameUpdate.class);
        kryo.register(Turn.class);
        kryo.register(NormalTurn.class);
        kryo.register(AnsagenTurn.class);
        kryo.register(CardPair.class);


        kryo.register(Array.class);
        kryo.register(IntArray.class);
        kryo.register(ArrayList.class);
        kryo.register(HashMap.class);
        kryo.register(String.class);
        kryo.register(int[].class);
        kryo.register(Card[].class);
        kryo.register(Player[].class);
        kryo.register(Object[].class);
    }
}
