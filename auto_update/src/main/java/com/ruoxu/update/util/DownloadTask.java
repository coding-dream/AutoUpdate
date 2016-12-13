package com.ruoxu.update.util;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangli on 16/12/12.
 */
public class DownloadTask {

    private long mFinished = 0;  //记录下载过的 进度
    private String sUrl;
    private File apkFile;
    private Callback callback;

    //线程池
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public DownloadTask(String url, File apkFile){
        this.sUrl = url;
        this.apkFile  = apkFile;
    }



    public interface Callback {
        int MSG_START = 0;
        int MSG_UPDATE = 1;
        int MSG_END = 2;
        int MSG_ERROR = 3;

        <T>void event(int what,T msg);
    }


    /**
     * 下载方法
     */
    public void download(Callback callback) {
        // 启动线程，开始下载
        this.callback = callback;
        DownloadThread thread = new DownloadThread();
        sExecutorService.execute(thread);
    }

    class DownloadThread implements Runnable {

        @Override
        public void run() {

            HttpURLConnection conn = null;

            FileOutputStream fileOutputStream = null;
            InputStream inputStream= null;
            Logger.i("==>Download url is "+sUrl);
            try {
                // 开启网络
                URL url = new URL(sUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                // 获取文件长度
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    callback.event(Callback.MSG_ERROR,"读取有误-1");
                    return;
                }

                Logger.i("length"+length);
                // 设置文件长度，并通知主线程
                callback.event(Callback.MSG_START,"初始化文件长度");

                // 设置写入位置
                fileOutputStream = new FileOutputStream(apkFile);

                // 读取数据
                inputStream = conn.getInputStream();
                byte[] buffer = new byte[1024 * 4];
                int len = -1;

                while ((len = inputStream.read(buffer)) != -1) {
                    // 写入文件
                    fileOutputStream.write(buffer, 0, len);
                    // 累加整个文件完成的进度
                    mFinished += len;  //  记录下载 过的进度

                    // 累加每个线程的完成进度
                    int progress = (int) (mFinished * 100 / length);

                    callback.event(Callback.MSG_UPDATE,progress);

                }

                //下载完毕

                callback.event(Callback.MSG_END,"下载完成");


            } catch (MalformedURLException e) {
                callback.event(Callback.MSG_ERROR,"URL解析出错");
            } catch (IOException e) {
                callback.event(Callback.MSG_ERROR,"下载出错IOExcetion");
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                IOUtils.close(inputStream);
                IOUtils.close(fileOutputStream);

            }






        }
    }



}
