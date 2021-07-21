package com.schnopsn;

import java.util.concurrent.Callable;

public interface GameView {

    void playMyCard(int index);

    void playEnemiesCard(int index);

    void drawCardForMe();

    void drawCardForEnemy();

    void blockMyCards();

    void unblockMyCards();

    void collectCardsForMe();

    void collectCardsForEnemy();




}
