package com.schnopsn.core.game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private ArrayList<Player> players;
    private Player currentPlayer;
    private GameState gameState;

    public Game(ArrayList<Player> players){
        this.players = players;
    }
}
