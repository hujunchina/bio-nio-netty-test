package com.hujunchina.bio;

import com.hujunchina.util.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class BIOServer implements Runnable {
    private final int PORT = 7250;
    private ServerSocket serverSocket = null;
    private Socket socketClient = null;

    public BIOServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        Util.echo("Server", "Started");
    }

    public BIOServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        Util.echo("Server", "Started");
    }

    public BIOServer(int PORT) throws IOException {
        serverSocket = new ServerSocket(PORT);
        Util.echo("Server", "Started");
    }

    private static class SendHandler implements Runnable{
        private Socket socket = null;
        public SendHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            while(true) {
                try {
                    OutputStream out = socket.getOutputStream();
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                    // 如何做心跳包
                    pw.write("A msg from Server during 10s");
                    pw.flush();
                    Util.sleep(10000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
//                第一个阻塞
                socketClient = serverSocket.accept();
                Thread t = new Thread(new SendHandler(socketClient));
                t.start();
                Util.echo("Server", "One Client coming in...");
                InputStream in = socketClient.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String res = null;
//                得到输入流  第二个阻塞
                while((res = br.readLine()) != "") {
                    Util.echo("Server got a msg:", res);
                }

                if(socketClient.isClosed()){
                    Util.echo("Server:", "Client disconnected.");
                }

            } catch (IOException e) {
                Util.echo("Server", "IOException");
                if( serverSocket != null){
                    socketClient = null;
                }
                if( socketClient != null){
                    socketClient = null;
                }
            }
        }
    }
}
