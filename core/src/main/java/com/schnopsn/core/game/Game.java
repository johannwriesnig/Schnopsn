package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardColor;
import com.schnopsn.core.game.cards.CardPair;
import com.schnopsn.core.game.cards.CardValue;
import com.schnopsn.core.game.turns.AnsagenTurn;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.game.turns.Turn;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player[] players;
    private Player currentPlayer;
    private GameState gameState;
    private Card playedCard;
    private Card currentTrumpfCard;
    private GameListener gameListener;

    public Game(Player[] players){
        this.players = players;
        changeState(GameState.NEW_ROUND_BEGINS);
    }

    private void changeState(GameState gameState){
        if(gameState==GameState.NEW_ROUND_BEGINS)initRound();

    }

    public void makeTurn(Player player, Turn turn){
        if(gameState!=GameState.AWAITING_TURN||currentPlayer!=player)return;
        if(turn instanceof NormalTurn) {
            playedCard = ((NormalTurn) turn).getPlayedCard();
            changeState(GameState.AWAITING_RESPONSE);
        } else if(turn instanceof AnsagenTurn){
            if(!((AnsagenTurn) turn).isCallable())return;
            if(((AnsagenTurn) turn).cardToPlay().getCardColor()==currentTrumpfCard.getCardColor())currentPlayer.getCollectedDeck().addCallPoints(40);
            else currentPlayer.getCollectedDeck().addCallPoints(20);
            if(currentPlayer.getCollectedDeck().getPoints()>=66)changeState(GameState.NEW_ROUND_BEGINS);
            else changeState(GameState.AWAITING_TURN);
        }
    }
    public void respondOnTurn(Player player, Card responseCard){
        if(gameState!=GameState.AWAITING_RESPONSE || currentPlayer==player||!player.getDeck().contains(responseCard))return;
        player.getDeck().remove(responseCard);
        boolean playedCardIsHigher = checkIfPlayedCardIsHigher(responseCard);
        CardPair cardPair = new CardPair(playedCard, responseCard);



    }

    public boolean checkIfPlayedCardIsHigher(Card responseCard){
        return false;
    }

    public void changeCard(Player player, Card card){
        if(gameState!=GameState.AWAITING_TURN || currentPlayer!=player || currentTrumpfCard.getCardColor()!=card.getCardColor()|| card.getCardValue()!= CardValue.BUBE||!player.getDeck().contains(card))return;
        player.getDeck().remove(card);
        player.getDeck().add(currentTrumpfCard);
        currentTrumpfCard = card;
        changeState(GameState.AWAITING_TURN);
    }


    public void initRound(){
        //distribute Cards to Players
    }


}
