package com.schnopsn.core.game;

import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.CardPair;
import com.schnopsn.core.game.cards.CardValue;
import com.schnopsn.core.game.cards.CollectedDeck;
import com.schnopsn.core.game.cards.DeckBuilder;
import com.schnopsn.core.game.cards.DrawDeck;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.game.turns.AnsagenTurn;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.game.turns.Turn;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;


public class Game {
    private Player[] players;
    private Turn turn;
    private Player currentPlayer;
    private GameState gameState;
    private DrawDeck drawDeck;
    private Card playedCard;
    private Card trumpf;
    private GameListener gameListener;
    private UpdateListener updateListener;
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
        if(!isClientGame()) this.gameState = gameState;

        if(gameState==GameState.NEW_ROUND_BEGINS && !(gameListener instanceof GameListenerClientSide))initRound();
        if(gameState==GameState.DRAWING&&!isClientGame()) setUpDraws();

    }

    public boolean isClientGame(){
        return gameListener instanceof GameListenerClientSide;
    }

    public void setUpDraws(){
        if(drawDeck.getDrawDeck().size()>=2)drawOneCardEach();
        currentPlayer = getOtherPlayer(currentPlayer);
        changeState(GameState.AWAITING_TURN);
    }

    public void makeTurn(Player player, Turn turn){
        if(gameState!=GameState.AWAITING_TURN||currentPlayer.getId()!=player.getId())return;
        this.turn = turn;
        if(turn instanceof NormalTurn) {
            playedCard = turn.getPlayedCard();
            Log.info("Turn Card: " + playedCard.getCardColor()+ " / "+ playedCard.getCardValue());
            currentPlayer.getHandDeck().remove(playedCard);
            currentPlayer = getOtherPlayer(currentPlayer);
            changeState(GameState.AWAITING_RESPONSE);
        } else if(turn instanceof AnsagenTurn){
            if(!((AnsagenTurn) turn).isCallable())return;
            if(turn.getPlayedCard().getCardColor()==trumpf.getCardColor())currentPlayer.getCollectedDeck().addCallPoints(40);
            else currentPlayer.getCollectedDeck().addCallPoints(20);
            playedCard = turn.getPlayedCard();
            currentPlayer.getHandDeck().remove(playedCard);
            boolean roundIsOver = isRoundOver();
            currentPlayer = getOtherPlayer(currentPlayer);
            if(!roundIsOver)changeState(GameState.AWAITING_RESPONSE);
        }
    }
    public void respondOnTurn(Player player, Card responseCard){
        if(gameState!=GameState.AWAITING_RESPONSE || currentPlayer.getId()!=player.getId()||!player.getHandDeck().contains(responseCard))return;
        playedCard = responseCard;
        Log.info("Response Card: " + responseCard.getCardColor()+ " / "+ responseCard.getCardValue());
        player.getHandDeck().remove(responseCard);
        boolean playedCardIsHigherThanResponse = playedCardIsHigherThanResponse(responseCard);
        CardPair cardPair = new CardPair(turn.getPlayedCard(), responseCard);
        if(playedCardIsHigherThanResponse){
            currentPlayer.getCollectedDeck().add(cardPair);
        }
        else {
            getOtherPlayer(currentPlayer).getCollectedDeck().add(cardPair);
        }
        boolean roundIsOver = isRoundOver();
        if(!roundIsOver){
            changeState(GameState.DRAWING);
        }

    }

    public void drawOneCardEach(){
        currentPlayer.getHandDeck().add(drawDeck.drawCard());
        getOtherPlayer(currentPlayer).getHandDeck().add(drawDeck.drawCard());
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
        boolean isHigher;
        if(turn.getPlayedCard().getCardColor().equals(trumpf.getCardColor())||responseCard.getCardColor().equals(trumpf.getCardColor())){
            if(turn.getPlayedCard().getCardColor().equals(trumpf.getCardColor())&&!responseCard.getCardColor().equals(trumpf.getCardColor()))isHigher=true;
            else if(!turn.getPlayedCard().getCardColor().equals(trumpf.getCardColor())&&responseCard.getCardColor().equals(trumpf.getCardColor()))isHigher=false;
            else {
                if(turn.getPlayedCard().getCardValue().getValue()>responseCard.getCardValue().getValue())isHigher=true;
                else isHigher = false;
            }
        } else {
            if(!turn.getPlayedCard().getCardColor().equals(responseCard.getCardColor()))isHigher=true;
            else{
                if(turn.getPlayedCard().getCardValue().getValue()>responseCard.getCardValue().getValue())isHigher=true;
                else isHigher = false;
            }
        }
        Log.info("Is higher: "+isHigher);
        return isHigher;
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
        Log.info("Trumpf: " + trumpf.getCardColor() + " / " + trumpf.getCardValue());
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
        if(updateListener!=null){
            ArrayList<HandDeck> oldDecks= new ArrayList<>();
            oldDecks.add(getCopyOfDeck(currentPlayer));
            oldDecks.add(getCopyOfDeck(getOtherPlayer(currentPlayer)));
            updateListener.updated(gameUpdate, oldDecks,gameState);
        }
        this.currentPlayer = gameUpdate.getCurrentPlayer();
        this.playedCard = gameUpdate.getPlayedCard();
        updatePlayers(gameUpdate.getPlayers());
        this.turn = gameUpdate.getTurn();
        this.drawDeck = gameUpdate.getDrawDeck();
        this.trumpf = gameUpdate.getTrumpf();
        this.gameState = gameUpdate.getGameState();
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

    public HandDeck getCopyOfDeck(Player player){
        HandDeck deckToReturn = new HandDeck(new Card[5]);

        for(Card card: player.getHandDeck().getDeck()){
            deckToReturn.add(card);
        }

        return deckToReturn;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
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
