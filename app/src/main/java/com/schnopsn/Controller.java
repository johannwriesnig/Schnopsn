package com.schnopsn;

import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.server.client.GameClient;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

import java.util.ArrayList;

public class Controller {
    private GameView gameView;
    private int myId;
    private GameUpdate gameUpdate;
    private GameState previousState;
    private ArrayList<HandDeck> oldDecks;
    private Game game;
    private Player meAsPlayer;
    private Player enemyAsPlayer;
    private UpdateListenerImpl updateListener;
    private Card cardToDraw1;
    private Card cardToDraw2;
    private boolean isLastDraw=false;


    public Controller(GameView gameView) {
        updateListener = new UpdateListenerImpl(this);
        this.gameView = gameView;
        game = GameClient.getInstance().getGame();
        game.setUpdateListener(updateListener);
        initMyId();
        initPlayers();
        blockCards();
    }

    public void initMyId() {
        for (Player player : game.getPlayers()) {
            if (player.getId() == GameClient.getInstance().getClient().getID()) {
                myId = player.getId();
            }
        }
    }

    public void updateGUI() {
        Log.info("Controller Info............");
        Log.info("PreviousState: " + previousState + " / CurrentState: " + gameUpdate.getGameState());
        Log.info("IsMyTurn: "+ isMyTurn());
        Log.info("IsMyTurnOnGameUpdate: "+ isMyTurnBasedOnUpdate());


        if (previousState == GameState.AWAITING_TURN && gameUpdate.getGameState() == GameState.AWAITING_RESPONSE) {
            int drawDeckSize = game.getDrawDeck().getDrawDeck().size();
            if(drawDeckSize==2)isLastDraw=true;
            if(drawDeckSize>=2){
                cardToDraw1 = game.getDrawDeck().getDrawDeck().get(0);
                cardToDraw2 = game.getDrawDeck().getDrawDeck().get(1);
                gameView.initDrawCards(cardToDraw1,cardToDraw2);
            }
            computeTurn();
        } else if(previousState == GameState.AWAITING_RESPONSE && gameUpdate.getGameState() == GameState.DRAWING){
            computeTurn();
        } else if(previousState==GameState.DRAWING && gameUpdate.getGameState()==GameState.AWAITING_TURN){
            collectCards();
            drawCards();
        }
        blockCards();

    }

    public void collectCards(){
        if(isMyTurnBasedOnUpdate())gameView.collectCardsForMe();
        else gameView.collectCardsForEnemy();
    }

    public void drawCards(){
        if(cardToDraw1==null||cardToDraw2==null)return;
        if(gameUpdate.getCurrentPlayer().getId()==myId){
            gameView.drawCardForMeFirst();
        } else{
            gameView.drawCardForEnemyFirst();

        }
    }

    public void blockCards() {
        if (isMyTurnBasedOnUpdate()) gameView.unblockMyCards();
        else gameView.blockMyCards();
    }

    public void computeTurn() {
        int index;
        if (isMyTurn()) {
            index = meAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playMyCard(index);
        } else {
            index = enemyAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playEnemiesCard(index);
        }
    }

    public void computeResponse(){
        int index;
        if (isMyTurn()) {
            index = meAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playMyCard(index);
        } else {
            index = enemyAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playEnemiesCard(index);
        }
    }

    public void initPlayers() {
        for (Player player : game.getPlayers()) {
            if (player.getId() == myId) meAsPlayer = player;
            else enemyAsPlayer = player;
        }
    }

    public void playCard(int indexOfPlayedCard) {
        if (game.getGameState() == GameState.AWAITING_TURN && isMyTurn()) {
            game.makeTurn(meAsPlayer, new NormalTurn(meAsPlayer.getHandDeck().getDeck()[indexOfPlayedCard]));
        } else if (game.getGameState() == GameState.AWAITING_RESPONSE && isMyTurn()) {
            game.respondOnTurn(meAsPlayer, meAsPlayer.getHandDeck().getDeck()[indexOfPlayedCard]);
        }
    }

    public boolean isMyTurn() {
       return game.getCurrentPlayer().getId() == myId;

    }

    public boolean isMyTurnBasedOnUpdate(){
        if(gameUpdate!=null)return gameUpdate.getCurrentPlayer().getId()==myId;
        else return isMyTurn();
    }


    public void setGameUpdate(GameUpdate gameUpdate) {
        this.gameUpdate = gameUpdate;
    }

    public void setPreviousState(GameState previousState) {
        this.previousState = previousState;
    }

    public void setOldDecks(ArrayList<HandDeck> oldDecks) {
        this.oldDecks = oldDecks;
    }

    public UpdateListenerImpl getUpdateListener() {
        return updateListener;
    }
}
