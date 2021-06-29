package com.schnopsn.core.game;

import com.schnopsn.core.server.dto.servertoclient.GameUpdate;

abstract public class UpdateListener {
    abstract public void updated(GameUpdate gameUpdate);
}
