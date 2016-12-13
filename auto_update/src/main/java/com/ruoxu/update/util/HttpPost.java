package com.ruoxu.update.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public final class HttpPost {

    private final static String TAG = HttpPost.class.getSimpleName();
    private static HttpPost instance;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public static HttpPost getInstance(){
        if (instance == null) {
            synchronized (HttpPost.class) {
                if (instance == null) {
                    instance = new HttpPost();
                }
            }
        }
        return instance;
    }


    public interface Callback {

        Callback NONE = new Callback() {
            @Override
            public <T> void done(T ret, Exception e) {
                if(null != e)
                    throw new RuntimeException(e);
            }
        };

        <T> void done(T ret, Exception e);
    }



    public void sendRequest(final String url, final Map<String, String> params, final Callback callback){


        Map<String, String> req = new HashMap<>();
        if(null != params){
            req.putAll(params);
        }

//        req.put("xx", xx);

        this.call(url, req, callback);
    }

    private void call(final String api, final Map<String, String> map, final Callback callback) {

        executorService.submit(new CallTask(api, map, callback, 3));
    }


    private String readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();//网页的二进制数据
        outStream.close();
        inStream.close();
        return new String(data, "UTF-8");
    }

    class CallTask implements Runnable{

        final String api;
        final Map<String, String> map;
        final Callback callback;

        CallTask(final String api, final Map map, final Callback wrapcall, final int trytimes){
            this.api = api;
            this.map = map;

            callback = new Callback() {
                @Override
                public <T> void done(T ret, Exception e) {
                    if(trytimes>1 && null != e) {
                        Log.e(TAG, "call api - "+api+" failed: ", e);
                        executorService.submit(new CallTask(api, map, wrapcall, trytimes-1));
                    }else{
                        wrapcall.done(ret, e);
                    }
                }
            };

            Log.d(TAG, "call api - "+api+" "+trytimes+" times");
        }

        @Override
        public void run() {

//            ignoreHttps(); //忽略https安全连接

            StringBuffer params = new StringBuffer();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                String content = readInputStream(connection.getInputStream());
                callback.done(content,null);

            } catch (Exception e) {
                callback.done(null, e);
            }
        }
    }



    public void ignoreHttps(){
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){

            public java.security.cert.X509Certificate[] getAcceptedIssuers(){return null;}

            public void checkClientTrusted(X509Certificate[] certs, String authType){}

            public void checkServerTrusted(X509Certificate[] certs, String authType){}

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {

            }
            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {

            }
        }};


        try {
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());//忽略掉自签名的网络安全连接(https)

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /** Post请求
    StringBuffer params = new StringBuffer();

    for (Map.Entry<String, String> entry : map.entrySet()) {
        params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
    }

    try {
        HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(1000 * 30);
        connection.setReadTimeout(1000 * 30);
        connection.setDoOutput(true);

        OutputStream out = connection.getOutputStream();

        String content = readInputStream(connection.getInputStream());
        callback.done(content,null);

        if (out != null) {
            out.write(postParams.getBytes());

            String content = readInputStream(connection.getInputStream());
            callback.done(content,null);
        } else {
            callback.done(null, new RuntimeException("httpURLConnection outputstream is null"));
        }

    } catch (Exception e) {
        callback.done(null, e);
    }

     */

}
