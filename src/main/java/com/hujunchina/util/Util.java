package com.hujunchina.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class Util {
    public static void echo(String self, String msg){
        String time = new SimpleDateFormat("YYYY-MM-DD HH:mm:SSS").format(new Date());
        System.out.format("[%s] %s %s%n", time, self, msg);
    }

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
