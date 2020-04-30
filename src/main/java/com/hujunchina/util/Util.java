package com.hujunchina.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
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

    public static String analysisRequest(String req){
        String res = "";
        if(req.equals("time")){
            res = new Date().toString();
        }else if(req.equals("hujun")){
            res = "hujun love liujihong";
        }else if(req.equals("port")){
            res = "7250";
        }else if(req.equals("ip")){
            res = "47.114.146.180";
        }else if(req.equals("weather")){
            res = getWeather();
        }else{
            res = "please input: time | hujun | ip | port | weather";
        }
        return res;
    }

    /**
     * https://lbs.amap.com/api/webservice/guide/api/weatherinfo/
     * 使用高德 API
     * https://juejin.im/post/5dba8486f265da4d0a68d34e
     * http 请求使用 tools httpclient库
     * https://github.com/alibaba/fastjson/wiki/FastJson-%E6%96%87%E6%A1%A3%E9%93%BE%E6%8E%A5
     * json 使用 阿里的 fastjson
     * @return
     */
    public static String getWeather(){
        String key = "3bd0ddebd124f2ae9b7d7663d8517668";
        String city = "330110";
        String url = String.format("https://restapi.amap.com/v3/weather/weatherInfo?key=%s&city=%s", key, city);
        System.out.println(url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String resStr = response.body().string();

            JSONObject json = JSON.parseObject(resStr);
            JSONObject weather = json.getJSONArray("lives").getJSONObject(0);
            return weather.get("city")+" "+weather.get("weather")+" "+weather.get("temperature")+"° ";
        } catch (IOException e) {
            Util.echo("Util|>", "weather get failed.");
        }
        return "";
    }

}
