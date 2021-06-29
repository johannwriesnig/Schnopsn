package com.schnopsn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.GameState;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.UpdateListener;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.game.turns.NormalTurn;
import com.schnopsn.core.server.client.GameClient;
import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

public class GameView extends AppCompatActivity {
    private Button playCard;
    private Button changeCard;
    private Button respond;
    private Game game;
    private Player me;

    private ImageView myCard1;
    private ImageView myCard2;
    private ImageView myCard3;
    private ImageView myCard4;
    private ImageView myCard5;

    private ImageView enemyCard1;
    private ImageView enemyCard2;
    private ImageView enemyCard3;
    private ImageView enemyCard4;
    private ImageView enemyCard5;

    private ImageView myPlayedCard;
    private ImageView EnemiesPlayedCard;

    private ImageView trumpfCard;

    private TextView myStandingsView;
    private TextView enemyStandingsView;

    private int indexPlayedCard;
    private boolean haveToWaitForRespond;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        playCard = findViewById(R.id.randomCard);
        changeCard = findViewById(R.id.changeCard);
        respond = findViewById(R.id.respond);

        myCard1 = findViewById(R.id.cardImage1);
        myCard2 = findViewById(R.id.cardImage2);
        myCard3 = findViewById(R.id.cardImage3);
        myCard4 = findViewById(R.id.cardImage4);
        myCard5 = findViewById(R.id.cardImage5);

        enemyCard1 = findViewById(R.id.enemyCardImage1);
        enemyCard2 = findViewById(R.id.enemyCardImage2);
        enemyCard3 = findViewById(R.id.enemyCardImage3);
        enemyCard4 = findViewById(R.id.enemyCardImage4);
        enemyCard5 = findViewById(R.id.enemyCardImage5);

        myPlayedCard = findViewById((R.id.myPlayedCard));
        EnemiesPlayedCard = findViewById(R.id.enemiesPlayedCard);

        trumpfCard = findViewById(R.id.trumpf);

        myStandingsView = findViewById(R.id.myNameView);
        enemyStandingsView = findViewById(R.id.enemyNameView);

        game = GameClient.getInstance().getGame();

        for(Player player: GameClient.getInstance().getGame().getPlayers()){
            if(player.getId() == GameClient.getInstance().getClient().getID())me = player;
        }

        initRound();
        setUpCardsDependingOnWhoTurn();
        initStandings();
        setListenerForMyCards();




        playCard.setOnClickListener(v->playCard());
        changeCard.setOnClickListener(v->changeCard());
        respond.setOnClickListener(v->respond());

        game.setUpdateListener(new UpdateListenerImpl());
        printInfo();
    }

    public void playCard(){
        game.makeTurn(me, new NormalTurn(me.getHandDeck().getDeck()[0]));
        printInfo();
    }

    public void respond(){
        game.respondOnTurn(me,me.getHandDeck().getDeck()[0]);
        printInfo();
    }

    public void changeCard(){
        game.changeCard(me,me.getHandDeck().getDeck()[0]);
        printInfo();
    }

    public void initRound(){
        HandDeck myDeck = me.getHandDeck();

        setCardImage(myCard1, myDeck.getDeck()[0]);
        setCardImage(myCard2, myDeck.getDeck()[1]);
        setCardImage(myCard3, myDeck.getDeck()[2]);
        setCardImage(myCard4, myDeck.getDeck()[3]);
        setCardImage(myCard5, myDeck.getDeck()[4]);

        setCardImage(trumpfCard, game.getTrumpf());
    }


    public void setCardImage(ImageView view, Card card){
        switch(card.getCardColor()){
            case PIK: switch (card.getCardValue()){
                case ASS:view.setBackgroundResource(R.drawable.pikass);
                break;
                case ZEHNER:view.setBackgroundResource(R.drawable.pikzehn);
                    break;
                case KOENIG:view.setBackgroundResource(R.drawable.pikkoenig);
                    break;
                case DAME:view.setBackgroundResource(R.drawable.pikdame);
                    break;
                case BUBE:view.setBackgroundResource(R.drawable.pikbub);
                    break;
            }
            break;
            case KARO: switch (card.getCardValue()){
                case ASS:view.setBackgroundResource(R.drawable.karoass);
                    break;
                case ZEHNER:view.setBackgroundResource(R.drawable.karozehn);
                    break;
                case KOENIG:view.setBackgroundResource(R.drawable.karokoenig);
                    break;
                case DAME:view.setBackgroundResource(R.drawable.karodame);
                    break;
                case BUBE:view.setBackgroundResource(R.drawable.karobub);
                    break;
            }
            break;
            case KREUZ: switch (card.getCardValue()){
                case ASS:view.setBackgroundResource(R.drawable.kreuzass);
                    break;
                case ZEHNER:view.setBackgroundResource(R.drawable.kreuzzehn);
                    break;
                case KOENIG:view.setBackgroundResource(R.drawable.kreuzkoenig);
                    break;
                case DAME:view.setBackgroundResource(R.drawable.kreuzdame);
                    break;
                case BUBE:view.setBackgroundResource(R.drawable.kreuzbub);
                    break;
            }
            break;
            case HERZ: switch (card.getCardValue()){
                case ASS:view.setBackgroundResource(R.drawable.herzass);
                    break;
                case ZEHNER:view.setBackgroundResource(R.drawable.herzzehn);
                    break;
                case KOENIG:view.setBackgroundResource(R.drawable.herzkoenig);
                    break;
                case DAME:view.setBackgroundResource(R.drawable.herzdame);
                    break;
                case BUBE:view.setBackgroundResource(R.drawable.herzbub);
                    break;
            }
            break;
        }
    }

    public void initStandings(){
        String myStandings;
        myStandings = me.getName()+": "+ me.getBummerl();
        myStandingsView.setText(myStandings);

        String enemyStandings;
        Player enemy = game.getOtherPlayer(me);
        enemyStandings = enemy.getName()+": " + enemy.getBummerl();
        enemyStandingsView.setText(enemyStandings);
    }

    public void printInfo(){
        Log.info("Trumpf: " + game.getTrumpf().getCardColor()+ " / "+ game.getTrumpf().getCardValue());
        Log.info("This is me: " + GameClient.getInstance().getClient().getID());
        Log.info("CurrentPlayer is: " + game.getCurrentPlayer().getId());
        Log.info("MyDeck: ");
        for(Card card: me.getHandDeck().getDeck()){
            if(card!=null)
            Log.info("Farbe: "+ card.getCardColor()+" / Wert: "+ card.getCardValue());
        }
    }
    public void initTextView(){
        String info;
        if(GameClient.getInstance().getGame().getCurrentPlayer().getId()==me.getId())info = "It's my turn";
        else info = "It's the enemies turn";

    }

    public void setListenerForMyCards(){
        myCard1.setOnClickListener((View view)->translate(myCard1, myPlayedCard, 0));
        myCard2.setOnClickListener((View view)->translate(myCard2, myPlayedCard, 1));
        myCard3.setOnClickListener((View view)->translate(myCard3, myPlayedCard,2));
        myCard4.setOnClickListener((View view)->translate(myCard4, myPlayedCard,3));
        myCard5.setOnClickListener((View view)->translate(myCard5, myPlayedCard,4));


    }

    private void translate(ImageView viewToMove, ImageView target, int index){
        indexPlayedCard=index;
        int duration = 900;
        viewToMove.animate()
                .x(target.getX())
                .y(target.getY())
                .setDuration(duration)
                .start();
        viewToMove.postDelayed(new Runnable() {
            @Override
            public void run() {
                target.setBackground(viewToMove.getBackground());
                viewToMove.setVisibility(View.INVISIBLE);
                makeTurn();
            }
        }, duration);

    }

    public void makeTurn(){
        haveToWaitForRespond=false;
        if(game.getGameState()== GameState.AWAITING_TURN){
            haveToWaitForRespond=true;
            game.makeTurn(me, new NormalTurn(me.getHandDeck().getDeck()[indexPlayedCard]));
        }
        else if(game.getGameState() == GameState.AWAITING_RESPONSE) {
            game.respondOnTurn(me, me.getHandDeck().getDeck()[indexPlayedCard]);
        }
    }

    public void setUpCardsDependingOnWhoTurn(){
        if(me.getId()!=game.getCurrentPlayer().getId()){
            myCard1.setAlpha(0.5f);
            myCard1.setEnabled(false);

            myCard2.setAlpha(0.5f);
            myCard2.setEnabled(false);

            myCard3.setAlpha(0.5f);
            myCard3.setEnabled(false);

            myCard4.setAlpha(0.5f);
            myCard4.setEnabled(false);

            myCard5.setAlpha(0.5f);
            myCard5.setEnabled(false);
        } else{
            myCard1.setAlpha(1f);
            myCard1.setEnabled(true);

            myCard2.setAlpha(1f);
            myCard2.setEnabled(true);

            myCard3.setAlpha(1f);
            myCard3.setEnabled(true);

            myCard4.setAlpha(1f);
            myCard4.setEnabled(true);

            myCard5.setAlpha(1f);
            myCard5.setEnabled(true);
        }
    }

    public void playEnemiesCard(Card card){

    }


    class UpdateListenerImpl extends UpdateListener{

        @Override
        public void updated(GameUpdate gameUpdate) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    initTextView();
                    setUpCardsDependingOnWhoTurn();

                }
            });
        }
    }
}
