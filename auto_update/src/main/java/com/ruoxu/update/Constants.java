package com.ruoxu.update;

import android.os.Environment;

/**
 * Created by wangli on 16/12/12.
 */
public class Constants {

    public final static int MSG_DOWNLOAD_DONE = 0x00;
    public final static int MSG_DOWNLOAD_CANCEL = 0x01;
    public final static int MSG_UPDATE_PROGRESS = 0x02;


    public final static String save_path = Environment.getExternalStorageDirectory() + "/Download";


    public static final String SERVER_URL = "http://test.com";
    public static final String APK_NAME = "xxx.apk";

}
