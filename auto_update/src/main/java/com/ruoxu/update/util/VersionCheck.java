package com.ruoxu.update.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.orhanobut.logger.Logger;
import com.ruoxu.update.VersionInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by wangli on 16/12/12.
 */
public class VersionCheck {
    private static final String DB_UPDATE = "update_info";

    public interface Callback{
        <T>void done(T t);

    }

    /**
     * 获取当前软件版本信息
     *
     * @param context
     * @return 版本信息
     */
    public static VersionInfo localVersion(Context context) {
        VersionInfo versionInfo = new VersionInfo();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionInfo.setVersionCode(pi.versionCode);
            versionInfo.setVersionName(pi.versionName);
            versionInfo.setDownloadUrl(null);
            versionInfo.setUpdateInfo(null);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.e("包名不存在");
            return null;
        }
        return versionInfo;
    }


    /**
     *
     * @param url
     * @return 获取服务器版本 ,注意当前线程在Thread中
     */
    public static void remoteVersion (String url, final Callback callback) {
        final VersionInfo versionInfo = new VersionInfo();

        HttpPost.getInstance().sendRequest(url, null, new HttpPost.Callback() {
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

                        callback.done(versionInfo);

                    } catch (JSONException jsonE) {
                        jsonE.printStackTrace();
                    }

                }
            }
        });

    }


        /**
         //	 * 检查是否有缓存更新文件
         //	 *
         //	 * @return 若返回false，说明没有缓存有效的更新文件，返回true说明有缓存更新文件
         //	 */
        public static void checkCache(Context context,Callback callback) {
            VersionInfo cacheVersion = new VersionInfo();
            SharedPreferences sp = context.getSharedPreferences(DB_UPDATE,Context.MODE_PRIVATE);

            cacheVersion.setVersionCode(sp.getInt("versionCode", 0));
            cacheVersion.setVersionName(sp.getString("versionName", null));
            cacheVersion.setUpdateInfo(sp.getString("updateInfo", null));

            if (localVersion(context).getVersionCode() >= cacheVersion.getVersionCode()) {
                Logger.i("当前版本比缓存版本更新");
                callback.done(null);
            } else{
                String savePath = sp.getString("savePath", null);
                if (savePath == null || !new File(savePath).exists()) {
                    Logger.i("缓存文件不存在");
                    callback.done(null);
                } else {
                    callback.done(savePath);
                }
            }

        }


        public static void checkRemote(final Context context, String url, final Callback callback){

            remoteVersion(url, new Callback() {
                @Override
                public <T> void done(T t) {
                    final VersionInfo remoteV = (VersionInfo) t;
                    VersionInfo localV = localVersion(context);
                    if (remoteV != null && localV != null) {
                        if (remoteV.getVersionCode() > localV.getVersionCode()) {
                            //有新版本了
                            updateCache(context, remoteV);

                            //切换线程
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.done(remoteV);
                                }
                            });

                        } else {
                            //暂无新版本
                            callback.done(null);
                        }
                    }

                }
            });
        }

    private static void updateCache(Context context, VersionInfo remoteV) {
		SharedPreferences sp = context.getSharedPreferences(DB_UPDATE,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

        editor.putInt("versionCode", remoteV.getVersionCode());
		editor.putString("versionName", remoteV.getVersionName());
		editor.putString("updateInfo", remoteV.getUpdateInfo());
		editor.apply();
    }

    public static void updateCacheFile(Context context, String path) {
        SharedPreferences sp = context.getSharedPreferences(DB_UPDATE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("savePath", path);
		editor.apply();
    }




}
