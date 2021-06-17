package com.schnopsn.core.server;


import com.esotericsoftware.kryonet.Server;
import com.schnopsn.core.server.utils.NetworkConstants;
import com.schnopsn.core.server.utils.RegisterHelper;
import com.schnopsn.core.server.utils.ServerListener;

import java.io.IOException;


public class MainServer {
    private Server server;

    public MainServer() throws IOException {
        server = new Server();
        server.addListener(new ServerListener(this));
        RegisterHelper.registerClasses(server.getKryo());
        server.start();
        server.bind(NetworkConstants.TCP_PORT);
    }

    public Server getServer() {
        return server;
    }

}
