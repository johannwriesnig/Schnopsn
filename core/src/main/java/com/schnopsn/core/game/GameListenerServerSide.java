package com.schnopsn.core.game;

import com.esotericsoftware.kryonet.Server;
import com.schnopsn.core.game.GameListener;
import com.schnopsn.core.server.MainServer;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

public class GameListenerServerSide implements GameListener {
    private Server server;
    private ServerGameData gameData;
    public GameListenerServerSide(MainServer server, ServerGameData gameData){
        this.server = server.getServer();
        this.gameData = gameData;
    }
    @Override
    public void inform(GameUpdate gameUpdate, GameState previousState) {
        sendToPlayers(gameUpdate);
    }

    public void sendToPlayers(GameUpdate gameUpdate){
        for(Player player: gameData.getGame().getPlayers()){
            server.sendToTCP(player.getId(),gameUpdate);
        }
    }
}
