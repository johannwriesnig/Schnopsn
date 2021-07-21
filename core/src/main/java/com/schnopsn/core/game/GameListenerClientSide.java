package com.schnopsn.core.game;

import com.esotericsoftware.kryonet.Client;
import com.schnopsn.core.server.client.GameClient;
import com.schnopsn.core.server.dto.clienttoserver.ChangeCard;
import com.schnopsn.core.server.dto.clienttoserver.MakeTurn;
import com.schnopsn.core.server.dto.clienttoserver.Respond;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

public class GameListenerClientSide implements GameListener {
    private GameClient client;

    public GameListenerClientSide(GameClient gameClient){
        this.client = gameClient;
    }

    @Override
    public void inform(GameUpdate gameUpdate, GameState previousState) {
        if(previousState==GameState.AWAITING_TURN&&gameUpdate.getGameState()==GameState.AWAITING_RESPONSE)
            client.sendMessage(new MakeTurn(gameUpdate.getTurn()));
        else if(previousState == GameState.AWAITING_TURN && gameUpdate.getGameState() == GameState.AWAITING_TURN)
            client.sendMessage(new ChangeCard(gameUpdate.getPlayedCard()));
        else if(previousState == GameState.AWAITING_RESPONSE && gameUpdate.getGameState() == GameState.AWAITING_TURN||
                previousState==GameState.AWAITING_RESPONSE && gameUpdate.getGameState() ==GameState.NEW_ROUND_BEGINS||
                previousState==GameState.AWAITING_RESPONSE && gameUpdate.getGameState() == GameState.GAME_OVER||
        previousState==GameState.AWAITING_RESPONSE&&gameUpdate.getGameState()==GameState.DRAWING)
            client.sendMessage(new Respond(gameUpdate.getPlayedCard()));

    }
}
