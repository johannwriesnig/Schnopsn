package com.schnopsn.core.server.dto.servertoclient;

import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.DrawDeck;
import com.schnopsn.core.game.turns.Turn;

public class GameUpdate {
    private Player[] players;
    private Player currentPlayer;
    private GameState gameState;
    private DrawDeck drawDeck;
    private Card playedCard;
    private Card trumpf;
    private Turn turn;

    public GameUpdate(){

    }

    public GameUpdate(Player[] players, Player currentPlayer, GameState gameState, DrawDeck drawDeck, Card playedCard, Card trumpf, Turn turn){
        this.players = players;
        this.currentPlayer = currentPlayer;
        this.gameState = gameState;
        this.drawDeck = drawDeck;
        this.playedCard = playedCard;
        this.trumpf = trumpf;
        this.turn = turn;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getGameState() {
        return gameState;
    }

    public DrawDeck getDrawDeck() {
        return drawDeck;
    }

    public Card getPlayedCard() {
        return playedCard;
    }

    public Card getTrumpf() {
        return trumpf;
    }

    public Turn getTurn() {
        return turn;
    }
}
