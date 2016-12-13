package com.ruoxu.update;

import android.os.Environment;

/**
 * Created by wangli on 16/12/12.
 */
public class Constants {

    public final static int MSG_DOWNLOAD_DONE = 0x00;
    public final static int MSG_DOWNLOAD_CANCEL = 0x01;
    public final static int MSG_UPDATE_PROGRESS = 0x02;

        public static final String SERVER_URL = "https://git.oschina.net/need88.com/TempFile/raw/master/index.html?dir=0&filepath=index.html&oid=d1483704a6b3794b579b2d5ba80876cf596082d8&sha=ac15e5ffcfd64d79d32ac5fa1fdfc8fa70de76a1";

    public static final String APK_NAME = "xxx.apk";//缓存apk文件的文件名
    public final static String save_path = Environment.getExternalStorageDirectory() + "/Download";


}
