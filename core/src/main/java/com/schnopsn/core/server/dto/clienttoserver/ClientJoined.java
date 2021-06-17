package com.schnopsn.core.server.dto.clienttoserver;

public class ClientJoined {
    String userName;
    public ClientJoined(){

    }

    public ClientJoined(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }
}
