package com.schnopsn;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
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

import java.util.ArrayList;

public class GameViewImpl extends AppCompatActivity implements GameView {
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

    private ImageView enemyCardBack1;
    private ImageView enemyCardBack2;
    private ImageView enemyCardBack3;
    private ImageView enemyCardBack4;
    private ImageView enemyCardBack5;

    private ImageView enemyCardFront1;
    private ImageView enemyCardFront2;
    private ImageView enemyCardFront3;
    private ImageView enemyCardFront4;
    private ImageView enemyCardFront5;

    private ImageView myPlayedCard;
    private ImageView enemiesPlayedCard;

    private ImageView myCollectedDeck;
    private ImageView enemiesCollectedDeck;

    private ImageView trumpfCard;

    private TextView myStandingsView;
    private TextView enemyStandingsView;



    private int indexPlayedCard;
    private boolean haveToWaitForRespond;

    private GameUpdate gameUp;



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

        enemyCardBack1 = findViewById(R.id.enemyCardImageBack1);
        enemyCardBack2 = findViewById(R.id.enemyCardImageBack2);
        enemyCardBack3 = findViewById(R.id.enemyCardImageBack3);
        enemyCardBack4 = findViewById(R.id.enemyCardImageBack4);
        enemyCardBack5 = findViewById(R.id.enemyCardImageBack5);

        enemyCardFront1 = findViewById(R.id.enemyCardImageFront1);
        enemyCardFront2 = findViewById(R.id.enemyCardImageFront2);
        enemyCardFront3 = findViewById(R.id.enemyCardImageFront3);
        enemyCardFront4 = findViewById(R.id.enemyCardImageFront4);
        enemyCardFront5 = findViewById(R.id.enemyCardImageFront5);

        myPlayedCard = findViewById((R.id.myPlayedCard));
        enemiesPlayedCard = findViewById(R.id.enemiesPlayedCard);

        myCollectedDeck = findViewById(R.id.myCollectedDeck);
        enemiesCollectedDeck = findViewById(R.id.enemiesCollectedDeck);

        trumpfCard = findViewById(R.id.trumpf);

        myStandingsView = findViewById(R.id.myNameView);
        enemyStandingsView = findViewById(R.id.enemyNameView);

        game = GameClient.getInstance().getGame();

        for(Player player: GameClient.getInstance().getGame().getPlayers()){
            if(player.getId() == GameClient.getInstance().getClient().getID())me = player;
        }

        setCardHeights();
        initRound();
        setUpCardsDependingOnWhoTurn();
        initStandings();
        setListenerForMyCards();




        playCard.setOnClickListener(v->playCard());
        changeCard.setOnClickListener(v->changeCard());
        respond.setOnClickListener(v->respond());

        Controller controller = new Controller(this);

        game.setUpdateListener(controller.getUpdateListener());
        printInfo();
    }

    public void setCardHeights(){
        int imageViewHeight = getHeight();

        myCard1.getLayoutParams().height=imageViewHeight;
        myCard2.getLayoutParams().height=imageViewHeight;
        myCard3.getLayoutParams().height=imageViewHeight;
        myCard4.getLayoutParams().height=imageViewHeight;
        myCard5.getLayoutParams().height=imageViewHeight;

        enemyCardFront1.getLayoutParams().height=imageViewHeight;
        enemyCardFront2.getLayoutParams().height=imageViewHeight;
        enemyCardFront3.getLayoutParams().height=imageViewHeight;
        enemyCardFront4.getLayoutParams().height=imageViewHeight;
        enemyCardFront5.getLayoutParams().height=imageViewHeight;

        enemyCardBack1.getLayoutParams().height=imageViewHeight;
        enemyCardBack2.getLayoutParams().height=imageViewHeight;
        enemyCardBack3.getLayoutParams().height=imageViewHeight;
        enemyCardBack4.getLayoutParams().height=imageViewHeight;
        enemyCardBack5.getLayoutParams().height=imageViewHeight;

        myPlayedCard.getLayoutParams().height=imageViewHeight;
        enemiesPlayedCard.getLayoutParams().height=imageViewHeight;

        myCollectedDeck.getLayoutParams().height=imageViewHeight;
        enemiesCollectedDeck.getLayoutParams().height=imageViewHeight;

        requestLayouts();

    }

    public int getHeight(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowWidth = size.x;
        int windowHeight = size.y;
        return windowHeight/3-75;
    }

    public void requestLayouts(){
        myCard1.requestLayout();
        myCard2.requestLayout();
        myCard3.requestLayout();
        myCard4.requestLayout();
        myCard5.requestLayout();

        enemyCardFront1.requestLayout();
        enemyCardFront2.requestLayout();
        enemyCardFront3.requestLayout();
        enemyCardFront4.requestLayout();
        enemyCardFront5.requestLayout();

        enemyCardBack1.requestLayout();
        enemyCardBack2.requestLayout();
        enemyCardBack3.requestLayout();
        enemyCardBack4.requestLayout();
        enemyCardBack5.requestLayout();

        myPlayedCard.requestLayout();
        enemiesPlayedCard.requestLayout();

        myCollectedDeck.requestLayout();
        enemiesCollectedDeck.requestLayout();
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

        HandDeck enemiesDeck = game.getOtherPlayer(me).getHandDeck();

        setCardImage(enemyCardFront1, enemiesDeck.getDeck()[0]);
        setCardImage(enemyCardFront2, enemiesDeck.getDeck()[1]);
        setCardImage(enemyCardFront3, enemiesDeck.getDeck()[2]);
        setCardImage(enemyCardFront4, enemiesDeck.getDeck()[3]);
        setCardImage(enemyCardFront5, enemiesDeck.getDeck()[4]);

        /*enemyCardFront1.setVisibility(View.INVISIBLE);
        enemyCardFront2.setVisibility(View.INVISIBLE);
        enemyCardFront3.setVisibility(View.INVISIBLE);
        enemyCardFront4.setVisibility(View.INVISIBLE);
        enemyCardFront5.setVisibility(View.INVISIBLE);*/


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
        myCard1.setOnClickListener((View view)-> translateMyMove(myCard1, myPlayedCard, 0));
        myCard2.setOnClickListener((View view)-> translateMyMove(myCard2, myPlayedCard, 1));
        myCard3.setOnClickListener((View view)-> translateMyMove(myCard3, myPlayedCard,2));
        myCard4.setOnClickListener((View view)-> translateMyMove(myCard4, myPlayedCard,3));
        myCard5.setOnClickListener((View view)-> translateMyMove(myCard5, myPlayedCard,4));


    }

    private void translateMyMove(ImageView viewToMove, ImageView target, int index){
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
                if(bothCardsArePlayed())collectCardPair();

            }
        }, duration);

    }

    private void translateEnemiesMove(int index){
        ImageView target = enemiesPlayedCard;
        ImageView backCard = getEnemiesImageViewBack(index);
        ImageView frontCard = getEnemiesImageViewFront(index);
        int duration = 1500;
        Log.info("check if images found");
        if(backCard==null||frontCard ==null) return;
        Log.info("check if images found");

        backCard.animate()
                .rotationY(180)
                .x(target.getX())
                .y(target.getY())
                .setDuration(duration)
                .start();

        frontCard.animate()
                .alpha(1)
                .rotationY(180)
                .x(target.getX())
                .y(target.getY())
                .setDuration(duration)
                .start();

        frontCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                target.setBackground(frontCard.getBackground());
                frontCard.setAlpha(0);
                backCard.setAlpha(0);
                if(bothCardsArePlayed())collectCardPair();
            }
        }, duration+200);



    }

    public boolean bothCardsArePlayed(){
        Log.info(String.valueOf(myPlayedCard.getBackground() != null && enemiesPlayedCard.getBackground() != null));
        return myPlayedCard.getBackground() != null && enemiesPlayedCard.getBackground() != null;
    }

    public ImageView getEnemiesImageViewBack(int index){
        ImageView viewToReturn=null;

        switch (index){
            case 0:
                viewToReturn = enemyCardBack1;
                break;
            case 1:
                viewToReturn = enemyCardBack2;
                break;
            case 2:
                viewToReturn = enemyCardBack3;
                break;
            case 3:
                viewToReturn = enemyCardBack4;
                break;
            case 4:
                viewToReturn = enemyCardBack5;
                break;
        }

        return viewToReturn;
    }

    public ImageView getEnemiesImageViewFront(int index){
        ImageView viewToReturn=null;

        switch (index){
            case 0:
                viewToReturn = enemyCardFront1;
                break;
            case 1:
                viewToReturn = enemyCardFront2;
                break;
            case 2:
                viewToReturn = enemyCardFront3;
                break;
            case 3:
                viewToReturn = enemyCardFront4;
                break;
            case 4:
                viewToReturn = enemyCardFront5;
                break;
        }

        return viewToReturn;
    }

    public void makeTurn(){
        haveToWaitForRespond=false;
        GameState state;
        if(gameUp!=null)state=gameUp.getGameState();
        else state = game.getGameState();
        if(state == GameState.AWAITING_TURN){
            haveToWaitForRespond=true;
            game.makeTurn(me, new NormalTurn(me.getHandDeck().getDeck()[indexPlayedCard]));
        }
        else if(state == GameState.AWAITING_RESPONSE) {
            game.respondOnTurn(me, me.getHandDeck().getDeck()[indexPlayedCard]);
        }
    }

    public void setUpCardsDependingOnWhoTurn(){
        Player currentPlayer;
        if(gameUp!=null)currentPlayer=gameUp.getCurrentPlayer();
        else currentPlayer = game.getCurrentPlayer();
        if(me.getId()!=currentPlayer.getId()){
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

    public void playEnemiesCard(Card card, ArrayList<HandDeck> oldDecks){
        HandDeck enemiesHandDeck = computeEnemiesDeck(oldDecks);
        Log.info("check if contains");
        for(Card c: enemiesHandDeck.getDeck()){
            Log.info(c.getCardColor()+"/"+c.getCardValue());
        }
        if(enemiesHandDeck.contains(card)){
            Log.info("it does contain");
            translateEnemiesMove(enemiesHandDeck.getCardIndex(card));
        }
    }

    public HandDeck computeEnemiesDeck(ArrayList<HandDeck> oldDecks){
        HandDeck deckToReturn=null;
        boolean isMyDeck;
        for(HandDeck deck: oldDecks){
            isMyDeck=false;
            for(Card card: deck.getDeck()){
                if(card!=null&&me.getHandDeck().contains(card))isMyDeck=true;
            }
            if(!isMyDeck)deckToReturn=deck;
        }
        return deckToReturn;
    }

    public void collectCardPair(){
        if(game.getCurrentPlayer().getId()==me.getId()){
            animateMyCardCollect();
        } else animateEnemiesCardCollect();
    }

    public void animateMyCardCollect(){
        int duration=2000;
        enemiesPlayedCard.animate()
                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())
                .rotationBy(40)
                .setDuration(duration)
                .start();
        myPlayedCard.animate()
                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())
                .rotationBy(40)
                .setDuration(duration)
                .start();

        enemiesPlayedCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                myCollectedDeck.setBackground(enemiesPlayedCard.getBackground());
                enemiesPlayedCard.setBackground(null);
            }
        }, duration);
        myPlayedCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                myPlayedCard.setBackground(null);
            }
        }, duration);
    }

    public void animateEnemiesCardCollect(){
        int duration=2000;
        myPlayedCard.animate()
                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .rotationBy(-40)
                .setDuration(duration)
                .start();
        enemiesPlayedCard.animate()
                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .rotationBy(-40)
                .setDuration(duration)
                .start();
        myPlayedCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                myPlayedCard.setBackground(null);
            }
        }, duration);
        enemiesPlayedCard.postDelayed(new Runnable() {
            @Override
            public void run() {

                enemiesCollectedDeck.setBackground(enemiesPlayedCard.getBackground());
                enemiesPlayedCard.setBackground(null);

            }
        }, duration);
    }

    @Override
    public void playMyCard(int index) {

    }

    @Override
    public void playEnemiesCard(int index) {

    }

    @Override
    public void drawCardForMe() {

    }

    @Override
    public void drawCardForEnemy() {

    }

    @Override
    public void blockCardsWhenNotMyTurn() {

    }


    class UpdateListenerImpl extends UpdateListener{

        @Override
        public void updated(GameUpdate gameUpdate, ArrayList<HandDeck> oldDecks, GameState previousState) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    gameUp = gameUpdate;
                    setUpCardsDependingOnWhoTurn();
                    playEnemiesCard(gameUpdate.getPlayedCard(), oldDecks);
                }
            };
            runOnUiThread(runnable);

        }
    }
}
