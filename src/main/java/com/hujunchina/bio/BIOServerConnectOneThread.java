package com.hujunchina.bio;

import com.hujunchina.util.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOServerConnectOneThread implements Runnable{
    private final int PORT = 7250;
    private ServerSocket serverSocket = null;

    public BIOServerConnectOneThread() throws IOException {
        serverSocket = new ServerSocket(PORT);
    }
    public BIOServerConnectOneThread(int PORT) throws IOException {
        serverSocket = new ServerSocket(PORT);
    }
    public BIOServerConnectOneThread(ServerSocket ss){
        serverSocket = ss;
    }

    @Override
    public void run() {
        Util.echo("Server", "Started");
        while(!Thread.interrupted()){
            try {
                Socket client = serverSocket.accept();
                new Thread(new ClientHandler(client)).start();
                Util.echo("Server|>", "a client comes in");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler implements Runnable{
        private Socket client = null;
        public ClientHandler(Socket client){
            this.client = client;
        }

        @Override
        public void run() {
//            先读入数据，然后加工
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String line = null;
                while((line = br.readLine())!=null){
                    Util.echo("Server got a msg:", line);
                    response(line);
                }

            } catch (IOException e) {
                Util.echo("Server|> ", "IO failed.");
            }
        }

        private void response(String req){
            try {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
                String res = Util.analysisRequest(req);
                pw.println(res);
                pw.println();
                pw.flush();
                Util.echo("Server send a msg:", res);
            } catch (IOException e) {
                Util.echo("Server|> ", "response failed.");
            }
        }
    }
}
