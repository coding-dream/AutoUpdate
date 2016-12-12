package com.ruoxu.update.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ruoxu.update.VersionInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wangli on 16/12/12.
 */
public class VersionCheck {
    private final String tag = getClass().getSimpleName();


    /**
     * 获取当前软件版本信息
     *
     * @param context
     * @return 版本信息
     */
    public VersionInfo localVersion(Context context) {
        VersionInfo versionInfo = new VersionInfo();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionInfo.setVersionCode(pi.versionCode);
            versionInfo.setVersionName(pi.versionName);
            versionInfo.setDownloadUrl(null);
            versionInfo.setUpdateInfo(null);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(tag, "包名不存在");
            return null;
        }
        return versionInfo;
    }


    /**
     *
     * @param url
     * @return 获取服务器版本
     */
    public VersionInfo remoteVersion (String url) {
        final VersionInfo versionInfo = new VersionInfo();

        HttpUtil.getInstance().sendRequest(url, null, new HttpUtil.Callback() {
            @Override
            public <T> void done(T ret, Exception e) {
                if (ret != null) {
                    String content = (String) ret;
                    try {
                        JSONObject object = new JSONObject(content);
                        String versionName = object.getString("versionName");
                        int versionCode = object.getInt("versionCode");
                        String downUrl = object.getString("downUrl");
                        String updateInfo = object.getString("updateInfo");
                        versionInfo.setVersionName(versionName);
                        versionInfo.setVersionCode(versionCode);
                        versionInfo.setDownloadUrl(downUrl);
                        versionInfo.setUpdateInfo(updateInfo);

                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }

                }
            }
        });

        return versionInfo;
    }



}
