package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.turns.Turn;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private ArrayList<Player> players;
    private Player currentPlayer;
    private GameState gameState;

    public Game(ArrayList<Player> players){
        this.players = players;
        changeState(GameState.NEW_ROUND_BEGINS);
    }

    private void changeState(GameState gameState){

    }

    public void makeTurn(Player player, Turn turn){

    }
    public void responseOnTurn(Player player, Card card){

    }

    public void changeCard(Player player, Card card){}

    public void initializeRound(){}


}
