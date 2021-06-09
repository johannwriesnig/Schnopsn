package com.schnopsn.core.server.client;

import com.esotericsoftware.kryonet.Client;
import com.schnopsn.core.server.utils.NetworkConstants;
import com.schnopsn.core.server.utils.RegisterHelper;

import java.io.IOException;

import jdk.vm.ci.code.Register;

public class GameClient {
    private Client client;
    private static GameClient instance;

    private GameClient(){
    }

    public static GameClient getInstance(){
        if(instance==null){
            instance = new GameClient();
        }
        return instance;
    }
    public void init(){
        try{
            client = new Client();
            RegisterHelper.registerClasses(client.getKryo());
            client.addListener(new ClientListener());
            client.connect(5000, NetworkConstants.MAIN_SERVER_IP, NetworkConstants.TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
