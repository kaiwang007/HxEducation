package com.lefeee.hxeducation;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by wei.cui on 2018/6/20.
 */

public class ApiCall {

    static String TAG = "ApiCall";

    private String SITE = "http://18t4h38845.51mypc.cn:8053/fn/";
    private String Test_SITE = "http://2q609240d3.zicp.vip:50879/tmla2/";

    private HttpURLConnection connection = null;

    public String CheckUser(String user, String psw) {
        String target = "tea/teacheraccount/teaphonelogin";
        String ret;
        String data = "?username=" + user + "&password=" + psw;
        ret = exc_get(target, data);
        Log.d(TAG, "CheckUser ret:" + ret);

        return ret;
    }

    public String ImagePost(String raw) {
        String target = "ImagePost/StImagePost";
        String ret = "";
        String urlRaw = null;
        try {
            urlRaw = URLEncoder.encode(raw, "UTF-8");
            Log.d(TAG, "urlRaw: " + urlRaw);
            ret = exc_post(target, "imageD64=" + raw);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "ImagePost ret:" + ret);
        return ret;
    }

    public String WorkListGet(int teaAccId) {
        String target = "tea/zxgrade/StWorkPost";
        String ret;
        String data = "?teaAccId=" + teaAccId;
        ret = exc_get(target, data);
        Log.d(TAG, "WorkListGet ret:" + ret);
        return ret;
    }

    private String exc_get(String target, String data) {

        int req_code;
        try {
            URL url = new URL(SITE + target + data);
            Log.d(TAG, "exc_get url: " + url);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");

            connection.setRequestProperty("Content-length", "0");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(2 * 1000);
            connection.setReadTimeout(2 * 1000);

            req_code = connection.getResponseCode();

            Log.d(TAG, "url: " + url.toString());

            Log.d(TAG, "rec: " + req_code);

            if (HTTP_OK == req_code) {
                StringBuffer stringBuffer = new StringBuffer();
                String readline;
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readline = bufferedReader.readLine()) != null) {
                    //stringBuffer.append(readline).append("\n");
                    stringBuffer.append(readline);
                }
                bufferedReader.close();
                return stringBuffer.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String exc_post(String target, String Json) {
        String result = null;
        int req_code=0;

        try {
            URL url = new URL(SITE + target);
            Log.d(TAG, "exc_post url: " + url);

            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(20 * 1000);
            connection.setReadTimeout(20 * 1000);

            connection.setRequestMethod("POST");

            if (Json != null && !TextUtils.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                Log.d(TAG, "writebytes length: " + writebytes.length);
                //connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));

                OutputStream outwritestream = connection.getOutputStream();
                if (outwritestream != null){
                    Log.d(TAG, "outwritestream success");
                }else{
                    Log.d(TAG, "outwritestream fail");
                }
                outwritestream.write(writebytes);
                outwritestream.flush();
                outwritestream.close();
            }

            req_code = connection.getResponseCode();

            Log.d(TAG, "exc_post req: " + req_code);
            if (HTTP_OK == req_code) {
                StringBuffer stringBuffer = new StringBuffer();
                String readline;
                BufferedReader bufferedReader;
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((readline = bufferedReader.readLine()) != null) {
                    //stringBuffer.append(readline).append("\n");
                    stringBuffer.append(readline);
                }
                bufferedReader.close();
                return stringBuffer.toString();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
