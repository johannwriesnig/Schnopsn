package com.schnopsn.core.game;

import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

public interface GameListener {
    void inform(GameUpdate gameUpdate,GameState previousState);
}
