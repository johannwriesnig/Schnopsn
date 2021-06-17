package com.schnopsn.core.server.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameListenerClientSide;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;
import com.schnopsn.core.server.dto.servertoclient.InitGame;

public class ClientListener extends Listener {
    private GameClient gameClient;

    public ClientListener(GameClient gameClient){
        this.gameClient = gameClient;
    }

    @Override
    public void received(Connection connection, Object object) {
        Log.info("Received Object: " + object.getClass());
        if(object instanceof InitGame){
            Game game = ((InitGame) object).getGame();
            gameClient.setGame(game);
            gameClient.getGame().setGameListener(new GameListenerClientSide(gameClient));

        } else if(object instanceof GameUpdate){
            gameClient.getGame().updateGame((GameUpdate) object);
        }
    }

    @Override
    public void connected(Connection connection) {
        Log.info("Client connected: " + connection.getRemoteAddressTCP());
    }

    @Override
    public void disconnected(Connection connection) {
        Log.info("Client disconnected: " + connection.getRemoteAddressTCP());
    }


}
