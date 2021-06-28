package com.schnopsn.core.server.utils;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameListenerServerSide;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.ServerGameData;
import com.schnopsn.core.server.MainServer;
import com.schnopsn.core.server.dto.clienttoserver.ChangeCard;
import com.schnopsn.core.server.dto.clienttoserver.ClientJoined;
import com.schnopsn.core.server.dto.clienttoserver.FindGame;
import com.schnopsn.core.server.dto.servertoclient.InitGame;
import com.schnopsn.core.server.dto.clienttoserver.MakeTurn;
import com.schnopsn.core.server.dto.clienttoserver.Respond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ServerListener extends Listener {
    private HashMap<Connection, String> connectionUserNameMapping;
    private ArrayList<Connection> searchingPlayers;
    private MainServer mainServer;
    private ArrayList<ServerGameData> gameDatas;

    public ServerListener(MainServer mainServer){
        this.mainServer = mainServer;
        searchingPlayers = new ArrayList<>();
        connectionUserNameMapping = new HashMap<>();
        gameDatas = new ArrayList<>();
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
        if(!(object instanceof com.esotericsoftware.kryonet.FrameworkMessage)) Log.info("Object Received: "+ object.getClass());
        if(object instanceof ClientJoined){
            connectionUserNameMapping.put(connection, ((ClientJoined) object).getUserName());
        } else if(object instanceof FindGame){
            searchingPlayers.add(connection);
            checkForPairing();
        } else if(object instanceof MakeTurn || object instanceof Respond || object instanceof ChangeCard){
            ServerGameData gameData = null;
            Player currentPlayer = null;
            for (ServerGameData data : gameDatas) {
                if (data.getUsers().contains(connection)) {
                    gameData = data;
                    for (Player player : gameData.getGame().getPlayers()) {
                        if (player.getId() == connection.getID()) {
                            currentPlayer = player;
                        }
                    }
                }
            }
            if(gameData==null||currentPlayer==null)return;
            if(object instanceof MakeTurn){
                gameData.getGame().makeTurn(currentPlayer,((MakeTurn) object).getTurn());
            } else if(object instanceof Respond){
                gameData.getGame().respondOnTurn(currentPlayer, ((Respond) object).getResponseCard());
            } else {
                gameData.getGame().changeCard(currentPlayer, ((ChangeCard) object).getCardToChange());
            }

        }
    }

    public synchronized void checkForPairing(){
        Connection[] pair = new Connection[2];
        List<Connection> toRemove = new ArrayList<>();
        int counter = 0;
        for (Iterator<Connection> iterator = searchingPlayers.iterator(); iterator.hasNext();) {
            pair[counter++] = iterator.next();
            if(counter==2){
                counter=0;
                toRemove.add(pair[0]);
                toRemove.add(pair[1]);
                notifyPair(pair);
            }
        }
        searchingPlayers.removeAll(toRemove);
    }

    public synchronized void notifyPair(Connection[] pair){
        Player[] players = new Player[2];

        Player player1=new Player();
        player1.setId(pair[0].getID());
        player1.setName(connectionUserNameMapping.get(pair[0]));
        players[0]=player1;

        Player player2=new Player();
        player2.setId(pair[1].getID());
        player2.setName(connectionUserNameMapping.get(pair[1]));
        players[1]=player2;

        Game game = new Game(players);
        ServerGameData gameData = new ServerGameData(game, new HashSet<Connection>(Arrays.asList(pair)));
        for(Connection con: pair){
            mainServer.getServer().sendToTCP(con.getID(),new InitGame(game));
        }
        gameDatas.add(gameData);
        game.setGameListener(new GameListenerServerSide(mainServer,gameData));
        game.startGame();
    }

}
