package com.hujunchina.bio;

import com.hujunchina.util.Util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

    @Override
    public void run() {
        while (true) {
            try {
//                第一个阻塞
                socketClient = serverSocket.accept();
                Util.echo("Server", "One Client coming in...");
//                得到输入流  第二个阻塞
                InputStream in = socketClient.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String res = null;
                while((res = br.readLine()) != "") {
                    Util.echo("Server got a msg:", res);

                }

//               得到输出流，单通道！ 需要新的线程去写入
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            OutputStream out = socketClient.getOutputStream();
//                            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
//                            pw.write("A msg from Server");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();

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
