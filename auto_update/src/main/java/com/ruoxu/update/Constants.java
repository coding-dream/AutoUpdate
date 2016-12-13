package com.ruoxu.update;

import android.os.Environment;

/**
 * Created by wangli on 16/12/12.
 */
public class Constants {

    public final static int MSG_DOWNLOAD_DONE = 0x00;
    public final static int MSG_DOWNLOAD_CANCEL = 0x01;
    public final static int MSG_UPDATE_PROGRESS = 0x02;

    public static final String SERVER_URL = "https://git.oschina.net/need88.com/TempFile/raw/master/update.html?dir=0&filepath=update.html&oid=51e5a3b8c304661a090f79fc64dcdb0528164acd&sha=9eccf2df1ac780005aebc668c9f899fff39f0467";

    public static final String APK_NAME = "xxx.apk";//缓存apk文件的文件名
    public final static String save_path = Environment.getExternalStorageDirectory() + "/Download";


}
