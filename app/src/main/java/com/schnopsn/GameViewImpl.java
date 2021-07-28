package com.schnopsn;

import android.animation.Animator;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esotericsoftware.minlog.Log;
import com.schnopsn.core.game.Game;
import com.schnopsn.core.game.Player;
import com.schnopsn.core.game.cards.Card;
import com.schnopsn.core.game.cards.HandDeck;
import com.schnopsn.core.server.client.GameClient;


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

    private ImageView myPlayedCardFront;
    private ImageView myPlayedCardBack;

    private ImageView enemiesPlayedCardFront;
    private ImageView enemiesPlayedCardBack;

    private ImageView myCollectedDeck;
    private ImageView enemiesCollectedDeck;

    private ImageView deckCard3Front;
    private ImageView deckCard3Back;

    private ImageView deckCard2Front;
    private ImageView deckCard2Back;

    private ImageView deckCard1;

    private ImageView trumpfCard;

    private TextView myStandingsView;
    private TextView enemyStandingsView;

    private Controller controller;

    private Handler handler;

    private boolean cardsAreBlocked=false;

    private final int DURATION_PLAY_CARD = 900;
    private final int DURATION_COLLECT_CARDS = 1050;
    private final int DURATION_DRAW_CARDS = 1000;


    private int indexMyPlayedCard;
    private int indexEnemiesPlayedCard;

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

        myPlayedCardFront = findViewById((R.id.myPlayedCardFront));
        myPlayedCardBack = findViewById(R.id.myPlayedCardBack);

        enemiesPlayedCardFront = findViewById(R.id.enemiesPlayedCardFront);
        enemiesPlayedCardBack = findViewById(R.id.enemiesPlayedCardBack);

        myCollectedDeck = findViewById(R.id.myCollectedDeck);
        enemiesCollectedDeck = findViewById(R.id.enemiesCollectedDeck);

        deckCard1 = findViewById(R.id.deckCard1);

        deckCard2Front = findViewById(R.id.deckCard2Front);
        deckCard2Back = findViewById(R.id.deckCard2Back);

        deckCard3Front = findViewById(R.id.deckCard3Front);
        deckCard3Back = findViewById(R.id.deckCard3Back);

        trumpfCard = findViewById(R.id.trumpf);

        myStandingsView = findViewById(R.id.myNameView);
        enemyStandingsView = findViewById(R.id.enemyNameView);

        game = GameClient.getInstance().getGame();

        for (Player player : GameClient.getInstance().getGame().getPlayers()) {
            if (player.getId() == GameClient.getInstance().getClient().getID()) me = player;
        }

        setCardHeights();
        initRound();
        initStandings();
        setListenerForMyCards();
        blockCardsInBeginning();
        controller = new Controller(this);

        printInfo();
    }

    public void setCardHeights() {
        int imageViewHeight = getHeight();

        myCard1.getLayoutParams().height = imageViewHeight;
        myCard2.getLayoutParams().height = imageViewHeight;
        myCard3.getLayoutParams().height = imageViewHeight;
        myCard4.getLayoutParams().height = imageViewHeight;
        myCard5.getLayoutParams().height = imageViewHeight;

        enemyCardFront1.getLayoutParams().height = imageViewHeight;
        enemyCardFront2.getLayoutParams().height = imageViewHeight;
        enemyCardFront3.getLayoutParams().height = imageViewHeight;
        enemyCardFront4.getLayoutParams().height = imageViewHeight;
        enemyCardFront5.getLayoutParams().height = imageViewHeight;

        enemyCardBack1.getLayoutParams().height = imageViewHeight;
        enemyCardBack2.getLayoutParams().height = imageViewHeight;
        enemyCardBack3.getLayoutParams().height = imageViewHeight;
        enemyCardBack4.getLayoutParams().height = imageViewHeight;
        enemyCardBack5.getLayoutParams().height = imageViewHeight;

        myPlayedCardFront.getLayoutParams().height = imageViewHeight;
        myPlayedCardBack.getLayoutParams().height = imageViewHeight;

        enemiesPlayedCardFront.getLayoutParams().height = imageViewHeight;
        enemiesPlayedCardBack.getLayoutParams().height = imageViewHeight;

        myCollectedDeck.getLayoutParams().height = imageViewHeight;
        enemiesCollectedDeck.getLayoutParams().height = imageViewHeight;

        deckCard1.getLayoutParams().height = imageViewHeight;

        deckCard2Front.getLayoutParams().height = imageViewHeight;
        deckCard2Back.getLayoutParams().height = imageViewHeight;

        deckCard3Front.getLayoutParams().height = imageViewHeight;
        deckCard3Back.getLayoutParams().height = imageViewHeight;

        requestLayouts();

    }

    public int getHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowWidth = size.x;
        int windowHeight = size.y;
        return windowHeight / 3 - 75;
    }

    public void requestLayouts() {
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

        myPlayedCardFront.requestLayout();
        myPlayedCardBack.requestLayout();

        enemiesPlayedCardFront.requestLayout();
        enemiesPlayedCardBack.requestLayout();

        myCollectedDeck.requestLayout();
        enemiesCollectedDeck.requestLayout();

        deckCard1.requestLayout();
        deckCard2Front.requestLayout();
        deckCard2Back.requestLayout();
        deckCard3Back.requestLayout();
        deckCard3Front.requestLayout();
    }


    public void initRound() {
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


    public void setCardImage(ImageView view, Card card) {
        switch (card.getCardColor()) {
            case PIK:
                switch (card.getCardValue()) {
                    case ASS:
                        view.setBackgroundResource(R.drawable.pikass);
                        break;
                    case ZEHNER:
                        view.setBackgroundResource(R.drawable.pikzehn);
                        break;
                    case KOENIG:
                        view.setBackgroundResource(R.drawable.pikkoenig);
                        break;
                    case DAME:
                        view.setBackgroundResource(R.drawable.pikdame);
                        break;
                    case BUBE:
                        view.setBackgroundResource(R.drawable.pikbub);
                        break;
                }
                break;
            case KARO:
                switch (card.getCardValue()) {
                    case ASS:
                        view.setBackgroundResource(R.drawable.karoass);
                        break;
                    case ZEHNER:
                        view.setBackgroundResource(R.drawable.karozehn);
                        break;
                    case KOENIG:
                        view.setBackgroundResource(R.drawable.karokoenig);
                        break;
                    case DAME:
                        view.setBackgroundResource(R.drawable.karodame);
                        break;
                    case BUBE:
                        view.setBackgroundResource(R.drawable.karobub);
                        break;
                }
                break;
            case KREUZ:
                switch (card.getCardValue()) {
                    case ASS:
                        view.setBackgroundResource(R.drawable.kreuzass);
                        break;
                    case ZEHNER:
                        view.setBackgroundResource(R.drawable.kreuzzehn);
                        break;
                    case KOENIG:
                        view.setBackgroundResource(R.drawable.kreuzkoenig);
                        break;
                    case DAME:
                        view.setBackgroundResource(R.drawable.kreuzdame);
                        break;
                    case BUBE:
                        view.setBackgroundResource(R.drawable.kreuzbub);
                        break;
                }
                break;
            case HERZ:
                switch (card.getCardValue()) {
                    case ASS:
                        view.setBackgroundResource(R.drawable.herzass);
                        break;
                    case ZEHNER:
                        view.setBackgroundResource(R.drawable.herzzehn);
                        break;
                    case KOENIG:
                        view.setBackgroundResource(R.drawable.herzkoenig);
                        break;
                    case DAME:
                        view.setBackgroundResource(R.drawable.herzdame);
                        break;
                    case BUBE:
                        view.setBackgroundResource(R.drawable.herzbub);
                        break;
                }
                break;
        }
    }

    public void initStandings() {
        String myStandings;
        myStandings = me.getName() + ": " + me.getBummerl();
        myStandingsView.setText(myStandings);

        String enemyStandings;
        Player enemy = game.getOtherPlayer(me);
        enemyStandings = enemy.getName() + ": " + enemy.getBummerl();
        enemyStandingsView.setText(enemyStandings);
    }

    public void printInfo() {
        Log.info("Trumpf: " + game.getTrumpf().getCardColor() + " / " + game.getTrumpf().getCardValue());
        Log.info("This is me: " + GameClient.getInstance().getClient().getID());
        Log.info("CurrentPlayer is: " + game.getCurrentPlayer().getId());
        Log.info("MyDeck: ");
        for (Card card : me.getHandDeck().getDeck()) {
            if (card != null)
                Log.info("Farbe: " + card.getCardColor() + " / Wert: " + card.getCardValue());
        }
    }

    public void setListenerForMyCards() {
        myCard1.setOnClickListener((View view) -> controller.playCard(0));
        myCard2.setOnClickListener((View view) -> controller.playCard(1));
        myCard3.setOnClickListener((View view) -> controller.playCard(2));
        myCard4.setOnClickListener((View view) -> controller.playCard(3));
        myCard5.setOnClickListener((View view) -> controller.playCard(4));

    }


    public ImageView getEnemiesImageViewBack(int index) {
        ImageView viewToReturn = null;

        switch (index) {
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

    public ImageView getEnemiesImageViewFront(int index) {
        ImageView viewToReturn = null;

        switch (index) {
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
                indexMyPlayedCard = index;
                ImageView viewToMove = getMyCardAsImageView(index);
                float viewToMoveX = viewToMove.getX();
                float viewToMoveY = viewToMove.getY();
                viewToMove.animate()
                        .x(myPlayedCardFront.getX())
                        .y(myPlayedCardFront.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                myPlayedCardFront.setBackground(viewToMove.getBackground());
                                myPlayedCardFront.animate().alpha(1).setDuration(0).setListener(null).start();
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

    public ImageView getMyCardAsImageView(int index) {
        ImageView viewToReturn;

        switch (index) {
            case 0:
                viewToReturn = myCard1;
                break;
            case 1:
                viewToReturn = myCard2;
                break;
            case 2:
                viewToReturn = myCard3;
                break;
            case 3:
                viewToReturn = myCard4;
                break;
            case 4:
                viewToReturn = myCard5;
                break;
            default:
                viewToReturn = myCard1;
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
                indexEnemiesPlayedCard = index;
                ImageView target = enemiesPlayedCardFront;
                ImageView backCard = getEnemiesImageViewBack(index);
                ImageView frontCard = getEnemiesImageViewFront(index);

                if (backCard == null || frontCard == null) return;


                float backCardX = backCard.getX();
                float backCardY = backCard.getY();
                backCard.animate()
                        .rotationYBy(180)
                        .x(target.getX())
                        .y(target.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                backCard.animate().alpha(0).rotationYBy(180).x(backCardX).y(backCardY).setDuration(0).setListener(null).start();

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
                        .rotationYBy(180)
                        .x(target.getX())
                        .y(target.getY())
                        .setDuration(DURATION_PLAY_CARD)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                enemiesPlayedCardFront.setBackground(frontCard.getBackground());
                                enemiesPlayedCardFront.animate().alpha(1).setDuration(0).setListener(null).start();
                                frontCard.animate().alpha(0).rotationYBy(180).x(frontCardX).y(frontCardY).setDuration(0).setListener(null).start();
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
    public void drawCardForMeFirst() {
        animateMyCardDraw(getMyCardAsImageView(indexMyPlayedCard), deckCard3Front, deckCard3Back);
        animateEnemiesCardDraw(getEnemiesImageViewFront(indexEnemiesPlayedCard),getEnemiesImageViewBack(indexEnemiesPlayedCard), deckCard2Front, deckCard2Back);
    }

    @Override
    public void drawCardForEnemyFirst() {
        animateEnemiesCardDraw(getEnemiesImageViewFront(indexEnemiesPlayedCard),getEnemiesImageViewBack(indexEnemiesPlayedCard), deckCard3Front, deckCard3Back);
        animateMyCardDraw(getMyCardAsImageView(indexMyPlayedCard), deckCard2Front, deckCard2Back);

    }

    @Override
    public void initDrawCards(Card cardToDraw1, Card cardToDraw2) {
        runOnUiThread(() -> {
            setCardImage(deckCard3Front, cardToDraw1);
            setCardImage(deckCard2Front, cardToDraw2);
        });

    }

    public void animateEnemiesCardDraw(ImageView targetFront,ImageView targetBack, ImageView deckCardFront, ImageView deckCardBack){
        Runnable animation = () -> {
            float deckCardBackX = deckCardBack.getX();
            float deckCardBackY = deckCardBack.getY();
            deckCardBack.setAlpha(1f);
            deckCardBack.animate()
                    .x(targetFront.getX())
                    .y(targetFront.getY())
                    .setDuration(DURATION_DRAW_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            targetFront.setBackground(deckCardFront.getBackground());
                            targetBack.setAlpha(1f);
                            deckCardFront.setAlpha(0f);
                            deckCardBack.animate().alpha(0).x(deckCardBackX).y(deckCardBackY).setDuration(0).setListener(null).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start(); };
        handler.postDelayed(animation, DURATION_COLLECT_CARDS + DURATION_PLAY_CARD+100);
    }

    public void animateMyCardDraw(ImageView target, ImageView deckCardFront, ImageView deckCardBack) {
        Runnable animation = () -> {
            float deckCardFrontX = deckCardFront.getX();
            float deckCardFrontY = deckCardFront.getY();

            deckCardFront.animate()
                    .alpha(1f)
                    .rotationYBy(180)
                    .x(target.getX())
                    .y(target.getY())
                    .setDuration(DURATION_DRAW_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            target.setBackground(deckCardFront.getBackground());
                            if(cardsAreBlocked)blockCard(target);
                            else unblockCard(target);
                            deckCardFront.animate().alpha(0f).rotationYBy(180).x(deckCardFrontX).y(deckCardFrontY).setDuration(0).setListener(null).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();

            float deckCardBackX = deckCardBack.getX();
            float deckCardBackY = deckCardBack.getY();
            deckCardBack.setAlpha(1f);
            deckCardBack.animate()
                    .rotationYBy(180)
                    .x(target.getX())
                    .y(target.getY())
                    .alpha(0f)
                    .setDuration(DURATION_DRAW_CARDS)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation1) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation1) {
                            deckCardBack.animate().alpha(0f).rotationYBy(180).x(deckCardBackX).y(deckCardBackY).setDuration(0).setListener(null).start();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation1) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation1) {

                        }
                    }).start();
        };
        handler.postDelayed(animation, DURATION_COLLECT_CARDS + DURATION_PLAY_CARD+100);
    }

    public void blockCard(ImageView target){
        target.setAlpha(0.5f);
        target.setEnabled(false);
    }

    public void unblockCard(ImageView target){
        target.setAlpha(1f);
        target.setEnabled(true);
    }

    public void blockCardsInBeginning() {
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

        enemiesPlayedCardBack.animate().alpha(0).setDuration(0).start();
        myPlayedCardBack.animate().alpha(0).setDuration(0).start();

        deckCard2Front.animate().alpha(0).setDuration(0).start();
        deckCard3Front.animate().alpha(0).setDuration(0).start();

    }

    @Override
    public void blockMyCards() {
        Runnable animation = new Runnable() {
            @Override
            public void run() {

                if (cardIsVisible(myCard1)) {
                    myCard1.setAlpha(0.5f);
                    myCard1.setEnabled(false);
                }

                if (cardIsVisible(myCard2)) {
                    myCard2.setAlpha(0.5f);
                    myCard2.setEnabled(false);
                }

                if (cardIsVisible(myCard3)) {
                    myCard3.setAlpha(0.5f);
                    myCard3.setEnabled(false);
                }

                if (cardIsVisible(myCard4)) {
                    myCard4.setAlpha(0.5f);
                    myCard4.setEnabled(false);
                }

                if (cardIsVisible(myCard5)) {
                    myCard5.setAlpha(0.5f);
                    myCard5.setEnabled(false);
                }
                cardsAreBlocked =true;
            }
        };

        handler.postDelayed(animation, DURATION_PLAY_CARD);
    }

    @Override
    public void unblockMyCards() {
        Runnable animation = new Runnable() {
            @Override
            public void run() {
                if (cardIsVisible(myCard1)) {
                    myCard1.setAlpha(1f);
                    myCard1.setEnabled(true);
                }

                if (cardIsVisible(myCard2)) {
                    myCard2.setAlpha(1f);
                    myCard2.setEnabled(true);
                }

                if (cardIsVisible(myCard3)) {
                    myCard3.setAlpha(1f);
                    myCard3.setEnabled(true);
                }

                if (cardIsVisible(myCard4)) {
                    myCard4.setAlpha(1f);
                    myCard4.setEnabled(true);
                }

                if (cardIsVisible(myCard5)) {
                    myCard5.setAlpha(1f);
                    myCard5.setEnabled(true);
                }
                cardsAreBlocked = false;
            }
        };
        handler.postDelayed(animation, DURATION_PLAY_CARD);
    }

    public boolean cardIsVisible(ImageView imageView) {
        return imageView.getAlpha() != 0;
    }

    @Override
    public void collectCardsForEnemy() {
        Runnable animation = () -> {
            animateEnemyCardsToEnemiesStaple();
            animateMyCardsToEnemiesStaple();
        };

        handler.postDelayed(animation, DURATION_PLAY_CARD + 100);
    }


    public void animateEnemyCardsToEnemiesStaple() {
        float enemiesPlayedCardX = enemiesPlayedCardFront.getX();
        float enemiesPlayedCardY = enemiesPlayedCardFront.getY();
        enemiesPlayedCardFront.animate()
                .rotationYBy(-180)
                .rotationBy(-40)
                .alpha(0f)
                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        enemiesPlayedCardFront.animate().alpha(0).rotationYBy(180).rotationBy(40).x(enemiesPlayedCardX).y(enemiesPlayedCardY)
                                .setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {
                        Log.info("animation got cancelled");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                })

                .start();

        enemiesPlayedCardBack.animate()
                .alpha(1)
                .rotationYBy(-180)
                .rotationBy(-40)
                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        enemiesPlayedCardBack.animate().alpha(0).rotationYBy(180).rotationBy(40).x(enemiesPlayedCardX).y(enemiesPlayedCardY)
                                .setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();
    }

    public void animateMyCardsToEnemiesStaple() {
        float myPlayedCardX = myPlayedCardFront.getX();
        float myPlayedCardY = myPlayedCardFront.getY();
        myPlayedCardFront.animate()
                .alpha(0f)
                .rotationYBy(180)
                .rotationBy(40)
                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        if (enemiesCollectedDeck.getBackground() == null)
                            enemiesCollectedDeck.setBackgroundResource(R.drawable.cardback);
                        myPlayedCardFront.animate().rotationYBy(180).rotationBy(-40).alpha(0).x(myPlayedCardX).y(myPlayedCardY).setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();

        float myPlayedCardBackX = myPlayedCardBack.getX();
        float myPlayedCardBackY = myPlayedCardBack.getY();
        myPlayedCardBack.animate()
                .alpha(1)
                .rotationYBy(180)
                .rotationBy(40)

                .x(enemiesCollectedDeck.getX())
                .y(enemiesCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        myPlayedCardBack.animate().alpha(0).rotationYBy(180).rotationBy(-40).x(myPlayedCardBackX).y(myPlayedCardBackY).setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();


    }

    @Override
    public void collectCardsForMe() {
        Runnable animation = () -> {
            animateEnemyCardsToMyStaple();
            animateMyCardsToMyStaple();

        };
        handler.postDelayed(animation, DURATION_PLAY_CARD + 100);
    }

    public void animateEnemyCardsToMyStaple() {
        float enemiesPlayedCardX = enemiesPlayedCardFront.getX();
        float enemiesPlayedCardY = enemiesPlayedCardFront.getY();
        enemiesPlayedCardFront.animate()
                .alpha(0f)
                .rotationYBy(-180)
                .rotationBy(40)
                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        enemiesPlayedCardFront.animate().alpha(0).rotationYBy(180).rotationBy(-40).x(enemiesPlayedCardX).y(enemiesPlayedCardY)
                                .setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {
                        Log.info("animation got cancelled");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                })

                .start();

        enemiesPlayedCardBack.animate()
                .alpha(1)
                .rotationYBy(-180)
                .rotationBy(40)
                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {
                        if (myCollectedDeck.getBackground() == null)
                            myCollectedDeck.setBackgroundResource(R.drawable.cardback);
                        enemiesPlayedCardBack.animate().alpha(0).rotationYBy(180).rotationBy(-40).x(enemiesPlayedCardX).y(enemiesPlayedCardY)
                                .setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();
    }

    public void animateMyCardsToMyStaple() {
        float myPlayedCardX = myPlayedCardFront.getX();
        float myPlayedCardY = myPlayedCardFront.getY();
        myPlayedCardFront.animate()
                .alpha(0f)
                .rotationYBy(180)
                .rotationBy(-40)
                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())

                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {

                        myPlayedCardFront.animate().rotationYBy(180).rotationBy(40).alpha(0).x(myPlayedCardX).y(myPlayedCardY).setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();

        float myPlayedCardBackX = myPlayedCardBack.getX();
        float myPlayedCardBackY = myPlayedCardBack.getY();
        myPlayedCardBack.animate()
                .alpha(1)
                .rotationYBy(180)
                .rotationBy(-40)

                .x(myCollectedDeck.getX())
                .y(myCollectedDeck.getY())
                .setDuration(DURATION_COLLECT_CARDS)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation1) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation1) {

                        myPlayedCardBack.animate().alpha(0).rotationYBy(180).rotationBy(40).x(myPlayedCardBackX).y(myPlayedCardBackY).setDuration(0).setListener(null).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation1) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation1) {

                    }
                }).start();


    }



}
