package com.schnopsn.core.server.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.server.dto.BaseMessage;
import com.schnopsn.core.server.dto.clienttoserver.ClientJoined;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;
import com.schnopsn.core.server.utils.NetworkConstants;
import com.schnopsn.core.server.utils.RegisterHelper;

import java.io.IOException;

import jdk.vm.ci.code.Register;

public class GameClient {
    private Client client;
    private static GameClient instance;
    private Game game;
    private String userName;
    private boolean inGame=false;
    private GameInitListener gameInitListener;

    private GameClient() {
    }

    public static GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }
        return instance;
    }

    public void init() {

        try {
            client = new Client();
            client.start();
            RegisterHelper.registerClasses(client.getKryo());
            client.addListener(new ClientListener(this));
            client.connect(5000, NetworkConstants.MAIN_SERVER_IP, NetworkConstants.TCP_PORT);
            sendUserName();

        } catch (IOException e) {
            Log.info("Connection failed.");
        }


    }

    public Client getClient() {
        return client;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        gameInitListener.changeView();
    }

    public void setGameInitListener(GameInitListener gameInitListener) {
        this.gameInitListener = gameInitListener;
    }

    public void sendMessage(final BaseMessage message){
        Thread thread = new Thread(){
            @Override
            public void run() {
                client.sendTCP(message);
            }
        };

        thread.start();
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void sendUserName(){
        client.sendTCP(new ClientJoined(userName));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
