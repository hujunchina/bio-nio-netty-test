package com.hujunchina.bio;

import com.hujunchina.util.Util;

import java.io.*;
import java.net.Socket;

public class BIOClient implements Runnable {
    private final int PORT = 7250;
    private final String HOST = "47.114.146.180";
    private Socket socket = null;

    public BIOClient() throws IOException {
        socket = new Socket(HOST, PORT);
        Util.echo("Client", "Started");
    }

    public BIOClient(String HOST) throws IOException {
        socket = new Socket(HOST, PORT);
        Util.echo("Client", "Started");
    }

    public BIOClient(Socket socket){
        this.socket = socket;
        Util.echo("Client", "Started");
    }

    @Override
    public void run() {
        int modCount = 0;
        while(true) {
            try {
                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                pw.println("A msg from BIO client"+modCount++);
                pw.flush();
                Util.sleep(5000);
                Util.echo("Client:","Client has sent a msg");

//                客户端写入流不关闭，服务端就没法输出数据，一直阻塞
//                流关闭，会导致socket关闭
//                osw.close();
//                socket.shutdownOutput();

//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        InputStream in = null;
//                        try {
//                            in = socket.getInputStream();
//                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//                            String res = null;
//                            while((res=br.readLine())!="") {
//                                Util.echo("Client got a msg:", res);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();

            } catch (IOException e) {
                Util.echo("Client", "Crashed. IOException");
            }
        }
    }
}
