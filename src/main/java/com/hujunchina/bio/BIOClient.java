package com.hujunchina.bio;

import com.hujunchina.util.Util;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

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

    private static class ReceiveHandler implements Runnable{
        private Socket socket = null;
        public ReceiveHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                InputStream in = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String res = null;
                while ((res = br.readLine()) != "") {
                    Util.echo("Client got a msg:", res);
                }
            }catch (Exception e){
                System.out.println("Client receive handler crashed.");
            }
        }
    }

    @Override
    public void run() {
        int modCount = 0;
        Scanner in = new Scanner(System.in);
        System.out.println("Please input some msg to send, using q or Q to quit");
        Thread t = new Thread(new ReceiveHandler(socket));
        t.start();
        while(true) {
            try {
                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                if(modCount==0){
                    pw.println("A msg from BIO client"+modCount++);
                }else{
                    String msg = in.nextLine();
                    if(msg.equals("q") || msg.equals("Q")){
                        System.out.println("Exit client.");
                        break;
                    }
                    pw.println(msg);
                    modCount++;
                }
                pw.flush();  // 刷新缓冲区，从近处缓冲区到内核缓冲区，然后到网卡发送出去。
                Util.sleep(500);
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
