package com.schnopsn;

import com.schnopsn.core.game.cards.Card;

import java.util.concurrent.Callable;

public interface GameView {

    void playMyCard(int index);

    void playEnemiesCard(int index);

    void drawCardForMeFirst();

    void drawCardForEnemyFirst();

    void blockMyCards();

    void unblockMyCards();

    void collectCardsForMe();

    void collectCardsForEnemy();

    void initDrawCards(Card cardToDraw1, Card cardToDraw2);




}
