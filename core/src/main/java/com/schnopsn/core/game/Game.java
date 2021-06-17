package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardPair;
import com.schnopsn.core.game.cards.CardValue;
import com.schnopsn.core.game.cards.CollectedDeck;
import com.schnopsn.core.game.cards.DeckBuilder;
import com.schnopsn.core.game.cards.DrawDeck;
import com.schnopsn.core.game.turns.AnsagenTurn;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.game.turns.Turn;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;
import com.esotericsoftware.minlog.Log;



public class Game {
    private Player[] players;
    private Turn turn;
    private Player currentPlayer;
    private GameState gameState;
    private DrawDeck drawDeck;
    private Card playedCard;
    private Card trumpf;
    private GameListener gameListener;
    private int roundCounter;

    public Game(){}

    public Game(Player[] players){
        if(players.length!=2)return;
        this.players = players;
        roundCounter = 0;
        currentPlayer = players[roundCounter%2];
        initRound();
    }

    private void changeState(GameState gameState){
        if(gameListener!=null){
            GameUpdate gameUpdate = new GameUpdate(players,currentPlayer,gameState,drawDeck,playedCard,trumpf, turn);
            gameListener.inform(gameUpdate, this.gameState);
        }
        this.gameState = gameState;
        if(gameState==GameState.NEW_ROUND_BEGINS && !(gameListener instanceof GameListenerClientSide))initRound();
    }

    public void makeTurn(Player player, Turn turn){
        Log.info("start making a turn");
        if(gameState!=GameState.AWAITING_TURN||currentPlayer.getId()!=player.getId())return;
        Log.info("here");
        this.turn = turn;
        if(turn instanceof NormalTurn) {
            playedCard = ((NormalTurn) turn).getPlayedCard();
            currentPlayer.getHandDeck().remove(playedCard);
            changeState(GameState.AWAITING_RESPONSE);
        } else if(turn instanceof AnsagenTurn){
            if(!((AnsagenTurn) turn).isCallable())return;
            if(((AnsagenTurn) turn).cardToPlay().getCardColor()==trumpf.getCardColor())currentPlayer.getCollectedDeck().addCallPoints(40);
            else currentPlayer.getCollectedDeck().addCallPoints(20);
            playedCard = ((AnsagenTurn) turn).cardToPlay();
            currentPlayer.getHandDeck().remove(playedCard);
            boolean roundIsOver = isRoundOver();
            if(!roundIsOver)changeState(GameState.AWAITING_TURN);
        }
        Log.info("end making a turn");
    }
    public void respondOnTurn(Player player, Card responseCard){
        Log.info("start making a response");
        if(gameState!=GameState.AWAITING_RESPONSE || currentPlayer.getId()==player.getId()||!player.getHandDeck().contains(responseCard))return;
        playedCard = responseCard;
        player.getHandDeck().remove(responseCard);
        boolean playedCardIsHigherThanResponse = playedCardIsHigherThanResponse(responseCard);
        CardPair cardPair = new CardPair(playedCard, responseCard);
        if(playedCardIsHigherThanResponse)player.getCollectedDeck().add(cardPair);
        else {
            currentPlayer.getCollectedDeck().add(cardPair);
            currentPlayer = player;
        }
        boolean roundIsOver = isRoundOver();
        if(!roundIsOver)changeState(GameState.AWAITING_TURN);
        Log.info("end making a response");

    }

    public boolean isRoundOver(){
        if(currentPlayer.getCollectedDeck().getPoints()>=66){
            int losersPoints = getOtherPlayer(currentPlayer).getCollectedDeck().getPoints();
            if(losersPoints==0)currentPlayer.setBummerl(currentPlayer.getBummerl()-3);
            else if(losersPoints<32)currentPlayer.setBummerl(currentPlayer.getBummerl()-2);
            else if(losersPoints>32)currentPlayer.setBummerl(currentPlayer.getBummerl()-1);
            boolean gameIsOver = isGameOver();
            if(!gameIsOver){
            roundCounter++;
            currentPlayer=players[roundCounter%2];
            changeState(GameState.NEW_ROUND_BEGINS);
            return true;}
        }
        return false;
    }
    public boolean isGameOver(){
        if(currentPlayer.getBummerl()==0){
            changeState(GameState.GAME_OVER);
            return true;
        }
        return false;
    }

    public void startGame(){
        changeState(GameState.AWAITING_TURN);
    }

    public boolean playedCardIsHigherThanResponse(Card responseCard){
        return false;
    }

    public void changeCard(Player player, Card card){
        Log.info("start changing a card");
        if(gameState!=GameState.AWAITING_TURN || currentPlayer.getId()!=player.getId() || trumpf.getCardColor()!=card.getCardColor()|| card.getCardValue()!= CardValue.BUBE||!player.getHandDeck().contains(card))return;
        playedCard = card;
        player.getHandDeck().remove(card);
        player.getHandDeck().add(trumpf);
        trumpf = card;
        changeState(GameState.AWAITING_TURN);
        Log.info("end changing a card");
    }




    public void initRound(){
        DeckBuilder deckBuilder = new DeckBuilder();
        deckBuilder.buildDecks();

        players[0].setDeck(deckBuilder.getHandDeck1());
        players[0].setCollectedDeck(new CollectedDeck());

        players[1].setDeck(deckBuilder.getHandDeck2());
        players[1].setCollectedDeck(new CollectedDeck());

        drawDeck = deckBuilder.getDrawDeck();
        trumpf = drawDeck.getDrawDeck().get(drawDeck.getDrawDeck().size()-1);
        changeState(GameState.AWAITING_TURN);
    }

    public Player getOtherPlayer(Player player){
        for(Player p: players){
            if(p!=player)return p;
        }
        return null;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setGameListener(GameListener gameListener) {
        this.gameListener = gameListener;
    }

    public void updateGame(GameUpdate gameUpdate){
        updatePlayers(gameUpdate.getPlayers());
        this.currentPlayer = gameUpdate.getCurrentPlayer();
        this.turn = gameUpdate.getTurn();
        this.gameState = gameUpdate.getGameState();
        this.drawDeck = gameUpdate.getDrawDeck();
        this.trumpf = gameUpdate.getTrumpf();
        this.playedCard = gameUpdate.getPlayedCard();
    }

    public void updatePlayers(Player[] players){
        for(Player player: this.players){
            for(Player newPlayer: players){
                if(player.getId() == newPlayer.getId()){
                    player.setDeck(newPlayer.getHandDeck());
                    player.setCollectedDeck(newPlayer.getCollectedDeck());
                }
            }
        }

    }

    public Turn getTurn() {
        return turn;
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

    public GameListener getGameListener() {
        return gameListener;
    }

    public int getRoundCounter() {
        return roundCounter;
    }
}
