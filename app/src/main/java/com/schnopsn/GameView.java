package com.schnopsn;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.server.client.GameClient;

public class GameView extends AppCompatActivity {
    private Button playCard;
    private Button changeCard;
    private Button respond;
    private Game game;
    private Player me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        playCard = findViewById(R.id.randomCard);
        changeCard = findViewById(R.id.changeCard);
        respond = findViewById(R.id.respond);

        game = GameClient.getInstance().getGame();

        for(Player player: GameClient.getInstance().getGame().getPlayers()){
            if(player.getId() == GameClient.getInstance().getClient().getID())me = player;
        }

        playCard.setOnClickListener(v->playCard());
        changeCard.setOnClickListener(v->changeCard());
        respond.setOnClickListener(v->respond());
        printInfo();
    }

    public void playCard(){
        game.makeTurn(me, new NormalTurn(me.getHandDeck().getDeck()[0]));
        printInfo();
    }

    public void respond(){
        game.respondOnTurn(me,me.getHandDeck().getDeck()[0]);

    }

    public void changeCard(){
        game.changeCard(me,me.getHandDeck().getDeck()[0]);
    }

    public void printInfo(){
        Log.info("Trumpf: " + game.getTrumpf().getCardColor()+ " / "+ game.getTrumpf().getCardValue());
        Log.info("This is me: " + GameClient.getInstance().getClient().getID());
        Log.info("CurrentPlayer is: " + game.getCurrentPlayer().getId());
        Log.info("MyDeck: ");
        for(Card card: me.getHandDeck().getDeck()){
            Log.info("Farbe: "+ card.getCardColor()+" / Wert: "+ card.getCardValue());
        }
    }
}
