package com.hujunchina;

import com.hujunchina.bio.BIOClient;
import com.hujunchina.bio.BIOServer;
import com.hujunchina.util.Util;

import java.io.IOException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("========== Welcome using my bio model v1.0.3 =========");
        System.out.println("To bootstrap server please input: App server");
        System.out.println("To bootstrap client please input: App client");
        System.out.println("You can order a host: App client 127.0.0.1");
        System.out.println("======================================================");
//        System.out.println(Arrays.toString(args));

        if(args[0].equals("server")){
            BIOServer bioServer = new BIOServer();
            Thread server = new Thread(bioServer);
            server.start();
        }else if(args[0].equals("client")) {
            String host = "127.0.0.1";
            if(args.length > 1) {
                host = args[1];
                System.out.println("Using new host "+host);
            }
            BIOClient bioClient = new BIOClient(host);
            Thread client = new Thread(bioClient);
            client.start();
        }else{
            System.out.println("please input one argument, eg: App server/client");
        }
    }
}
