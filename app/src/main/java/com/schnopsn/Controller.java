package com.schnopsn;

import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.UpdateListener;
import com.schnopsn.core.game.cards.HandDeck;
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


    public Controller(GameView gameView){
        updateListener = new UpdateListenerImpl(this);
        this.gameView = gameView;
        game = GameClient.getInstance().getGame();
        initMyId();
        initPlayers();
    }

    public void initMyId(){
        for(Player player: game.getPlayers()){
            if(player.getId()==GameClient.getInstance().getClient().getID()){
                myId = player.getId();
            }
        }
    }

    public void checkExecution(){
        if(previousState==GameState.AWAITING_TURN&&gameUpdate.getGameState()==GameState.AWAITING_RESPONSE){
            computeTurn();
        }
    }

    public void computeTurn(){
        int index;
        if(isMyTurn()) {
            index = meAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playMyCard(index);
        }
        else {
            index = enemyAsPlayer.getHandDeck().getCardIndex(gameUpdate.getPlayedCard());
            gameView.playEnemiesCard(index);
        }


    }

    public void initPlayers(){
        for(Player player: game.getPlayers()){
            if(player.getId()==myId)meAsPlayer=player;
            else enemyAsPlayer=player;
        }
    }

    public boolean isMyTurn(){
        return gameUpdate.getCurrentPlayer().getId() == myId;
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

    public UpdateListenerImpl getUpdateListener(){
        return updateListener;
    }
}
