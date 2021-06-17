package com.schnopsn.core.server.dto.servertoclient;

import com.schnopsn.core.game.Game;
import com.schnopsn.core.server.dto.BaseMessage;

public class InitGame extends BaseMessage {
    Game game;
    public InitGame(Game game){this.game = game;}
    public InitGame(){}

    public Game getGame() {
        return game;
    }
}
