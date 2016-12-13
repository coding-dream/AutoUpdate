package com.ruoxu.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.ruoxu.update.util.VersionCheck;

import java.io.File;

/**
 * Created by wangli on 16/12/9.
 */
public class UpdateAgent {


    static UpdateAgent instance;
    private String apkName = "";

	NotificationCompat.Builder mBuilder;
	NotificationManager mNotificationManager;

    int mUpdatingIconId = android.R.drawable.stat_sys_download;
    int mUpdateFinishIconId = android.R.drawable.stat_sys_download_done;

    public static UpdateAgent getInstance(){
        if (instance == null) {
            synchronized (UpdateAgent.class) {
                if (instance == null) {
                    instance = new UpdateAgent();
                }
            }
        }
        return instance;
    }

	public static void update(Context context){
		update(context,Constants.SERVER_URL);
	}

    public static void update(final Context context, final String url){
        // 先检查是否缓存过更新信息

		VersionCheck.checkCache(context, new VersionCheck.Callback() {
			@Override
			public <T> void done(T cachePath) {
				if (cachePath != null) {
					//存在缓存，且比当前版本 新
					doInstall(context, (String)cachePath);


				} else {
					//无缓存，需联网对比版本后，根据对比结果决定是否下载
					VersionCheck.checkRemote(context, url, new VersionCheck.Callback() {
						@Override
						public <T> void done(T t) {
							VersionInfo remoteVersion= (VersionInfo) t;
							if (null!=remoteVersion) {
								Toast.makeText(context, "检测到新版本", Toast.LENGTH_SHORT).show();
								showDialog(context,remoteVersion);
							} else {
								Toast.makeText(context, "暂无新版", Toast.LENGTH_SHORT).show();
							}

						}
					});

				}
			}

		});



    }



	public void downloadApk(Context context, String downloadUrl){


		// 新建文件夹
		File fp = new File(Constants.save_path);
		if (!fp.exists()) {
			fp.mkdir();
		}

		mNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

		// 初始化通知栏
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setTicker("开始下载更新")
				.setSmallIcon(mUpdatingIconId)
				.setContentIntent(getDefalutIntent(context, Notification.FLAG_NO_CLEAR| PendingIntent.FLAG_ONE_SHOT))
				.setDefaults(Notification.DEFAULT_SOUND).setContentTitle("更新")
				.setPriority(Notification.PRIORITY_DEFAULT).setOngoing(true);// 设置通知小ICON

		Notification notification = mBuilder.build();

		mNotificationManager.notify(1, notification);

		// 启动下载服务
		Intent service = new Intent(context, UpdateService.class);
		service.putExtra("url", downloadUrl);
		context.startService(service);

	}








	//==========》Service中处理
    public void updateFinish(Context applicationContext) {
		mBuilder.setProgress(0, 0, false)
				.setContentTitle("更新完成")
				.setContentText("点击安装")
				.setOngoing(false)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setContentIntent(
						getInstallIntent(applicationContext, Notification.FLAG_AUTO_CANCEL
								| PendingIntent.FLAG_ONE_SHOT))
				.setSmallIcon(mUpdateFinishIconId);
		Notification notification = mBuilder.build();
		mNotificationManager.notify(1, notification);

		// 更新缓存信息
		VersionCheck.updateCacheFile(applicationContext,Constants.save_path+File.separator+apkName);

    }

    public void updateCancel() {
        Logger.i("取消下载");
        mNotificationManager.cancelAll();

    }

    public void updateNotificationPorgress(Integer progress) {
        mBuilder.setProgress(100, progress, false).setDefaults(0);
		Notification notification = mBuilder.build();
//
		mNotificationManager.notify(1, notification);
    }
    //==========》Service中处理





    private PendingIntent getDefalutIntent(Context context, int flags) {
		Intent intent = new Intent(context, UpdateReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, flags);
		return pendingIntent;
	}

	private PendingIntent getInstallIntent(Context context, int flags) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Constants.save_path + "/" + apkName)), "application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, flags);
		return pendingIntent;
	}



	private static void doInstall(Context context,String savePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(savePath)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	private static void showDialog(final Context context, final VersionInfo remoteVersion) {
		new AlertDialog.Builder(context)
				.setTitle("更新")
				.setMessage(remoteVersion.getUpdateInfo())
				.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 开启下载，此方法在通知栏弹出消息题型
						UpdateAgent.getInstance().downloadApk(context, remoteVersion.getDownloadUrl());
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Logger.i("取消更新");
					}
				}).create();

	}





}
