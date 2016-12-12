package com.ruoxu.update.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HttpUtil {

    private final static String TAG = HttpUtil.class.getSimpleName();
    private static HttpUtil instance;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public static HttpUtil getInstance(){
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) {
                    instance = new HttpUtil();
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
            StringBuffer params = new StringBuffer();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }

            params.append("delete");
            String postParams = params.toString();
            if (postParams.endsWith("&delete")) {
                postParams.replace("&delete", "");
            }


            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(api).openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);
                connection.setDoOutput(true);

                OutputStream out = connection.getOutputStream();
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
        }
    }
}
