package com.hujunchina;

import com.hujunchina.bio.BIOClient;
import com.hujunchina.bio.BIOClientConnectOneThread;
import com.hujunchina.bio.BIOServer;
import com.hujunchina.bio.BIOServerConnectOneThread;
import com.hujunchina.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException {
        greeting();
        if(args.length==0){
            System.out.println("please input one argument, eg: App server/client");
            return;
        }

        if(args[0].equals("1")){
            new Thread(new BIOServer()).start();
        }else if(args[0].equals("2")) {
            String host = "127.0.0.1";
            if(args.length > 1) {
                host = args[1];
                System.out.println("Using new host "+host);
            }
           new Thread(new BIOClient(host)).start();
        }else if(args[0].equals("3")){
            new Thread(new BIOServerConnectOneThread()).start();
        }else if(args[0].equals("4")){
            String host = "127.0.0.1";
            if(args.length > 1) {
                host = args[1];
                System.out.println("Using new host "+host);
            }
            new Thread(new BIOClientConnectOneThread(host)).start();
        }
    }

    private static void greeting(){
        System.out.println("========== Welcome using my bio model v1.1.6 =========");
        System.out.println("To bootstrap model please input: App # ");
        System.out.println("You can order a host: App client 127.0.0.1");
        System.out.println("========== 1. bootstrap base bio model server ========");
        System.out.println("========== 2. bootstrap base bio model client ========");
        System.out.println("========== 3. bootstrap cpt bio model server  ========");
        System.out.println("========== 4. bootstrap cpt bio model client  ========");
        System.out.println("==========");
        System.out.println("===================== cpt = connection per thread ====");
        System.out.println("======================================================");
    }
}
