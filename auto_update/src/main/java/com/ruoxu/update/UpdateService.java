package com.ruoxu.update;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.ruoxu.update.util.DownloadTask;

import java.io.File;

public class UpdateService extends Service {

    public final static String tag = UpdateService.class.getSimpleName();



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_DOWNLOAD_DONE:
                    UpdateAgent.getInstance().updateFinish(getApplicationContext());
                    stopSelf();
                    break;
                case Constants.MSG_DOWNLOAD_CANCEL:
                    UpdateAgent.getInstance().updateCancel((Integer) msg.obj);
                    stopSelf();
                    break;
                case Constants.MSG_UPDATE_PROGRESS:
                    UpdateAgent.getInstance().updateNotificationPorgress((Integer) msg.obj);
                    break;
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(tag, "onCreate");
        super.onCreate();

        String apkName = "xx";
        String url = "";
        File apkFile = new File(Constants.save_path,apkName);
        DownloadTask downloadTask = new DownloadTask(url,apkFile);
        downloadTask.download(new DownloadTask.Callback() {
            @Override
            public <T> void event(int what, T msg) {
                switch (what) {
                    case DownloadTask.Callback.MSG_START:

                        break;
                    case DownloadTask.Callback.MSG_UPDATE:
                        int progress = (Integer)msg;


                        break;
                    case DownloadTask.Callback.MSG_END:

                        break;

                }
            }
        });

        //开始下载
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String apkName  = "xxx";
                    File apkFile = new File(Constants.save_path,apkName);





                    // 更新结束,更新通知栏并且结束更新服务
                    handler.sendEmptyMessage(Constants.MSG_DOWNLOAD_DONE);
                }

            }

        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUrl = intent.getStringExtra("url");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "onDestroy");
        super.onDestroy();
    }



}
