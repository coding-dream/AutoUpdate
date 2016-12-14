package com.ruoxu.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "还未下载完成，请稍等片刻", Toast.LENGTH_SHORT).show();

    }
}
