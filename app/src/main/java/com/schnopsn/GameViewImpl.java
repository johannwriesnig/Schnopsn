package com.schnopsn;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
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
import com.schnopsn.core.server.dto.servertoclient.InitGame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class GameViewImpl extends AppCompatActivity implements GameView {
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

    private Controller controller;

    private Handler handler;

    private final int DURATION_PLAY_CARD = 900;
    private final int DURATION_COLLECT_CARDS = 900;


    private int indexPlayedCard;
    private boolean haveToWaitForRespond;

    private GameUpdate gameUp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        handler = new Handler();

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
        initStandings();
        setListenerForMyCards();
        blockMyCards();
        controller = new Controller(this);

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

    public void setListenerForMyCards(){
        myCard1.setOnClickListener((View view)-> controller.playCard(0));
        myCard2.setOnClickListener((View view)-> controller.playCard(1));
        myCard3.setOnClickListener((View view)-> controller.playCard(2));
        myCard4.setOnClickListener((View view)-> controller.playCard(3));
        myCard5.setOnClickListener((View view)-> controller.playCard(4));

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


    @Override
    public void playMyCard(int index) {
        Log.info("playMycard gets called");
        Runnable animation = new Runnable() {
            @Override
            public void run() {
                Log.info("We are in runnable: playMyCard");
                indexPlayedCard=index;
                ImageView viewToMove = getMyCardAsImageView(index);
                float viewToMoveX = viewToMove.getX();
                float viewToMoveY = viewToMove.getY();
                viewToMove.animate()
                        .x(myPlayedCard.getX())
                        .y(myPlayedCard.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                myPlayedCard.setBackground(viewToMove.getBackground());
                                myPlayedCard.animate().alpha(1).setDuration(0).setListener(null).start();
                                viewToMove.animate().alpha(0).x(viewToMoveX).y(viewToMoveY).setDuration(0).setListener(null).start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

            }
        };
        handler.post(animation);
    }

    public ImageView getMyCardAsImageView(int index){
        ImageView viewToReturn;

        switch(index){
            case 0: viewToReturn=myCard1;
                    break;
            case 1: viewToReturn=myCard2;
                    break;
            case 2: viewToReturn=myCard3;
                break;
            case 3: viewToReturn=myCard4;
                break;
            case 4: viewToReturn=myCard5;
                break;
            default:viewToReturn=myCard1;
        }
         return viewToReturn;
    }

    @Override
    public void playEnemiesCard(int index) {
        Log.info("playEnemiesCard gets called");
        Runnable animation = new Runnable() {
            @Override
            public void run() {
                Log.info("We are in runnable");
                ImageView target = enemiesPlayedCard;
                ImageView backCard = getEnemiesImageViewBack(index);
                ImageView frontCard = getEnemiesImageViewFront(index);

                if(backCard==null||frontCard==null)return;


                float backCardX = backCard.getX();
                float backCardY = backCard.getY();
                backCard.animate()
                        .rotationY(180)
                        .x(target.getX())
                        .y(target.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                backCard.animate().alpha(0).rotationY(180).x(backCardX).y(backCardY).setDuration(0).setListener(null).start();

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

                float frontCardX = frontCard.getX();
                float frontCardY = frontCard.getY();
                frontCard.animate()
                        .alpha(1)
                        .rotationY(180)
                        .x(target.getX())
                        .y(target.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                enemiesPlayedCard.setBackground(frontCard.getBackground());
                                enemiesPlayedCard.animate().alpha(1).setDuration(0).setListener(null).start();
                                frontCard.animate().alpha(0).x(frontCardX ).y(frontCardY).setDuration(0).setListener(null).start();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();

            }
        };

        handler.post(animation);

    }

    @Override
    public void drawCardForMe() {

    }

    @Override
    public void drawCardForEnemy() {

    }

    @Override
    public void blockMyCards() {
        Runnable animation = new Runnable() {
            @Override
            public void run() {

                if(cardIsVisible(myCard1)){
                myCard1.setAlpha(0.5f);
                myCard1.setEnabled(false);
                }

                if(cardIsVisible(myCard2)){
                myCard2.setAlpha(0.5f);
                myCard2.setEnabled(false);}

                if(cardIsVisible(myCard3)){
                myCard3.setAlpha(0.5f);
                myCard3.setEnabled(false);}

                if(cardIsVisible(myCard4)){
                myCard4.setAlpha(0.5f);
                myCard4.setEnabled(false);}

                if(cardIsVisible(myCard5)){
                myCard5.setAlpha(0.5f);
                myCard5.setEnabled(false);}
            }
        };

        handler.postDelayed(animation, DURATION_PLAY_CARD);
    }

    @Override
    public void unblockMyCards(){
        Runnable animation = new Runnable() {
            @Override
            public void run() {
                if(cardIsVisible(myCard1)){
                myCard1.setAlpha(1f);
                myCard1.setEnabled(true);}

                if(cardIsVisible(myCard2)){
                myCard2.setAlpha(1f);
                myCard2.setEnabled(true);}

                if(cardIsVisible(myCard3)){
                myCard3.setAlpha(1f);
                myCard3.setEnabled(true);}

                if(cardIsVisible(myCard4)){
                myCard4.setAlpha(1f);
                myCard4.setEnabled(true);}

                if(cardIsVisible(myCard5)){
                myCard5.setAlpha(1f);
                myCard5.setEnabled(true);}
            }
        };
        handler.postDelayed(animation, DURATION_PLAY_CARD);
    }

    public boolean cardIsVisible(ImageView imageView){
        return imageView.getAlpha() != 0;
    }
    @Override
    public void collectCardsForEnemy() {
        Runnable animation = () -> {

            float myPlayedCardX = myPlayedCard.getX();
            float myPlayedCardY = myPlayedCard.getY();
            myPlayedCard.animate()
                    .x(enemiesCollectedDeck.getX())
                    .y(enemiesCollectedDeck.getY())
                    .rotationY(180)
                    .rotationBy(-40)
                    .setDuration(DURATION_COLLECT_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            if(enemiesCollectedDeck.getBackground()==null)enemiesCollectedDeck.setBackgroundResource(R.drawable.cardback);
                            myPlayedCard.animate().alpha(0).x(myPlayedCardX).y(myPlayedCardY).rotationY(-180).rotationBy(40).setListener(null).setDuration(0).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();

            float enemiesPlayedCardX = enemiesPlayedCard.getX();
            float enemiesPlayedCardY = enemiesPlayedCard.getY();

            enemiesPlayedCard.animate()
                    .x(enemiesCollectedDeck.getX())
                    .y(enemiesCollectedDeck.getY())
                    .rotationY(180)
                    .rotationBy(-40)
                    .setDuration(DURATION_COLLECT_CARDS)
                    .setListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            enemiesPlayedCard.animate().alpha(0).rotationBy(40).rotationY(-180).x(enemiesPlayedCardX).y(enemiesPlayedCardY)
                                    .setListener(null)
                                    .setDuration(0)
                                    .start();


                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();


        };

        handler.postDelayed(animation,DURATION_PLAY_CARD+100);


    }

    @Override
    public void collectCardsForMe() {
        Runnable animation = () -> {

            float enemiesPlayedCardX = enemiesPlayedCard.getX();
            float enemiesPlayedCardY = enemiesPlayedCard.getY();
            enemiesPlayedCard.animate()
                    .x(myCollectedDeck.getX())
                    .y(myCollectedDeck.getY())
                    .rotationY(180)
                    .rotationBy(40)
                    .setDuration(DURATION_COLLECT_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            if(myCollectedDeck.getBackground()==null)myCollectedDeck.setBackgroundResource(R.drawable.cardback);
                            enemiesPlayedCard.animate().alpha(0).x(enemiesPlayedCardX).y(enemiesPlayedCardY).rotationY(-180)
                                    .rotationBy(-40).setDuration(0).setListener(null).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {
                            Log.info("animation got cancelled");
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();
            float myPlayedCardX = myPlayedCard.getX();
            float myPlayedCardY = myPlayedCard.getY();
            myPlayedCard.animate()
                    .x(myCollectedDeck.getX())
                    .y(myCollectedDeck.getY())
                    .rotationY(180)
                    .rotationBy(40)
                    .setDuration(DURATION_COLLECT_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            myPlayedCard.animate().alpha(0).x(myPlayedCardX).y(myPlayedCardY).rotationBy(-40).rotationY(-180).setDuration(0).setListener(null).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();


        };
        handler.postDelayed(animation,DURATION_PLAY_CARD+100);
    }


}
