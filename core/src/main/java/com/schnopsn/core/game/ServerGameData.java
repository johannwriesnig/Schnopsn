package com.schnopsn.core.game;

import com.esotericsoftware.kryonet.Connection;

import java.util.HashSet;

public class ServerGameData {
    private HashSet<Connection> users;
    private Game game;

    public ServerGameData(Game game, HashSet<Connection> users){
        this.game = game;
        this.users = users;
    }

    public Game getGame() {
        return game;
    }
    public HashSet<Connection> getUsers() {
        return users;
    }
}
