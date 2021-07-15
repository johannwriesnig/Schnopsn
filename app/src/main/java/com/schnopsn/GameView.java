package com.schnopsn;

public interface GameView {

    void playMyCard(int index);

    void playEnemiesCard(int index);

    void drawCardForMe();

    void drawCardForEnemy();

    void blockCardsWhenNotMyTurn();
}
