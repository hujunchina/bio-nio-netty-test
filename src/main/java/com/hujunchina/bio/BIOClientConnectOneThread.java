package com.hujunchina.bio;

import com.hujunchina.util.Util;
import okhttp3.Route;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class BIOClientConnectOneThread implements Runnable{
    private Socket socket = null;
    private final int PORT = 7250;

    public BIOClientConnectOneThread() throws IOException {
        socket = new Socket("127.0.0.1", PORT);
    }
    public BIOClientConnectOneThread(String ip) throws IOException {
        socket = new Socket(ip, PORT);
    }
    public BIOClientConnectOneThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        Util.echo("Client|>", "Started");
        Scanner scanner = new Scanner(System.in);
//        Util.echo("Client|>", "Please input a client name");
        String inputStr = scanner.nextLine();
        while(!inputStr.equals("q")) {
            try {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                pw.println(inputStr);
                pw.flush();
//                Util.echo("Client|>", "sent a msg");

                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = null;
                while ((line = br.readLine()).length() != 0) {
                    Util.echo("Client|>", line);
                }
                Util.sleep(2000);
                inputStr = scanner.nextLine();
            } catch (IOException e) {
                Util.echo("Client|>", "IO failed.");
                return;
            }
        }
    }
}
