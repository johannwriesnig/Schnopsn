package com.schnopsn.core.server.utils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.ServerGameData;
import com.schnopsn.core.server.MainServer;
import com.schnopsn.core.server.dto.clienttoserver.ClientJoined;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;
import com.schnopsn.core.server.dto.clienttoserver.InitGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class ServerListener extends Listener {
    private HashMap<Connection, String> connectionUserNameMapping;
    private ArrayList<Connection> searchingPlayers;
    private MainServer mainServer;
    private ArrayList<ServerGameData> gameData;

    public ServerListener(MainServer mainServer){
        this.mainServer = mainServer;
        searchingPlayers = new ArrayList<>();
        connectionUserNameMapping = new HashMap<>();
        gameData = new ArrayList<>();
    }


    @Override
    public void connected(Connection connection) {
        Log.info("Client connected: "+ connection.getRemoteAddressTCP());
    }

    @Override
    public void disconnected(Connection connection) {
        searchingPlayers.remove(connection);
        connectionUserNameMapping.remove(connection);
        Log.info("Client disconnected: "+ connection.getRemoteAddressTCP());
    }

    @Override
    public void received(Connection connection, Object object) {
        Log.info("Object Received: "+ object.getClass());
        if(object instanceof ClientJoined){
            connectionUserNameMapping.put(connection, ((ClientJoined) object).getUserName());
        } else if(object instanceof FindGame){
            searchingPlayers.add(connection);
            checkForPairing();
        }
    }

    public synchronized void checkForPairing(){
        Connection[] pair = new Connection[2];
        int counter = 0;
        for (Iterator<Connection> iterator = searchingPlayers.iterator(); iterator.hasNext();) {
            pair[counter++] = iterator.next();
            if(counter==2){
                counter=0;
                searchingPlayers.remove(pair[0]);
                searchingPlayers.remove(pair[1]);
                notifyPair(pair);
            }
        }
    }

    public void notifyPair(Connection[] pair){
        ArrayList<Player> players = new ArrayList<>();

        Player player1=new Player();
        player1.setId(pair[0].getID());
        player1.setName(connectionUserNameMapping.get(pair[0]));
        players.add(player1);

        Player player2=new Player();
        player2.setId(pair[1].getID());
        player2.setName(connectionUserNameMapping.get(pair[1]));
        players.add(player2);

        Game game = new Game(players);
        ServerGameData gameData = new ServerGameData(game, new HashSet<Connection>(Arrays.asList(pair)));
        for(Connection con: pair){
            mainServer.getServer().sendToTCP(con.getID(),new InitGame(game));
        }
    }

}
