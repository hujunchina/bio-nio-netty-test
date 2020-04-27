package com.hujunchina;

import com.hujunchina.bio.BIOClient;
import com.hujunchina.bio.BIOServer;
import com.hujunchina.util.Util;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        BIOServer bioServer = new BIOServer();
        Thread server = new Thread(bioServer);
        server.start();

        Util.sleep(1000);
        BIOClient bioClient = new BIOClient("127.0.0.1");
        Thread client = new Thread(bioClient);
        client.start();
    }
}
